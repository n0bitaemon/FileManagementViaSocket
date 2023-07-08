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
import filemanager.com.server.exception.FileAlreadyExistException;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.InvalidPathException;
import filemanager.com.server.exception.NoPermissionException;
import filemanager.com.server.exception.NotLoggedInException;
import filemanager.com.server.exception.ServerException;

/**
 * Usage: Create new directory <br>
 * Syntax: mkdir {@literal <}dirname> <br>
 * Arguments: <br>
 * - dirname: new directory name
 * 
 * @author Triet
 *
 */
public class MakeDirCommand extends AuthCommand {
	private static final Logger LOGGER = LogManager.getLogger(MakeDirCommand.class);

	private Path path;

	@Override
	public boolean validate() throws ServerException {
		LOGGER.info("{}: validate command - {}", this.remoteAddress, Constants.DIR_MAKE_CMD);

		if(!isAuthenticated()) {
			throw new NotLoggedInException();
		}

		if (!Validator.validateNumberOfArgs(this.args, 1)) {
			throw new InvalidNumberOfArgsException(1, this.args.size());
		}

		// Set temporary file path by user input
		String tempPath = this.args.get(0);

		// Set canonical file path
		String canonicalPath;
		try {
			canonicalPath = Utils.getCanonicalFilePath(tempPath, this.username);
		} catch (IOException e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new InvalidPathException(tempPath);
		}

		// Check permission of user
		if (!Validator.checkPermission(canonicalPath, this.username)) {
			throw new NoPermissionException(tempPath);
		}

		// Set up valid path property
		Path canonicalFolder = Paths.get(canonicalPath);
		this.path = canonicalFolder;

		if (Files.exists(this.path)) {
			throw new FileAlreadyExistException(tempPath);
		}
		return true;
	}

	@Override
	public String exec() throws ServerException {
		LOGGER.info("{}: exec command - {}", this.remoteAddress, Constants.DIR_MAKE_CMD);

		try {
			Files.createDirectories(this.path);
		} catch (IOException e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new ServerException();
		}
		return String.format("Created folder: %s", this.path.getFileName());
	}

}
