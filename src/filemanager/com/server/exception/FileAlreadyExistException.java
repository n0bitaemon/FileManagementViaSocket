package filemanager.com.server.exception;

public class FileAlreadyExistException extends ServerException {
	public FileAlreadyExistException(String path) {
		super(String.format("File already exist: %s", path));
	}
}
