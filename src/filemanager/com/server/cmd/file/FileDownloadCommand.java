package filemanager.com.server.cmd.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.AuthCommand;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Environments;
import filemanager.com.server.common.Utils;
import filemanager.com.server.exception.FileNotFoundException;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.InvalidPathException;
import filemanager.com.server.exception.NoPermissionException;
import filemanager.com.server.exception.NotLoggedInException;
import filemanager.com.server.exception.ServerException;

public class FileDownloadCommand extends AuthCommand {
	private static final Logger LOGGER = LogManager.getLogger(FileDownloadCommand.class);
	
	private Path source;

	@Override
	public boolean validate() throws ServerException {
		LOGGER.info("{}: validate command - {}", this.remoteAddress, Constants.FILE_DOWNLOAD_CMD);
		
		this.username = Utils.getCurrentUsername(this.remoteAddress);

		int[] validNumberOfArgs = {1, 2};
		if(!Validator.validateNumberOfArgs(this.args, validNumberOfArgs)) {
			throw new InvalidNumberOfArgsException(validNumberOfArgs, this.args.size());
		}
		
		if (!Authentication.accIsLoging(this.username)) {
			throw new NotLoggedInException();
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
		
		if (!Files.exists(this.source)) {
			throw new FileNotFoundException(tempSource);
		}
		
		return true;
	}

	@Override
	public String exec() throws ServerException {
		LOGGER.info("{}: exec command - {}", this.remoteAddress, Constants.FILE_DOWNLOAD_CMD);
		
		try (FileChannel downloadChannel = FileChannel.open(this.source, StandardOpenOption.READ)) {
			ByteBuffer downloadBuffer = ByteBuffer.allocate((int) downloadChannel.size());
			
			downloadChannel.read(downloadBuffer);
			downloadBuffer.flip();
			socketChannel.write(downloadBuffer);
			
			LOGGER.info("{}: file {} is downloaded", this.remoteAddress, source.toString());
			return String.format("File %s is downloaded", this.source.getFileName());
		}catch (Exception e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new ServerException();
		}
	}

}
