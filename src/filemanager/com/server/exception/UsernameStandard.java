package filemanager.com.server.exception;

public class UsernameStandard extends ServerException {
	public UsernameStandard() {
		super("Username must contains only alphanumeric characters (A-Z, a-z, 0-9)");
	}
}
