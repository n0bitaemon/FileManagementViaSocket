package filemanager.com.server.common;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
	public static boolean validateFilePath(String filePath) {
		Path path = Paths.get(filePath);
		boolean isValid = path.isAbsolute() && path.startsWith("/");
		return isValid;
	}
}
