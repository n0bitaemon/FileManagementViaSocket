package filemanager.com.client.exception;

public class ClientException extends Exception {
	public ClientException() {
		super("Client error occured, please try again!");
	}
	
	public ClientException(String s) {
		super(s);
	}
}
