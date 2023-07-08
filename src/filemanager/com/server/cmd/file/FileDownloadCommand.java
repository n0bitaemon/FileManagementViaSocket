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
import filemanager.com.server.cmd.AuthCommand;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Environments;
import filemanager.com.server.common.Utils;
import filemanager.com.server.exception.FileNotFoundException;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.InvalidPathException;
import filemanager.com.server.exception.NoPermissionException;
import filemanager.com.server.exception.NotAFileException;
import filemanager.com.server.exception.NotLoggedInException;
import filemanager.com.server.exception.ServerException;

public class FileDownloadCommand extends AuthCommand {
	private static final Logger LOGGER = LogManager.getLogger(FileDownloadCommand.class);
	
	private Path source;

	@Override
	public boolean validate() throws ServerException {
		LOGGER.info("{}: validate command - {}", this.remoteAddress, Constants.FILE_DOWNLOAD_CMD);

		if(!isAuthenticated()) {
			throw new NotLoggedInException();
		}
		
		if(!Validator.validateNumberOfArgs(this.args, 2)) {
			throw new InvalidNumberOfArgsException(2, this.args.size());
		}
		
		// Set temporary file path by user input
		String tempSource = this.args.get(0);
		
		// Set canonical old file path
		String canonicalSource;
		try {
			canonicalSource = Utils.getCanonicalFilePath(tempSource, this.username);
		} catch (IOException e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new InvalidPathException(tempSource);
		}

		// Check permission of user
		if (!Validator.checkPermission(canonicalSource, this.username)) {
			throw new NoPermissionException(tempSource);
		}
		
		// Set up valid path property
		Path canonicalSourceFile = Paths.get(canonicalSource);
		this.source = canonicalSourceFile;

		if(!Files.isRegularFile(this.source)) {
			throw new NotAFileException(tempSource);
		}
		
		if (!Files.exists(this.source)) {
			throw new FileNotFoundException(tempSource);
		}
		
		return true;
	}
	

	@Override
	public String exec() throws ServerException {
		LOGGER.info("{}: exec command - {}", this.remoteAddress, Constants.FILE_DOWNLOAD_CMD);
		SocketChannel socketChannel = (SocketChannel) this.key.channel();
		ByteBuffer tftpBuffer = ByteBuffer.allocate(516);
		
		try {
			int numBytes;

			// Send file size back
			TFTPUtils.sendFileSize(source, socketChannel, tftpBuffer);
			
			// Receive RRQ packet
			tftpBuffer.clear();
			do {
				numBytes = socketChannel.read(tftpBuffer);
			} while(numBytes <= 0);
			if(!TFTPUtils.checkPacket(tftpBuffer, TFTPUtils.OP_RRQ)) {
				throw new ServerException();
			}

			// Start sending file
			if(!TFTPUtils.sendFile(this.source.toFile(), socketChannel, tftpBuffer)){
				throw new ServerException();
			}
		} catch (Exception e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			LOGGER.error("{}: Error while downloading file", this.remoteAddress);
			throw new ServerException();
		}
		
		LOGGER.info("{}: file {} is downloaded", this.remoteAddress, source.toString());
		return String.format("%s: File \"%s\" is downloaded", this.remoteAddress.toString(), this.source.getFileName().toString());	
	}

}
