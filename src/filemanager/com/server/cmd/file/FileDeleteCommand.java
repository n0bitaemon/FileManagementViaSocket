package filemanager.com.server.cmd.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Environments;
import filemanager.com.server.common.Utils;
import filemanager.com.server.exception.FileNotFoundException;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.InvalidPathException;
import filemanager.com.server.exception.NoPermissionException;
import filemanager.com.server.exception.NotLoggedInException;
import filemanager.com.server.exception.ServerException;

public class FileDeleteCommand extends AuthCommand{
	// delete /folder/file.txt n0bita
	private Path path;
	
	public Path getPath() {
		return path;
	}
	
	public void setPath(Path path) {
		this.path = path;
	}

	public boolean validate() throws ServerException {
		System.out.println("[SERVER LOG] FILE DELETION VALIDATE");
		setUsername(Utils.getCurrentUsername(getRemoteAddress().toString()));
		
		if(!Validator.validateNumberOfArgs(getArgs(), 1)) {
			throw new InvalidNumberOfArgsException(1, getArgs().size());
		}
		
		if(!isLoggedIn()) {
			throw new NotLoggedInException();
		}
		
		// Set temporary file path by user input
		String tempPath = getArgs().get(0);
		
		// Set canonical file path
		String canonicalFilePath;
		try {
			 canonicalFilePath = Utils.getCanonicalFilePath(tempPath, getUsername());
		} catch (IOException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new InvalidPathException(tempPath);
		}
		
		// Check permission of user
		if(!Validator.checkPermission(canonicalFilePath, getUsername())) {
			throw new NoPermissionException(tempPath);
		}
		
		// Set up valid path property
		Path canonicalFile = Paths.get(canonicalFilePath);
		setPath(canonicalFile);
				
		
		// Validate file path
		if(!Files.exists(getPath())) {
			throw new FileNotFoundException(tempPath);
		}
		
		// Checking for credentials
		
		return true;
	}

	public String exec() throws ServerException {
		System.out.println("[SERVER LOG] FILE DELETION EXEC");

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
			throw new ServerException();
		}
		
		return String.format("Deleted: %s", getPath().getFileName());
	}
	
}
