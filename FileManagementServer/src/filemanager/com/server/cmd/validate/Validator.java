package filemanager.com.server.cmd.validate;

import java.util.List;

import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Utils;

/**
 * Common validators
 * 
 * @author Triet
 *
 */
public class Validator {
	private Validator() {
		
	}

	public static boolean validateNumberOfArgs(List<String> args, int[] validNums) {
		return Utils.search(validNums, args.size()) != -1;
	}
	
	public static boolean validateNumberOfArgs(List<String> args, int validNum) {
		return args.size() == validNum;
	}

	/**
	 * Check if a user has the permission to access a path
	 * @param path The path that user want to access
	 * @param username The username of user
	 * @return true, false
	 */
	public static boolean checkPermission(String path, String username) {
		String userFolder = Constants.STORAGE_DIR + username;
		return path.startsWith(userFolder);
	}
}
