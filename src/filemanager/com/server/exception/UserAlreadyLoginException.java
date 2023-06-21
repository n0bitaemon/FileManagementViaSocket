package filemanager.com.server.exception;

public class UserAlreadyLoginException extends ServerException {
	public UserAlreadyLoginException(String loggedInUsername) {
		super(String.format("You are logged in as %s", loggedInUsername));
	}
}
