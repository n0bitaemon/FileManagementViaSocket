package filemanager.com.server.cmd.dir;

import filemanager.com.server.cmd.Command;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Utils;

public class ListFileCommand extends Command {

	@Override
	public String validate() {
		if(!Utils.validateNumberOfArgs(getArgs(), 0)) {
			return String.format("Invalid number of arguments! Expected 1 but %d was given\n", getArgs().size());
		}
		return Constants.RESPONSE_SUCCESS_MSG;
	}

	@Override
	public String exec() {
		
		return Constants.RESPONSE_SUCCESS_MSG;
	}
	
}
