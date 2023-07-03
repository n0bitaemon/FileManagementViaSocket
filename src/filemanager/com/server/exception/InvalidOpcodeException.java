package filemanager.com.server.exception;

public class InvalidOpcodeException extends ServerException {
	public InvalidOpcodeException() {
		super("The server receive an invalid opcode");
	}
}
