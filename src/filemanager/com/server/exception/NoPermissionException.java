package filemanager.com.server.exception;

public class NoPermissionException extends ServerException {
	public NoPermissionException(String path) {
		super(String.format("No permission: ", path));
	}
}
