package filemanager.com.server.cmd.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import filemanager.com.server.cmd.Command;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Environments;
import filemanager.com.server.common.Utils;
import filemanager.com.server.exception.FileAlreadyExistException;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.InvalidPathException;
import filemanager.com.server.exception.NoPermissionException;
import filemanager.com.server.exception.ServerException;

public class MakeDirCommand extends Command {
	private Path path;

	public MakeDirCommand() {
		
	}
	
	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}
	
	@Override
	public boolean validate() throws ServerException {
		if(!Validator.validateNumberOfArgs(getArgs(), 1)) {
			throw new InvalidNumberOfArgsException(1, getArgs().size());
		}
		
		// Set temporary file path by user input
		String tempPath = getArgs().get(0);
		
		// Set canonical file path
		String canonicalPath;
		try {
			 canonicalPath = Utils.getCanonicalFilePath(tempPath, tempUser);
		} catch (IOException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new InvalidPathException(tempPath);
		}
		
		// Check permission of user
		if(!Validator.checkPermission(canonicalPath, tempUser)) {
			throw new NoPermissionException(tempPath);
		}
		
		// Set up valid path property
		Path canonicalFolder = Paths.get(canonicalPath);
		setPath(canonicalFolder);
		
		if(Files.exists(getPath())) {
			throw new FileAlreadyExistException(tempPath);
		}
		return true;
	}

	@Override
	public String exec() throws ServerException {
		try {
			Files.createDirectories(getPath());
		} catch (IOException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new ServerException();
		}
		return String.format("Created folder: %s", getPath().getFileName());
	}

}
