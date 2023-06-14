package filemanager.com.server.cmd.dir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import filemanager.com.server.cmd.Command;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Utils;

public class MakeDirCommand extends Command {
	private Path absoluteDirPath;

	public MakeDirCommand() {
		
	}
	
	public void setArgs(List<String> args) {
		super.setArgs(args);
		setDirPath(getArgs().get(0));
	}
	
	public void setDirPath(String dirPath) {
		absoluteDirPath = Paths.get(Constants.STORAGE_DIR + "/" + tempUser + "/" + dirPath).toAbsolutePath();
		System.out.println("absoluteNewPath: " + absoluteDirPath);
	}
	
	@Override
	public String validate() {
		if(!Utils.validateNumberOfArgs(getArgs(), 1)) {
			return String.format("Invalid number of arguments! Expected 1 but %d was given\n", getArgs().size());
		}
		return Constants.RESPONSE_SUCCESS_MSG;
	}

	@Override
	public String exec() {
		try {
			Files.createDirectories(absoluteDirPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Constants.RESPONSE_SUCCESS_MSG;
	}

}
