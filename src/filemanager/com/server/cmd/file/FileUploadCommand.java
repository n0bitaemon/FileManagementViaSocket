package filemanager.com.server.cmd.file;

import filemanager.com.server.cmd.Command;

public class FileUploadCommand extends Command {

	@Override
	public String validate() {
		System.out.println("[SERVER LOG] FILE UPLOAD VALIDATION");
		return null;
	}
	
	@Override
	public String exec() {
		System.out.println("[SERVER LOG] FILE UPLOAD EXECUTION");
		return null;
	}


}
