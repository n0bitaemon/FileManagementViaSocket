package filemanager.com.client.common;

/**
 * A list of constants that will be used in other places
 * @author triet
 *
 */
public class Constants {
	private Constants() {
		
	}
	
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String APP_DIR = System.getProperty("user.dir");
	public static final String HOME_DIR = System.getProperty("user.home");
	
	public static final String ERR_NO_RESPONSE = "No response from server";
	public static final String ERR_UNEXPECTED = "Unexpected error";
	public static final String ERR_MESSAGE_TO_LONG = "Message too long";
	public static final String ERR_CANNOT_REACH_SERVER = "Cannot reach server";
	
	public static final String MSG_DISCONNECTED = "Disconnected to server";
	
}
