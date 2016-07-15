package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.log;
import static org.flowerplatform.rapp_manager.linux.Main.logp;

import java.io.IOException;

import org.flowerplatform.rapp_manager.command.AbstractRappCommand;
import org.flowerplatform.rapp_manager.linux.Constants;
import org.flowerplatform.rapp_manager.linux.Util;
import org.flowerplatform.tiny_http_server.HttpCommandException;

/**
 * @author Claudiu Matei
 */
public class StopCommand extends AbstractRappCommand {

	private static final String STOP_APP_COMMAND = Constants.BIN_PATH + "/stop-app %s %s";
	
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
			String cmd = String.format(STOP_APP_COMMAND, Constants.WORK_DIR, rappId);
			p = Runtime.getRuntime().exec(cmd);
			logp("...");
			p.waitFor();
			if (p.exitValue() != 0) {
				log("failed");
				throw new RuntimeException("Error stopping rapp: " + rappId);
			} else {
				log("done");
			}
			return "Rapp stopped: " + rappId;
		} catch (IOException | InterruptedException e) {
			return e.getMessage();
		}
	}
}
