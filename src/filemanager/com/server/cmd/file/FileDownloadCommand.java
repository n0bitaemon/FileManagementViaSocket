package filemanager.com.server.cmd.file;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import filemanager.com.server.cmd.Command;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.ServerException;

public class FileDownloadCommand extends Command {
	private static final Logger LOGGER = LogManager.getLogger(FileDeleteCommand.class);
	
	private Path source;
	private Path dest;

	@Override
	public boolean validate() throws ServerException {
		LOGGER.info("{}: validate command - {}", this.remoteAddress, Constants.FILE_DOWNLOAD_CMD);
		
		if(!Validator.validateNumberOfArgs(this.args, 2)) {
			throw new InvalidNumberOfArgsException(2, this.args.size());
		}
		
		return true;
	}

	@Override
	public String exec() {
		LOGGER.info("{}: exec command - {}", this.remoteAddress, Constants.FILE_DOWNLOAD_CMD);
		return Constants.RESPONSE_SUCCESS_MSG;
	}

}
