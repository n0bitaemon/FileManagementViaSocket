package filemanager.com.client.exception;

public class ServerUnreachableException extends ClientException {
	public ServerUnreachableException() {
		super("Server unreachable!");
	}
}
