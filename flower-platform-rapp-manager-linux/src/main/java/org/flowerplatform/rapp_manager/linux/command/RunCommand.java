package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.logger;

import java.io.IOException;

import org.flowerplatform.rapp_manager.command.AbstractRunCommand;
import org.flowerplatform.rapp_manager.linux.Constants;
import org.flowerplatform.rapp_manager.linux.FileUtils;
import org.flowerplatform.rapp_manager.linux.Util;
import org.flowerplatform.tiny_http_server.HttpCommandException;


/**
 * 
 * @author Claudiu Matei
 *
 */
public class RunCommand extends AbstractRunCommand {

	/**
	 * The format of the command line invocation :
	 * start-app home_dir rappId filesystemName
	 */
	private static final String START_APP_COMMAND = "/bin/bash " + Constants.BIN_PATH + "/start-app %s %s %s";
	
	public Object run() throws HttpCommandException {
		if (rappId == null) {
			throw new HttpCommandException("Rapp name not specified");
		}
		
		// check if rapp is already running
		try {
			if (Util.isRappRunning(rappId)) {
				throw new HttpCommandException("Rapp is already running.");
			}
		} catch (IOException | InterruptedException e) {
			throw new HttpCommandException(e.getMessage(), e);
		}
		
		// try to start rapp
		Process p;
		try {
			logger.logSameLine("Starting rapp: " + rappId);
			String cmd = String.format(START_APP_COMMAND, Constants.WORK_DIR, rappId, FileUtils.rappIdToFilesystemName(rappId));
			p = Runtime.getRuntime().exec(cmd);
			logger.logSameLine("...");
			p.waitFor();
			if (p.exitValue() != 0) {
				logger.log("failed");
				throw new HttpCommandException("Error starting rapp: " + rappId);
			} else {
				logger.log("done");
			}
			return "Rapp started: " + rappId;
		} catch (IOException | InterruptedException e) {
			throw new HttpCommandException(e.getMessage(), e);
		}
	}

}
