package filemanager.com.server.cmd.file;

import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.exception.InvalidNumberOfArgsException;

public class FileUploadCommand extends AuthCommand {
	private Path uploadedPath;

	public Path getUploadedPath() {
		return uploadedPath;
	}

	public void setUploadedPath(Path uploadedPath) {
		this.uploadedPath = uploadedPath;
	}

	@Override
	public boolean validate() throws InvalidNumberOfArgsException {
		System.out.println("[SERVER LOG] FILE UPLOAD VALIDATION");
		
		if(!Validator.validateNumberOfArgs(getArgs(), 2)) {
			throw new InvalidNumberOfArgsException(2, getArgs().size());
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
