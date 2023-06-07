package filemanager.com.server.cmd;

import java.util.ArrayList;
import java.util.List;

import filemanager.com.server.cmd.auth.LoginCommand;
import filemanager.com.server.cmd.auth.LogoutCommand;
import filemanager.com.server.cmd.auth.RegisterCommand;
import filemanager.com.server.cmd.file.FileDeleteCommand;
import filemanager.com.server.cmd.file.FileDownloadCommand;
import filemanager.com.server.cmd.file.FileUploadCommand;

public abstract class Command {
	
	// Command constants
	public static final String AUTH_LOGIN_CMD = "LOGIN";
	public static final String AUTH_LOGOUT_CMD = "LOGOUT";
	public static final String AUTH_REGISTER_CMD = "REGISTER";
	public static final String FILE_UPLOAD_CMD = "UPLOAD";
	public static final String FILE_DOWNLOAD_CMD = "DOWNLOAD";
	public static final String FILE_DELETE_CMD = "DELETE";
	
	private List<String> args;
	
	public static void main(String[] args) {
		Command cmd = parseCommandFromString("register n0bita trietsuper");
		cmd.exec();
		cmd.validate();
	}
	
	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

	public Command() {
		// Default constructor
	}
	
	public abstract boolean exec();
	
	public abstract boolean validate();
	
	public static Command parseCommandFromString(String msg) {
		//Analyze msg and detect cmd type
		String msg_arr[] = msg.split(" ");
		Command cmd = null;
		
		String cmd_name = msg_arr[0];
		switch (cmd_name.toUpperCase()) {
			case Command.AUTH_LOGIN_CMD: {
				cmd = new LoginCommand();
				break;
			}case Command.AUTH_LOGOUT_CMD: {
				cmd = new LogoutCommand();
				break;
			}case Command.AUTH_REGISTER_CMD: {
				cmd = new RegisterCommand();
				break;
			}case Command.FILE_DELETE_CMD: {
				cmd = new FileDeleteCommand();
				break;
			}case Command.FILE_DOWNLOAD_CMD: {
				cmd = new FileDownloadCommand();
				break;
			}case Command.FILE_UPLOAD_CMD: {
				cmd = new FileUploadCommand();
				break;
			}default: {
				return null;
			}
		}
		
		List<String> args = new ArrayList<String>();
		for(int i = 1; i < msg_arr.length; i++) {
			args.add(msg_arr[i]);
		}
		
		cmd.setArgs(args);
		
		return cmd;
	}
}
