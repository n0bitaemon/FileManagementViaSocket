package filemanager.com.server.cmd.file;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import filemanager.com.server.cmd.AuthCommand;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Environments;
import filemanager.com.server.common.Utils;
import filemanager.com.server.exception.DirectoryNotFoundException;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.InvalidPathException;
import filemanager.com.server.exception.NoPermissionException;
import filemanager.com.server.exception.NotADirectoryException;
import filemanager.com.server.exception.NotLoggedInException;
import filemanager.com.server.exception.ServerException;

/**
 * Usage: List all files/subfolder in a folder <br>
 * Syntax: ls [{@literal <}path>] <br>
 * Arguments:
 * - path (optional): path on user's server storage
 * @author n0bita-windows
 *
 */
public class ListFileCommand extends AuthCommand {
	private static final Logger LOGGER = LogManager.getLogger(ListFileCommand.class);
	
	private Path path;

	@Override
	public boolean validate() throws ServerException {
		LOGGER.info("{}: validate command - {}", this.remoteAddress, Constants.DIR_LIST_FILE_CMD);

		if(!isAuthenticated()) {
			throw new NotLoggedInException();
		}

		int[] validNumberOfArgs = {0, 1};
		if (!Validator.validateNumberOfArgs(this.args, validNumberOfArgs)) {
			throw new InvalidNumberOfArgsException(validNumberOfArgs, this.args.size());
		}

		// Set temporary file path by user input
		String tempPath;
		if (this.args.size() == 1) {
			tempPath = this.args.get(0);
		} else {
			tempPath = "/";
		}

		// Set canonical file path
		String canonicalFilePath;
		try {
			canonicalFilePath = Utils.getCanonicalFilePath(tempPath, this.username);
		} catch (IOException e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new InvalidPathException(tempPath);
		}

		// Check permission of user
		if (!Validator.checkPermission(canonicalFilePath, this.username)) {
			throw new NoPermissionException(tempPath);
		}

		// Set up valid path property
		Path canonicalFile = Paths.get(canonicalFilePath);
		this.path = canonicalFile;

		if (!Files.exists(this.path)) {
			throw new DirectoryNotFoundException(tempPath);
		}

		if (!Files.isDirectory(this.path)) {
			throw new NotADirectoryException(tempPath);
		}
		return true;
	}

	@Override
	public String exec() throws ServerException {
		LOGGER.info("{}: exec command - {}", this.remoteAddress, Constants.DIR_LIST_FILE_CMD);

		StringBuilder filesResponse = new StringBuilder();

		List<Path> files = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.path);) {
			for (Path entry : stream) {
				files.add(entry);
			}
		} catch (IOException e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new ServerException();
		}

		if (files.isEmpty()) {
			filesResponse.append("");
			return filesResponse.toString();
		}

		filesResponse.append("\n");
		for (Path entry : files) {
			filesResponse.append(files.indexOf(entry) == files.size() - 1 ? "└─ " : "├─ ");
			filesResponse.append(entry.getFileName().toString());
			filesResponse.append("\n");
		}

		return filesResponse.toString();
	}

}
