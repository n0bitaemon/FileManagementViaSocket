package filemanager.com.server.cmd.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import filemanager.com.server.cmd.AuthCommand;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Environments;
import filemanager.com.server.common.Utils;
import filemanager.com.server.exception.DirectoryNotFoundException;
import filemanager.com.server.exception.FileNotFoundException;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.InvalidPathException;
import filemanager.com.server.exception.NoPermissionException;
import filemanager.com.server.exception.NotLoggedInException;
import filemanager.com.server.exception.ServerException;

/**
 * Usage: Copy file from source path to destination path <br>
 * Syntax: copy {@literal <}source> {@literal <}dest> <br>
 * Arguments: <br>
 * - source: existed file/folder on user's server storage <br>
 * - dest: new file/folder on user's server storage
 * 
 * @author Triet
 *
 */
public class FileCopyCommand extends AuthCommand {
	private static final Logger LOGGER = LogManager.getLogger(FileCopyCommand.class);
	
	private Path source;
	private Path dest;

	@Override
	public boolean validate() throws ServerException {
		LOGGER.info("{}: validate command - {}", this.remoteAddress, Constants.FILE_COPY_CMD);

		if(!isAuthenticated()) {
			throw new NotLoggedInException();
		}

		if (!Validator.validateNumberOfArgs(this.args, 2)) {
			throw new InvalidNumberOfArgsException(2, this.args.size());
		}
		
		// Set temporary file path by user input
		String tempSource = this.args.get(0);
		String tempDest = this.args.get(1);

		// Set canonical old file path
		String canonicalSourceStr;
		try {
			canonicalSourceStr = Utils.getCanonicalFilePath(tempSource, this.username);
		} catch (IOException e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new InvalidPathException(tempSource);
		}

		// Set canonical new file path
		String canonicalDestStr;
		try {
			canonicalDestStr = Utils.getCanonicalFilePath(tempDest, this.username);
		} catch (IOException e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new InvalidPathException(tempDest);
		}

		// Check permission of user
		if (!Validator.checkPermission(canonicalSourceStr, this.username)) {
			throw new NoPermissionException(tempSource);
		}
		if (!Validator.checkPermission(canonicalDestStr, this.username)) {
			throw new NoPermissionException(tempDest);
		}

		// Set up valid path property
		Path canonicalSourceFile = Paths.get(canonicalSourceStr);
		Path canonicalDestFile = Paths.get(canonicalDestStr);
		this.source = canonicalSourceFile;
		this.dest = canonicalDestFile;

		if (!Files.exists(this.source)) {
			throw new FileNotFoundException(tempSource);
		}

		if (Files.exists(this.dest)) {
			throw new FileNotFoundException(tempDest);
		}

		return true;
	}

	@Override
	public String exec() throws ServerException {
		LOGGER.info("{}: exec command - {}", this.remoteAddress, Constants.FILE_COPY_CMD);
		
		try {
			// If directory not exist, make dir
			Path parentFolder = this.dest.getParent();
			if (!Files.exists(parentFolder)) {
				throw new DirectoryNotFoundException(parentFolder.toString());
			}

			Files.copy(this.source, this.dest);
		} catch (IOException e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new ServerException();
		}

		String relativeSourceStr = this.source.toString().replace(Utils.getUserDir(this.username), "");
		String relativeDestStr = this.dest.toString().replace(Utils.getUserDir(this.username), "");
		return String.format("File copied: %s => %s", relativeSourceStr, relativeDestStr);
	}

}
