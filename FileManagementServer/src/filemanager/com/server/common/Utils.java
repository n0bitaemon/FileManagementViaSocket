package filemanager.com.server.common;

import java.io.File;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.StringTokenizer;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.exception.InvalidCommandException;

/**
 * Common utility functions that will be used by other classes
 * @author Triet
 *
 */
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
	
	public static int search(int[] arr, int x) {
        int l = 0;
        int r = arr.length - 1;
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
	
	public static String[] translateCommandline(String toProcess) throws InvalidCommandException {
        if (toProcess == null || toProcess.isEmpty()) {
            //no command? no string
            return new String[0];
        }
        // parse with a simple finite state machine

        final int normal = 0;
        final int inQuote = 1;
        final int inDoubleQuote = 2;
        int state = normal;
        final StringTokenizer tok = new StringTokenizer(toProcess, "\"' ", true);
        final ArrayList<String> result = new ArrayList<>();
        final StringBuilder current = new StringBuilder();
        boolean lastTokenHasBeenQuoted = false;

        while (tok.hasMoreTokens()) {
            String nextTok = tok.nextToken();
            switch (state) {
            case inQuote:
                if ("'".equals(nextTok)) {
                    lastTokenHasBeenQuoted = true;
                    state = normal;
                } else {
                    current.append(nextTok);
                }
                break;
            case inDoubleQuote:
                if ("\"".equals(nextTok)) {
                    lastTokenHasBeenQuoted = true;
                    state = normal;
                } else {
                    current.append(nextTok);
                }
                break;
            default:
                if ("'".equals(nextTok)) {
                    state = inQuote;
                } else if ("\"".equals(nextTok)) {
                    state = inDoubleQuote;
                } else if (" ".equals(nextTok)) {
                    if (lastTokenHasBeenQuoted || current.length() > 0) {
                        result.add(current.toString());
                        current.setLength(0);
                    }
                } else {
                    current.append(nextTok);
                }
                lastTokenHasBeenQuoted = false;
                break;
            }
        }
        if (lastTokenHasBeenQuoted || current.length() > 0) {
            result.add(current.toString());
        }
        if (state == inQuote || state == inDoubleQuote) {
            throw new InvalidCommandException();
        }
        return result.toArray(new String[0]);
    }
}
