package filemanager.com.server.exception;

public class FileTooLargeException extends ServerException {
	public FileTooLargeException() {
		super("File too large");
	}
}
