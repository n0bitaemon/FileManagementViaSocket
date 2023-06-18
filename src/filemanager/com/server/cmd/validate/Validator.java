package filemanager.com.server.cmd.validate;

import java.util.List;

import filemanager.com.server.common.Constants;

public class Validator {

	public static boolean validateNumberOfArgs(List<String> args, int validNum) {
		return args.size() == validNum;
	}

	public static boolean checkPermission(String canonicalFilePath, String userDir) {
		String userFolder = Constants.STORAGE_DIR + userDir;
		if(canonicalFilePath.startsWith(userFolder)) {
			return true;
		}
		return false;
	}
}
