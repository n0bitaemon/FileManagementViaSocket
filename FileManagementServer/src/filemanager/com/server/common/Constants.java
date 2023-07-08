package filemanager.com.server.common;

/**
 * Common constants that will be used by other classes
 * @author Triet
 *
 */
public class Constants {
	private Constants() {
		
	}
	
	public static final String AUTH_LOGIN_CMD = "login";
	public static final String AUTH_LOGOUT_CMD = "logout";
	public static final String AUTH_REGISTER_CMD = "reg";
	public static final String FILE_UPLOAD_CMD = "upload";
	public static final String FILE_DOWNLOAD_CMD = "download";
	public static final String FILE_DELETE_CMD = "rm";
	public static final String FILE_MOVE_CMD = "mv";
	public static final String FILE_COPY_CMD = "cp";
	public static final String DIR_MAKE_CMD = "mkdir";
	public static final String DIR_LIST_FILE_CMD = "ls";

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String APP_DIR = System.getProperty("user.dir");
	public static final String STORAGE_DIR = APP_DIR + FILE_SEPARATOR + "storage" + FILE_SEPARATOR;

	public static final String RESPONSE_SUCCESS_MSG = "Success";

	public static final String ERROR_UNEXPECTED = "Unexpected error";
}
