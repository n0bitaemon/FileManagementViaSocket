package filemanager.com.server.exception;

public class ServerException extends Exception {
	public ServerException() {
		super("Server error occured, please try again!");
	}

	public ServerException(String s) {
		super(s);
	}
}
