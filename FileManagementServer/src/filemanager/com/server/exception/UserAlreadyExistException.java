package filemanager.com.server.exception;

public class UserAlreadyExistException extends ServerException {
	public UserAlreadyExistException(String username) {
		super(String.format("Account with username %s is already exist", username));
	}
}
