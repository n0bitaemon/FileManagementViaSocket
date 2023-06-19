package filemanager.com.server.exception;

public class NotADirectoryException extends ServerException {
	public NotADirectoryException(String path) {
		super(String.format("Not a directory: ", path));
	}
}
