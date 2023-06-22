package filemanager.com.server.cmd.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Environments;
import filemanager.com.server.common.Utils;
import filemanager.com.server.exception.FileAlreadyExistException;
import filemanager.com.server.exception.FileNotFoundException;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.InvalidPathException;
import filemanager.com.server.exception.NoPermissionException;
import filemanager.com.server.exception.NotLoggedInException;
import filemanager.com.server.exception.ServerException;

public class FileMoveCommand extends AuthCommand {
	private Path oldPath;
	private Path newPath;
	
	public FileMoveCommand() {
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
		System.out.println("[SERVER LOG] FILE MOVE VALIDATE");
		setUsername(Utils.getCurrentUsername(getRemoteAddress().toString()));
		if(!Validator.validateNumberOfArgs(getArgs(), 2)) {
			throw new InvalidNumberOfArgsException(2, getArgs().size());
		}
		
		if(!isLoggedIn()) {
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
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new InvalidPathException(tempOldPath);
		}

		// Set canonical new file path
		String canonicalNewFilePath;
		try {
			 canonicalNewFilePath = Utils.getCanonicalFilePath(tempNewPath, getUsername());
		} catch (IOException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new InvalidPathException(tempNewPath);
		}
		
		// Check permission of user
		if(!Validator.checkPermission(canonicalOldFilePath, getUsername())) {
			throw new NoPermissionException(tempOldPath);
		}
		if(!Validator.checkPermission(canonicalNewFilePath, getUsername())) {
			throw new NoPermissionException(tempNewPath);
		}
		
		// Set up valid path property
		Path canonicalOldFile = Paths.get(canonicalOldFilePath);
		Path canonicalNewFile = Paths.get(canonicalNewFilePath);
		setOldPath(canonicalOldFile);
		setNewPath(canonicalNewFile);
		
		
		if(!Files.exists(getOldPath())) {
			throw new FileNotFoundException(tempOldPath);
		}
		
		if(Files.exists(getNewPath())) {
			throw new FileAlreadyExistException(tempNewPath);
		}
		return true;
	}

	@Override
	public String exec() throws ServerException {
		System.out.println("[SERVER LOG] FILE MOVE EXEC");
		try {
			//If directory not exist, make dir
			if(!Files.exists(getNewPath().getParent())) {
				Files.createDirectories(getNewPath());
				System.out.println("Directory not exist, created");
			}
			
			Files.move(getOldPath(), getNewPath());
		} catch (IOException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new ServerException();
		}
		
		String relativeOldPath = getOldPath().toString().replace(Constants.STORAGE_DIR + getUsername(), "");
		String relativeNewPath = getNewPath().toString().replace(Constants.STORAGE_DIR + getUsername(), "");
		return String.format("Moved: %s => %s", relativeOldPath, relativeNewPath);
	}

}
