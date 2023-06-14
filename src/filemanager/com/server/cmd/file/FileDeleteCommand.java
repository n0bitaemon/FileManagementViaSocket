package filemanager.com.server.cmd.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import filemanager.com.server.cmd.Command;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Utils;

public class FileDeleteCommand extends Command{
	// delete /folder/file.txt n0bita
	private Path absoluteFilePath;
	
	public void setArgs(List<String> args) {
		super.setArgs(args);
		setFilePath(getArgs().get(0));
	}
	
	public void setFilePath(String relativeFilePath) {
		absoluteFilePath = Paths.get(Constants.STORAGE_DIR + "/" + tempUser + "/" + relativeFilePath).toAbsolutePath();
	}
	
	public String validate() {
		System.out.println("[SERVER LOG] FILE DELETION VALIDATE");
		
		if(!Utils.validateNumberOfArgs(getArgs(), 1)) {
			return String.format("Invalid number of arguments! Expected 1 but %d was given\n", getArgs().size());
		}
		
		// Validate file path
		if(!Files.exists(absoluteFilePath)) {
			return "File not found";
		}
		try {
			Files.delete(absoluteFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return Constants.ERROR_UNEXPECTED;
		}
		
		// Checking for credentials
		
		return Constants.RESPONSE_SUCCESS_MSG;
	}

	public String exec() {
		System.out.println("[SERVER LOG] FILE DELETION EXECUTION");
		
		return Constants.RESPONSE_SUCCESS_MSG;
	}
	
}
