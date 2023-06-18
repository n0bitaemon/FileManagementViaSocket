package filemanager.com.server.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
	public static boolean validateFilePath(String filePath) {
		Path path = Paths.get(filePath);
		boolean isValid = path.isAbsolute() && path.startsWith("/");
		return isValid;
	}
	
	public static String getCanonicalFilePath(String path, String userDir) throws IOException {
		String userFolder = Constants.STORAGE_DIR + userDir;
		File file = new File(userFolder, path);
		return file.getCanonicalFile().toString();
	}
	
	
}
