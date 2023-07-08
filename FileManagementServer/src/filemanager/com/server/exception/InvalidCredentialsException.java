package filemanager.com.server.exception;

public class InvalidCredentialsException extends ServerException {
	public InvalidCredentialsException() {
		super("Invalid username or password");
	}
}
