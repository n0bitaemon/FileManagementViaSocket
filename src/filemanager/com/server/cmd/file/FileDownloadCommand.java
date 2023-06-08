package filemanager.com.server.cmd.file;

import filemanager.com.server.Response;
import filemanager.com.server.cmd.Command;

public class FileDownloadCommand extends Command {

	@Override
	public Response validate() {
		System.out.println("[SERVER LOG] FILE DOWNLOAD VALIDATION");
		return null;
	}
	
	@Override
	public Response exec() {
		System.out.println("[SERVER LOG] FILE DOWNLOAD EXECUTION");
		return null;
	}


}
