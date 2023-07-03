package filemanager.com.server.cmd;

import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;

import filemanager.com.server.cmd.auth.LoginCommand;
import filemanager.com.server.cmd.auth.LogoutCommand;
import filemanager.com.server.cmd.auth.RegisterCommand;
import filemanager.com.server.cmd.file.FileCopyCommand;
import filemanager.com.server.cmd.file.FileDeleteCommand;
import filemanager.com.server.cmd.file.FileDownloadCommand;
import filemanager.com.server.cmd.file.FileMoveCommand;
import filemanager.com.server.cmd.file.FileUploadCommand;
import filemanager.com.server.cmd.file.ListFileCommand;
import filemanager.com.server.cmd.file.MakeDirCommand;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Utils;
import filemanager.com.server.exception.InvalidCommandException;
import filemanager.com.server.exception.ServerException;

public abstract class Command {

	protected SelectionKey key;
	protected List<String> args;
	protected SocketAddress remoteAddress;

	protected Command() {

	}

	protected Command(SelectionKey key, SocketAddress remoteAddress, List<String> args) {
		this.key = key;
		this.remoteAddress = remoteAddress;
		this.args = args;
	}

	public abstract boolean validate() throws ServerException;

	public abstract String exec() throws ServerException;

	public static Command parseCommandFromString(String msg, SocketAddress remoteAddress, SelectionKey key) throws InvalidCommandException {
		// Analyze msg and detect cmd type
		String[] msgArr = Utils.translateCommandline(msg);
		if(msgArr.length == 0) {
			return null;
		}
		Command cmd;

		String cmdNameRaw = msgArr[0];
		String cmdNameOnlyAlphabet = Utils.removeNonAlphabetCharacter(cmdNameRaw);
		String cmdName = Utils.normalizeString(cmdNameOnlyAlphabet);
		switch (cmdName) {
			case Constants.AUTH_LOGIN_CMD: {
				cmd = new LoginCommand();
				break;
			}
			case Constants.AUTH_LOGOUT_CMD: {
				cmd = new LogoutCommand();
				break;
			}
			case Constants.AUTH_REGISTER_CMD: {
				cmd = new RegisterCommand();
				break;
			}
			case Constants.FILE_DELETE_CMD: {
				cmd = new FileDeleteCommand();
				break;
			}
			case Constants.FILE_DOWNLOAD_CMD: {
				cmd = new FileDownloadCommand();
				break;
			}
			case Constants.FILE_UPLOAD_CMD: {
				cmd = new FileUploadCommand();
				break;
			}
			case Constants.FILE_MOVE_CMD: {
				cmd = new FileMoveCommand();
				break;
			}
			case Constants.FILE_COPY_CMD: {
				cmd = new FileCopyCommand();
				break;
			}
			case Constants.DIR_MAKE_CMD: {
				cmd = new MakeDirCommand();
				break;
			}
			case Constants.DIR_LIST_FILE_CMD: {
				cmd = new ListFileCommand();
				break;
			}
			default: {
				return null;
			}
		}
		
		List<String> args = new ArrayList<>();
		for (int i = 1; i < msgArr.length; i++) {
			args.add(msgArr[i]);
		}
		cmd.args = args;
		cmd.remoteAddress = remoteAddress;
		cmd.key = key;

		return cmd;
	}
	
}
