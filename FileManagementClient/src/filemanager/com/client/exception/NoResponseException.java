package filemanager.com.client.exception;

public class NoResponseException extends ClientException {
	public NoResponseException() {
		super("No response from server");
	}
}
