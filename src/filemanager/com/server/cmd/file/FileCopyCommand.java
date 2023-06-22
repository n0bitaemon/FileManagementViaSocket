package filemanager.com.server.cmd.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Environments;
import filemanager.com.server.common.Utils;
import filemanager.com.server.exception.FileNotFoundException;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.InvalidPathException;
import filemanager.com.server.exception.NoPermissionException;
import filemanager.com.server.exception.NotLoggedInException;
import filemanager.com.server.exception.ServerException;

public class FileCopyCommand extends AuthCommand {
	private Path oldPath;
	private Path newPath;

	public FileCopyCommand() {
	}

	public Path getOldPath() {
		return oldPath;
	}

	public void setOldPath(Path oldPath) {
		this.oldPath = oldPath;
	}

	public Path getNewPath() {
		return newPath;
	}

	public void setNewPath(Path newPath) {
		this.newPath = newPath;
	}

	@Override
	public boolean validate() throws ServerException {
		System.out.println("[SERVER LOG] FILE COPY VALIDATE");
		setUsername(Utils.getCurrentUsername(getRemoteAddress()));

		if (!Validator.validateNumberOfArgs(getArgs(), 2)) {
			throw new InvalidNumberOfArgsException(2, getArgs().size());
		}

		if (!Authentication.accIsLoging(getUsername())) {
			throw new NotLoggedInException();
		}

		// Set temporary file path by user input
		String tempOldPath = getArgs().get(0);
		String tempNewPath = getArgs().get(1);

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
		setOldPath(canonicalOldFile);
		setNewPath(canonicalNewFile);

		if (!Files.exists(getOldPath())) {
			throw new FileNotFoundException(tempOldPath);
		}

		if (Files.exists(getNewPath())) {
			throw new FileNotFoundException(tempNewPath);
		}

		return true;
	}

	@Override
	public String exec() throws ServerException {
		System.out.println("[SERVER LOG] FILE COPY EXEC");
		try {
			// If directory not exist, make dir
			Path parentFolder = getNewPath().getParent();
			if (!Files.exists(parentFolder)) {
				Files.createDirectories(parentFolder);
			}

			Files.copy(getOldPath(), getNewPath());
		} catch (IOException e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new ServerException();
		}

		String relativeOldPath = getOldPath().toString().replace(Utils.getUserDir(getUsername()), "");
		String relativeNewPath = getNewPath().toString().replace(Utils.getUserDir(getUsername()), "");
		return String.format("File copied: %s => %s", relativeOldPath, relativeNewPath);
	}

}
