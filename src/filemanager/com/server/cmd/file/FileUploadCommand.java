package filemanager.com.server.cmd.file;

import filemanager.com.server.cmd.Command;

public class FileUploadCommand extends Command {

	@Override
	public boolean exec() {
		System.out.println("File upload exec()");
		return true;
	}

	@Override
	public boolean validate() {
		System.out.println("File upload validate()");
		return true;
	}

}
