package filemanager.com.server.cmd.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import filemanager.com.server.auth.Authentication;
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

public class FileCopyCommand extends AuthCommand {
	private static final Logger LOGGER = LogManager.getLogger(FileCopyCommand.class);

	private Path oldPath;
	private Path newPath;

	@Override
	public boolean validate() throws ServerException {
		LOGGER.info("{}: validate command - {}", this.remoteAddress, Constants.FILE_COPY_CMD);

		setUsername(Utils.getCurrentUsername(this.remoteAddress));

		if (!Validator.validateNumberOfArgs(this.args, 2)) {
			throw new InvalidNumberOfArgsException(2, this.args.size());
		}

		if (!Authentication.accIsLoging(getUsername())) {
			throw new NotLoggedInException();
		}

		// Set temporary file path by user input
		String tempOldPath = this.args.get(0);
		String tempNewPath = this.args.get(1);

		// Set canonical old file path
		String canonicalOldFilePath;
		try {
			canonicalOldFilePath = Utils.getCanonicalFilePath(tempOldPath, getUsername());
		} catch (IOException e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new InvalidPathException(tempOldPath);
		}

		// Set canonical new file path
		String canonicalNewFilePath;
		try {
			canonicalNewFilePath = Utils.getCanonicalFilePath(tempNewPath, getUsername());
		} catch (IOException e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new InvalidPathException(tempNewPath);
		}

		// Check permission of user
		if (!Validator.checkPermission(canonicalOldFilePath, getUsername())) {
			throw new NoPermissionException(tempOldPath);
		}
		if (!Validator.checkPermission(canonicalNewFilePath, getUsername())) {
			throw new NoPermissionException(tempNewPath);
		}

		// Set up valid path property
		Path canonicalOldFile = Paths.get(canonicalOldFilePath);
		Path canonicalNewFile = Paths.get(canonicalNewFilePath);
		this.oldPath = canonicalOldFile;
		this.newPath = canonicalNewFile;

		if (!Files.exists(this.oldPath)) {
			throw new FileNotFoundException(tempOldPath);
		}

		if (Files.exists(this.newPath)) {
			throw new FileNotFoundException(tempNewPath);
		}

		return true;
	}

	@Override
	public String exec() throws ServerException {
		LOGGER.info("{}: exec command - {}", this.remoteAddress, Constants.FILE_COPY_CMD);
		
		try {
			// If directory not exist, make dir
			Path parentFolder = this.newPath.getParent();
			if (!Files.exists(parentFolder)) {
				Files.createDirectories(parentFolder);
			}

			Files.copy(this.oldPath, this.newPath);
		} catch (IOException e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new ServerException();
		}

		String relativeOldPath = this.oldPath.toString().replace(Utils.getUserDir(getUsername()), "");
		String relativeNewPath = this.newPath.toString().replace(Utils.getUserDir(getUsername()), "");
		return String.format("File copied: %s => %s", relativeOldPath, relativeNewPath);
	}

}
