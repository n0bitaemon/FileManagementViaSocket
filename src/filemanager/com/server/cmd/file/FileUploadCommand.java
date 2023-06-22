package filemanager.com.server.cmd.file;

import filemanager.com.server.common.Constants;

public class FileUploadCommand extends AuthCommand {

	@Override
	public boolean validate() {
		System.out.println("[SERVER LOG] FILE UPLOAD VALIDATION");
		return true;
	}
	
	@Override
	public String exec() {
		System.out.println("[SERVER LOG] FILE UPLOAD EXECUTION");
		return Constants.RESPONSE_SUCCESS_MSG;
	}


}
