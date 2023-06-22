package filemanager.com.server.exception;

public class UserAlreadyLoginException extends ServerException {
	public UserAlreadyLoginException(String loggedInUsername) {
		super(String.format("You are already logged in as %s, please log out first", loggedInUsername));
	}
}
