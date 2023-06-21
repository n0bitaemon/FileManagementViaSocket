package filemanager.com.server.exception;

public class MinLengthException extends ServerException {
	public MinLengthException(String field, int num) {
		super(String.format("The field %s length must be larger than %d characters", field.toLowerCase(), num));
	}
}
