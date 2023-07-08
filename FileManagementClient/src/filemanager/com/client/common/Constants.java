package filemanager.com.client.common;

public class Constants {
	private Constants() {
		
	}
	
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String APP_DIR = System.getProperty("user.dir");
	public static final String HOME_DIR = System.getProperty("user.home");
	
	public static final String ERR_NO_RESPONSE = "No response from server";
	public static final String ERR_UNEXPECTED = "Unexpected error";
	public static final String ERR_MESSAGE_TO_LONG = "Message too long";

	public static final String MSG_DISCONNECTED = "Disconnected to server";

	
}
