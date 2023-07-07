package filemanager.com.server.exception;

public class InvalidUsernameException extends ServerException {
	public InvalidUsernameException() {
		super("Username must contains only alphanumeric characters (A-Z, a-z, 0-9)");
	}
}