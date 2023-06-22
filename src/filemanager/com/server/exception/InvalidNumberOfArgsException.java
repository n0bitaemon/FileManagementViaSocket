package filemanager.com.server.exception;

public class InvalidNumberOfArgsException extends ServerException {
	public InvalidNumberOfArgsException(int expected, int given) {
		super(String.format("Invalid number of arguments: Expected %s arguments but %s was given", expected, given));
	}

	public InvalidNumberOfArgsException(int[] expected, int given) {
		super(generateErrMessageFromArray(expected, given));
	}

	private static String generateErrMessageFromArray(int[] expected, int given) {
		StringBuilder str = new StringBuilder("Invalid number of arguments: Expected ");
		for (int i = 0; i < expected.length; i++) {
			if (i == expected.length - 1) {
				str.append(i);
				break;
			}
			str.append(i + " or ");
		}
		str.append(" arguments but " + given + " was given");
		return str.toString();
	}
}
