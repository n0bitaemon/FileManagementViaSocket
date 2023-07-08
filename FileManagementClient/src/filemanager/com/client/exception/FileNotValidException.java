package filemanager.com.client.exception;

public class FileNotValidException extends ClientException {
	public FileNotValidException(String path) {
		super(String.format("Invalid path: %s", path));
	}
}
