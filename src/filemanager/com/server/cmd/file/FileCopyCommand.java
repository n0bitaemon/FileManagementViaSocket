package filemanager.com.server.cmd.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import filemanager.com.server.cmd.Command;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Utils;

public class FileCopyCommand extends Command {
	private Path absoluteOldPath;
	private Path absoluteNewPath;
	
	public FileCopyCommand() {
	}
	
	public Path getAbsoluteOldPath() {
		return absoluteOldPath;
	}

	public Path getAbsoluteNewPath() {
		return absoluteNewPath;
	}
	
	public void setArgs(List<String> args) {
		super.setArgs(args);
		setAbsolutePaths(getArgs().get(0), getArgs().get(1));
	}
	
	public void setAbsolutePaths(String relativeOldPath, String relativeNewPath) {
		absoluteOldPath = Paths.get(Constants.STORAGE_DIR + "/" + tempUser + "/" + relativeOldPath).toAbsolutePath();
		absoluteNewPath = Paths.get(Constants.STORAGE_DIR + "/" + tempUser + "/" + relativeNewPath).toAbsolutePath();
		System.out.println("absoluteOldPath: " + absoluteOldPath);
		System.out.println("absoluteNewPath: " + absoluteNewPath);
	}

	@Override
	public String validate() {
		if(!Utils.validateNumberOfArgs(getArgs(), 2)) {
			return String.format("Invalid number of arguments! Expected 2 but %d was given\n", getArgs().size());
		}
		if(!Files.exists(absoluteOldPath)) {
			return String.format("File not found: %s", absoluteOldPath);
		}
		if(Files.exists(absoluteNewPath)) {
			return String.format("File is already exist: %s", absoluteNewPath);
		}
		return Constants.RESPONSE_SUCCESS_MSG;
	}

	@Override
	public String exec() {
		try {
			//If directory not exist, make dir
			if(!Files.exists(absoluteNewPath.getParent())) {
				Files.createDirectories(absoluteNewPath);
				System.out.println("Directory not exist, created");
			}
			
			Files.copy(absoluteOldPath, absoluteNewPath);
		} catch (IOException e) {
			return Constants.ERROR_UNEXPECTED;
		}
		
		return Constants.RESPONSE_SUCCESS_MSG;
	}

}
