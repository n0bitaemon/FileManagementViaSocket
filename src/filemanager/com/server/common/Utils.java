package filemanager.com.server.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Utils {
	public static boolean validateFilePath(String filePath) {
		Path path = Paths.get(filePath);
		boolean isValid = path.isAbsolute() && path.startsWith("/");
		return isValid;
	}
	
	public static boolean validateNumberOfArgs(List<String> args, int validNum) {
		return args.size() == validNum;
	}
}
