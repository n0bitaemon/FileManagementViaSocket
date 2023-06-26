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

import filemanager.com.server.auth.Authentication;
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

public class ListFileCommand extends AuthCommand {
	private static final Logger LOGGER = LogManager.getLogger(ListFileCommand.class);
	
	private Path path;

	public Path getPath() {
		return this.path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	@Override
	public boolean validate() throws ServerException {
		LOGGER.info("{}: validate command - {}", getRemoteAddress(), Constants.DIR_LIST_FILE_CMD);
		
		setUsername(Utils.getCurrentUsername(getRemoteAddress()));

		if (!Validator.validateNumberOfArgs(getArgs(), 1) && !Validator.validateNumberOfArgs(getArgs(), 0)) {
			int[] validNumberOfArguments = { 0, 1 };
			throw new InvalidNumberOfArgsException(validNumberOfArguments, getArgs().size());
		}

		if (!Authentication.accIsLoging(getUsername())) {
			throw new NotLoggedInException();
		}

		// Set temporary file path by user input
		String tempPath;
		if (getArgs().size() == 1) {
			tempPath = getArgs().get(0);
		} else {
			tempPath = "/";
		}

		// Set canonical file path
		String canonicalFilePath;
		try {
			canonicalFilePath = Utils.getCanonicalFilePath(tempPath, getUsername());
		} catch (IOException e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new InvalidPathException(tempPath);
		}

		// Check permission of user
		if (!Validator.checkPermission(canonicalFilePath, getUsername())) {
			throw new NoPermissionException(tempPath);
		}

		// Set up valid path property
		Path canonicalFile = Paths.get(canonicalFilePath);
		setPath(canonicalFile);

		if (!Files.exists(getPath())) {
			throw new DirectoryNotFoundException(tempPath);
		}

		if (!Files.isDirectory(getPath())) {
			throw new NotADirectoryException(tempPath);
		}
		return true;
	}

	@Override
	public String exec() throws ServerException {
		LOGGER.info("{}: exec command - {}", getRemoteAddress(), Constants.DIR_LIST_FILE_CMD);

		StringBuilder filesResponse = new StringBuilder();

		List<Path> files = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(getPath());) {
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

		for (Path entry : files) {
			filesResponse.append(files.indexOf(entry) == files.size() - 1 ? "└─ " : "├─ ");
			filesResponse.append(entry.getFileName().toString());
			filesResponse.append("\n");
		}

		return filesResponse.toString();
	}

}
