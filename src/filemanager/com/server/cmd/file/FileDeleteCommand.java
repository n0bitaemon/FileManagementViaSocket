package filemanager.com.server.cmd.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import filemanager.com.server.cmd.Command;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Environments;
import filemanager.com.server.common.Utils;

public class FileDeleteCommand extends Command{
	// delete /folder/file.txt n0bita
	private Path path;
	
	public Path getPath() {
		return path;
	}
	
	public void setPath(Path path) {
		this.path = path;
	}
	
	public String validate() {
		System.out.println("[SERVER LOG] FILE DELETION VALIDATE");
		
		if(!Validator.validateNumberOfArgs(getArgs(), 1)) {
			return String.format("Invalid number of arguments! Expected 1 but %d was given\n", getArgs().size());
		}
		
		// Set temporary file path by user input
		String tempPath = getArgs().get(0);
		
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
				
		
		// Validate file path
		if(!Files.exists(getPath())) {
			return "File not found";
		}
		
		// Checking for credentials
		
		return Constants.RESPONSE_SUCCESS_MSG;
	}

	public String exec() {
		System.out.println("[SERVER LOG] FILE DELETION EXECUTION");

		try {
			Files.walkFileTree(getPath(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}
				
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			return Constants.ERROR_UNEXPECTED;
		}
		
		return String.format("Deleted %s", getPath().getFileName());
	}
	
}
