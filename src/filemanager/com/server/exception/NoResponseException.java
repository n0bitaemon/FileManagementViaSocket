package filemanager.com.server.exception;

public class NoResponseException extends ServerException {
	public NoResponseException() {
		super("No response from client");
	}
}
