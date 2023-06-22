package filemanager.com.server.cmd.file;

import filemanager.com.server.cmd.Command;
import filemanager.com.server.common.Constants;

public class FileDownloadCommand extends Command {

	@Override
	public boolean validate() {
		System.out.println("[SERVER LOG] FILE DOWNLOAD VALIDATION");
		return true;
	}

	@Override
	public String exec() {
		System.out.println("[SERVER LOG] FILE DOWNLOAD EXECUTION");
		return Constants.RESPONSE_SUCCESS_MSG;
	}

}
