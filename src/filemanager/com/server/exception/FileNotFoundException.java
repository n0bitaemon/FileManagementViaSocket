package filemanager.com.server.exception;

public class FileNotFoundException extends ServerException {
	public FileNotFoundException(String path) {
		super(String.format("File not found: %s", path));
	}
}
