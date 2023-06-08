package filemanager.com.server.common;

public class Constants {
	public static final String AUTH_LOGIN_CMD = "LOGIN";
	public static final String AUTH_LOGOUT_CMD = "LOGOUT";
	public static final String AUTH_REGISTER_CMD = "REGISTER";
	public static final String FILE_UPLOAD_CMD = "UPLOAD";
	public static final String FILE_DOWNLOAD_CMD = "DOWNLOAD";
	public static final String FILE_DELETE_CMD = "DELETE";
	
	public static final String APP_DIR = System.getProperty("user.dir");
	public static final String STORAGE_DIR = APP_DIR + "/storage";
}
