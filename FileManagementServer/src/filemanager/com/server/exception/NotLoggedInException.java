package filemanager.com.server.exception;

public class NotLoggedInException extends ServerException {
	public NotLoggedInException() {
		super("Please login first!");
	}
}
