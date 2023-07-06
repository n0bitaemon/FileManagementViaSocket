package filemanager.com.server.cmd.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import filemanager.com.server.TFTPUtils;
import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.AuthCommand;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Environments;
import filemanager.com.server.common.Utils;
import filemanager.com.server.exception.FileAlreadyExistException;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.InvalidPathException;
import filemanager.com.server.exception.NoPermissionException;
import filemanager.com.server.exception.NotLoggedInException;
import filemanager.com.server.exception.ServerException;

public class FileUploadCommand extends AuthCommand {
	private static final Logger LOGGER = LogManager.getLogger(FileDownloadCommand.class);

	private Path dest;

	@Override
	public boolean validate() throws ServerException {
		LOGGER.info("{}: validate command - {}", this.remoteAddress, Constants.FILE_UPLOAD_CMD);
		
		this.username = Utils.getCurrentUsername(this.remoteAddress);

		int[] validNumberOfArgs = {1, 2};
		if(!Validator.validateNumberOfArgs(this.args, validNumberOfArgs)) {
			throw new InvalidNumberOfArgsException(validNumberOfArgs, this.args.size());
		}
		
		if (!Authentication.accIsLoging(this.username)) {
			throw new NotLoggedInException();
		}
		
		// Set temporary file path by user input
		String tempDest = this.args.get(1);
		
		// Set canonical old file path
		String canonicalDest;
		try {
			canonicalDest = Utils.getCanonicalFilePath(tempDest, this.username);
		} catch (IOException e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new InvalidPathException(tempDest);
		}

		// Check permission of user
		if (!Validator.checkPermission(canonicalDest, this.username)) {
			throw new NoPermissionException(tempDest);
		}
		
		// Set up valid path property
		Path canonicalSourceFile = Paths.get(canonicalDest);
		this.dest = canonicalSourceFile;
		
		System.out.println("DEST: " + this.dest);
		
		if (Files.exists(this.dest)) {
			throw new FileAlreadyExistException(tempDest);
		}
		
		return true;
	}

	@Override
	public String exec() throws ServerException {
		LOGGER.info("{}: exec command - {}", this.remoteAddress, Constants.FILE_UPLOAD_CMD);

		SocketChannel socketChannel = (SocketChannel) this.key.channel();
		
		ByteBuffer tftpBuffer = ByteBuffer.allocate(TFTPUtils.BUFSIZE);
		
		try {
			// Send validation success status
			TFTPUtils.sendSuccessStatus(socketChannel, tftpBuffer);
			
			// Send RRQ packet
			TFTPUtils.sendRRQPacket(this.dest.toString(), socketChannel, tftpBuffer);
			
			// Receive file
			TFTPUtils.receiveFile(dest, socketChannel, tftpBuffer);
			
		} catch (IOException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new ServerException();
		}
		
		return String.format("File %s is uploaded", this.dest.getFileName().toString());
	}

}
