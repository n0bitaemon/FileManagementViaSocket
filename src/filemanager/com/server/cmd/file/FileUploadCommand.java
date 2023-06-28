package filemanager.com.server.cmd.file;

import java.nio.file.Path;

import filemanager.com.server.cmd.AuthCommand;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.exception.InvalidNumberOfArgsException;

public class FileUploadCommand extends AuthCommand {
	private Path source;
	private Path dest;

	@Override
	public boolean validate() throws InvalidNumberOfArgsException {
		System.out.println("[SERVER LOG] FILE UPLOAD VALIDATION");
		
		if(!Validator.validateNumberOfArgs(this.args, 2)) {
			throw new InvalidNumberOfArgsException(2, this.args.size());
		}
		
		return true;
	}

	@Override
	public String exec() {
		System.out.println("[SERVER LOG] FILE UPLOAD EXECUTION");
		
//		try(FileChannel fileChannel = FileChannel.open(getUploadedPath(), StandardOpenOption.WRITE)){
//			SocketChannel socketChannel = getSocketChannel();
//			while(socketChannel.read(null))
//		}
		
		return Constants.RESPONSE_SUCCESS_MSG;
	}

}
