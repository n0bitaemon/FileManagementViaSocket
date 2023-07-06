package filemanager.com.server.cmd.validate;

import java.util.List;

import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Utils;

public class Validator {
	private Validator() {
		
	}

	public static boolean validateNumberOfArgs(List<String> args, int[] validNums) {
		return Utils.search(validNums, args.size()) != -1;
	}
	
	public static boolean validateNumberOfArgs(List<String> args, int validNum) {
		return args.size() == validNum;
	}

	public static boolean checkPermission(String canonicalFilePath, String userDir) {
		String userFolder = Constants.STORAGE_DIR + userDir;
		return canonicalFilePath.startsWith(userFolder);
	}
}
