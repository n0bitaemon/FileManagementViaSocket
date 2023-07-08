package filemanager.com.client.common;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * A list of commom methods that will be used in other places
 * @author triet
 *
 */
public class Utils {
	private Utils() {}
	
	public static String getCanonicalFilePath(String path) throws IOException {
		File file = new File(Constants.APP_DIR, path);
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
	
	public static void showHelpMenu() {
		System.out.println("######## HELP MENU ########");
		System.out.println("You can use following commands: ");
		System.out.println();
		System.out.println("reg <username> <password>");
		System.out.println("login <username> <password>");
		System.out.println("logout");
		System.out.println();
		System.out.println("ls [path] - list directory");
		System.out.println("copy <source> <dest> - Copy file from source to destination");
		System.out.println("move <source> <dest> - Move file from source to destination");
		System.out.println("mkdir <dirname> - Create new directory");
		System.out.println("rm <path> - Delete file or directory");
		System.out.println("download <path> <dest> - Download a file");
		System.out.println("upload <path> <dest> - Upload a file");
		System.out.println("######## END MENU ########");
	}
	
	public static String[] translateCommandline(String toProcess) {
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
        	return new String[0];
        }
        return result.toArray(new String[0]);
    }
}
