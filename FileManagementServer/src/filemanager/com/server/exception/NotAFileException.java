package filemanager.com.server.exception;

public class NotAFileException extends ServerException {
	public NotAFileException(String path) {
		super(String.format("File not found: %s", path));
	}
}
