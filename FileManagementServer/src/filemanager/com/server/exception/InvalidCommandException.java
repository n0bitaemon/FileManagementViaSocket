package filemanager.com.server.exception;

public class InvalidCommandException extends ServerException {
	public InvalidCommandException() {
		super("Invalid command");
	}
}
