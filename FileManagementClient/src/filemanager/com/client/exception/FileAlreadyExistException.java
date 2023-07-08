package filemanager.com.client.exception;

public class FileAlreadyExistException extends ClientException {
	public FileAlreadyExistException(String path) {
		super(String.format("File already exist: %s", path));
	}
}
