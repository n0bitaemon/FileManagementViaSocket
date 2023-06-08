package filemanager.com.server.cmd.file;

import filemanager.com.server.Response;
import filemanager.com.server.cmd.Command;

public class FileUploadCommand extends Command {

	@Override
	public Response validate() {
		System.out.println("[SERVER LOG] FILE UPLOAD VALIDATION");
		return null;
	}
	
	@Override
	public Response exec() {
		System.out.println("[SERVER LOG] FILE UPLOAD EXECUTION");
		return null;
	}


}
