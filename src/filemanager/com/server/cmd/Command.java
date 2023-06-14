package filemanager.com.server.cmd;

import java.util.ArrayList;
import java.util.List;

import filemanager.com.server.cmd.auth.LoginCommand;
import filemanager.com.server.cmd.auth.LogoutCommand;
import filemanager.com.server.cmd.auth.RegisterCommand;
import filemanager.com.server.cmd.dir.MakeDirCommand;
import filemanager.com.server.cmd.file.FileDeleteCommand;
import filemanager.com.server.cmd.file.FileDownloadCommand;
import filemanager.com.server.cmd.file.FileMoveCommand;
import filemanager.com.server.cmd.file.FileUploadCommand;
import filemanager.com.server.common.Constants;

public abstract class Command {
	
	private List<String> args;
	public static final String tempUser = "n0bita";
	
	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

	public Command() {
		
	}
	
	public abstract String validate();
	
	public abstract String exec();
	
	public static Command parseCommandFromString(String msg) {
		//Analyze msg and detect cmd type
		String msg_arr[] = msg.split(" ");
		Command cmd;
		
		String cmd_name = msg_arr[0];
		switch (cmd_name.toLowerCase()) {
			case Constants.AUTH_LOGIN_CMD: {
				cmd = new LoginCommand();
				break;
			}case Constants.AUTH_LOGOUT_CMD: {
				cmd = new LogoutCommand();
				break;
			}case Constants.AUTH_REGISTER_CMD: {
				cmd = new RegisterCommand();
				break;
			}case Constants.FILE_DELETE_CMD: {
				cmd = new FileDeleteCommand();
				break;
			}case Constants.FILE_DOWNLOAD_CMD: {
				cmd = new FileDownloadCommand();
				break;
			}case Constants.FILE_UPLOAD_CMD: {
				cmd = new FileUploadCommand();
				break;
			}case Constants.FILE_MOVE_CMD: {
				cmd = new FileMoveCommand();
				break;
			}case Constants.DIR_MAKE_CMD: {
				cmd = new MakeDirCommand();
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
