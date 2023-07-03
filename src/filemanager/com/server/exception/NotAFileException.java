package filemanager.com.server.exception;

public class NotAFileException extends ServerException {
	public NotAFileException(String path) {
		System.out.println(String.format("File not found: %s", path));
	}
}
