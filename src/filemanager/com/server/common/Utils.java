package filemanager.com.server.common;

import java.io.File;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.Normalizer.Form;

import filemanager.com.server.auth.Authentication;

public class Utils {
	private Utils() {
		
	}
	
	public static boolean validateFilePath(String filePath) {
		Path path = Paths.get(filePath);
		return path.isAbsolute() && path.startsWith("/");
	}

	public static String getCanonicalFilePath(String path, String userDir) throws IOException {
		String userFolder = Constants.STORAGE_DIR + userDir;
		File file = new File(userFolder, path);
		return file.getCanonicalFile().toString();
	}

	public static String removeNonAlphabetCharacter(String s) {
		if (s == null)
			return null;
		return s.replaceAll("[^A-Za-z0-9]", "");
	}

	public static String normalizeString(String s) {
		return Normalizer.normalize(s, Form.NFKC);
	}

	public static String getUserDir(String username) {
		return Constants.STORAGE_DIR + username;
	}

	public static String getCurrentUsername(SocketAddress remoteAddress) {
		return Authentication.session.get(remoteAddress);
	}
	
	public static int search(int arr[], int x) {
        int l = 0, r = arr.length - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;

            if (arr[m] == x)
                return m;

            if (arr[m] < x)
                l = m + 1;

            else
                r = m - 1;
        }

        return -1;
	}

}
