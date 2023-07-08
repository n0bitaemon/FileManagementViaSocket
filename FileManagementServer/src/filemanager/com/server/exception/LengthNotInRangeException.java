package filemanager.com.server.exception;

public class LengthNotInRangeException extends ServerException{
	public LengthNotInRangeException(int min, int max, String field) {
		super(String.format("The length of field %s must in range (%d, %d)", field, min, max));
	}
}
