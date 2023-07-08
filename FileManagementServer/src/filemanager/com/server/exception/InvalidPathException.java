package filemanager.com.server.exception;

public class InvalidPathException extends ServerException {
	public InvalidPathException(String path) {
		super(String.format("Invalid file path: %s", path));
	}
}
