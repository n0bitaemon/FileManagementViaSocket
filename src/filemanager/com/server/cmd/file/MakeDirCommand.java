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
	public String validate() {
		if(!Validator.validateNumberOfArgs(getArgs(), 1)) {
			return String.format("Invalid number of arguments! Expected 1 but %d was given\n", getArgs().size());
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
			return "Invalid file path!";
		}
		
		// Check permission of user
		if(!Validator.checkPermission(canonicalPath, tempUser)) {
			return "Forbidden!";
		}
		
		// Set up valid path property
		Path canonicalFolder = Paths.get(canonicalPath);
		setPath(canonicalFolder);
		
		if(Files.exists(getPath())) {
			return "Directory is already exist!";
		}
		return Constants.RESPONSE_SUCCESS_MSG;
	}

	@Override
	public String exec() {
		try {
			Files.createDirectories(getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return String.format("Created folder %s", getPath().getFileName());
	}

}
