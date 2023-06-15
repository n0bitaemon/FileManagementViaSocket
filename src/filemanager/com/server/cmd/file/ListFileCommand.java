package filemanager.com.server.cmd.file;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import filemanager.com.server.cmd.Command;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Utils;

public class ListFileCommand extends Command {
	private Path absoluteDirPath;
	
	public void setArgs(List<String> args) {
		super.setArgs(args);
		if(getArgs().size() == 0) {
			setDirPath("/");
		}else {
			setDirPath(getArgs().get(0));
		}
	}
	
	public void setDirPath(String relativeFilePath) {
		absoluteDirPath = Paths.get(Constants.STORAGE_DIR + "/" + tempUser + "/" + relativeFilePath).toAbsolutePath();
	}

	@Override
	public String validate() {
		if(!Utils.validateNumberOfArgs(getArgs(), 1) && !Utils.validateNumberOfArgs(getArgs(), 0)) {
			return String.format("Invalid number of arguments! Expected 1 but %d was given\n", getArgs().size());
		}
		if(!Files.exists(absoluteDirPath)) {
			return "Directory not found!";
		}
		if(!Files.isDirectory(absoluteDirPath)) {
			return "The given path is not a directory";
		}
		return Constants.RESPONSE_SUCCESS_MSG;
	}

	@Override
	public String exec() {
		StringBuilder filesResponse = new StringBuilder();
		
		List<Path> files = new ArrayList<>();
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(absoluteDirPath);
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
		
		System.out.println("before for");
		
		for(Path entry : files) {
			System.out.printf("indexOf(entry): %d, files.size(): %d", files.indexOf(entry), files.size());
			filesResponse.append(files.indexOf(entry) == files.size()-1 ? "└─ " : "├─ ");
			filesResponse.append(entry.getFileName().toString());
			filesResponse.append("\n");
		}
		
		System.out.println("Done");
		
		return filesResponse.toString();
	}
	
}
