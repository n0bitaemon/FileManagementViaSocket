package filemanager.com.server.exception;

public class DirectoryNotFoundException extends ServerException {

	public DirectoryNotFoundException(String path) {
		super(String.format("Directory not found: s", path));
	}
}
