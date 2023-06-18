package filemanager.com.server.cmd.file;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import filemanager.com.server.cmd.Command;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Environments;
import filemanager.com.server.common.Utils;

public class ListFileCommand extends Command {
	private Path path;
	
	public Path getPath() {
		return this.path;
	}
	
	public void setPath(Path path) {
		this.path = path;
	}

	@Override
	public String validate() {
		System.out.println("[SERVER LOG] LIST FILE");
		if(!Validator.validateNumberOfArgs(getArgs(), 1) && !Validator.validateNumberOfArgs(getArgs(), 0)) {
			return String.format("Invalid number of arguments! Expected 0 or 1 parameter but %d was given\n", getArgs().size());
		}
		
		// Set temporary file path by user input
		String tempPath;
		if(getArgs().size() == 1) {
			tempPath = getArgs().get(0);
		}else {
			tempPath = "/";
		}
		
		// Set canonical file path
		String canonicalFilePath;
		try {
			 canonicalFilePath = Utils.getCanonicalFilePath(tempPath, tempUser);
		} catch (IOException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			return "Invalid file path!";
		}
		
		// Check permission of user
		if(!Validator.checkPermission(canonicalFilePath, tempUser)) {
			return "Forbidden!";
		}
		
		// Set up valid path property
		Path canonicalFile = Paths.get(canonicalFilePath);
		setPath(canonicalFile);
		
		if(!Files.exists(getPath())) {
			return "Directory not found!";
		}
		
		if(!Files.isDirectory(getPath())) {
			return "The given path is not a directory";
		}
		return Constants.RESPONSE_SUCCESS_MSG;
	}

	@Override
	public String exec() {
		StringBuilder filesResponse = new StringBuilder();
		
		List<Path> files = new ArrayList<>();
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(getPath());
			for(Path entry : stream) {
				files.add(entry);
			}
		} catch (IOException e) {
			return Constants.ERROR_UNEXPECTED;
		}
		
		if(files.size() == 0) {
			filesResponse.append("");
			return filesResponse.toString();
		}
		
		for(Path entry : files) {
			filesResponse.append(files.indexOf(entry) == files.size()-1 ? "└─ " : "├─ ");
			filesResponse.append(entry.getFileName().toString());
			filesResponse.append("\n");
		}
		
		return filesResponse.toString();
	}
	
}
