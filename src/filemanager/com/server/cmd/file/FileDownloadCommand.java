package filemanager.com.server.cmd.file;

import filemanager.com.server.cmd.Command;

public class FileDownloadCommand extends Command {

	@Override
	public boolean exec() {
		System.out.println("File Download exec()");
		return true;
	}

	@Override
	public boolean validate() {
		System.out.println("File download validate()");
		return true;
	}

}
