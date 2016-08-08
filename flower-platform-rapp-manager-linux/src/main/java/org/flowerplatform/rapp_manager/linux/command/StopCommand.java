package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.log;
import static org.flowerplatform.rapp_manager.linux.Main.logp;

import java.io.IOException;

import org.flowerplatform.rapp_manager.command.AbstractRappCommand;
import org.flowerplatform.rapp_manager.linux.Constants;
import org.flowerplatform.rapp_manager.linux.FileUtils;
import org.flowerplatform.rapp_manager.linux.Util;
import org.flowerplatform.tiny_http_server.HttpCommandException;

/**
 * @author Claudiu Matei
 */
public class StopCommand extends AbstractRappCommand {

	/**
	 * The format of the command line invocation :
	 * stop-app home_dir rappId filesystemName
	 */
	private static final String STOP_APP_COMMAND = "/bin/bash " + Constants.BIN_PATH + "/stop-app %s %s %s";
	
	public Object run() throws HttpCommandException {
		if (rappId == null) {
			throw new IllegalArgumentException("Rapp name not specified");
		}

		// check if rapp is already running
		try {
			if (!Util.isRappRunning(rappId)) {
				throw new HttpCommandException("Rapp is not running.");
			}
		} catch (IOException | InterruptedException e) {
			throw new HttpCommandException(e.getMessage(), e);
		}
		
		Process p;
		try {
			logp("Stopping rapp: " + rappId);
			String cmd = String.format(STOP_APP_COMMAND, Constants.WORK_DIR, rappId, FileUtils.rappIdToFilesystemName(rappId));
			p = Runtime.getRuntime().exec(cmd);
			logp("...");
			
			p.waitFor();
			if (p.exitValue() != 0) {
				String processOutput = Util.getPrettyProcessOutput(p);
				log("Failed to stop rapp " + rappId + ". Startup script terminated abnormally with exit code " + p.exitValue() + "; output from the script was " + processOutput);
				throw new HttpCommandException("Error stopping rapp " + rappId + "\n" + processOutput);
			} else {
				log("Successfully stopped rapp " + rappId);
			}
			return "Rapp stopped: " + rappId;
		} catch (IOException | InterruptedException e) {
			log("Error stopping app", e);
			throw new HttpCommandException(e.getMessage());
		}
	}
}
