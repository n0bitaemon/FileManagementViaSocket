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

public class FileCopyCommand extends Command {
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
	public String validate() {
		if(!Validator.validateNumberOfArgs(getArgs(), 2)) {
			return String.format("Invalid number of arguments! Expected 2 but %d was given\n", getArgs().size());
		}
		
		// Set temporary file path by user input
		String tempOldPath = getArgs().get(0);
		String tempNewPath = getArgs().get(1);
		
		// Set canonical old file path
		String canonicalOldFilePath;
		try {
			 canonicalOldFilePath = Utils.getCanonicalFilePath(tempOldPath, tempUser);
		} catch (IOException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			return String.format("Invalid file path: %s", tempOldPath);
		}

		// Set canonical new file path
		String canonicalNewFilePath;
		try {
			 canonicalNewFilePath = Utils.getCanonicalFilePath(tempNewPath, tempUser);
		} catch (IOException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			return String.format("Invalid file path: %s", tempNewPath);
		}
		
		// Check permission of user
		if(!Validator.checkPermission(canonicalOldFilePath, tempUser)) {
			return String.format("No permission to path: %s", tempOldPath);
		}
		if(!Validator.checkPermission(canonicalNewFilePath, tempUser)) {
			return String.format("No permission to path: %s", tempNewPath);
		}
		
		// Set up valid path property
		Path canonicalOldFile = Paths.get(canonicalOldFilePath);
		Path canonicalNewFile = Paths.get(canonicalNewFilePath);
		setOldPath(canonicalOldFile);
		setNewPath(canonicalNewFile);
		
		if(!Files.exists(getOldPath())) {
			return String.format("File not found: %s", getOldPath());
		}
		
		if(Files.exists(getNewPath())) {
			return String.format("File is already exist: %s", getNewPath());
		}
		
		return Constants.RESPONSE_SUCCESS_MSG;
	}

	@Override
	public String exec() {
		try {
			//If directory not exist, make dir
			Path parentFolder = getNewPath().getParent();
			if(!Files.exists(parentFolder)) {
				Files.createDirectories(parentFolder);
			}
			
			Files.copy(getOldPath(), getNewPath());
		} catch (IOException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			return Constants.ERROR_UNEXPECTED;
		}
		
		String relativeOldPath = getOldPath().toString().replace(Constants.STORAGE_DIR + tempUser, "");
		String relativeNewPath = getNewPath().toString().replace(Constants.STORAGE_DIR + tempUser, "");
		return String.format("Copied %s to %s", relativeOldPath, relativeNewPath);
	}

}
