package filemanager.com.server.cmd.file;

import filemanager.com.server.cmd.Command;

public class FileDeleteCommand extends Command{
	public boolean validate() {
		System.out.println("File delete validate()");
		return false;
	}

	public boolean exec() {
		System.out.println("File delete exec()");
		return false;
	}
	
}
