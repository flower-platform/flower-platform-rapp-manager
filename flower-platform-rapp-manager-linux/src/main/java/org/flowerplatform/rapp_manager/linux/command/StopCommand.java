package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.log;
import static org.flowerplatform.rapp_manager.linux.Main.logp;

import java.io.IOException;

import org.flowerplatform.rapp_manager.linux.Constants;
import org.flowerplatform.rapp_manager.linux.Util;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;


/**
 * 
 * @author Claudiu Matei
 *
 */
public class StopCommand implements IHttpCommand {

	private static final String STOP_APP_COMMAND = Constants.BIN_PATH + "/stop-app %s %s";
	
	private String rappName;
	
	public Object run() throws HttpCommandException {
		if (rappName == null) {
			throw new IllegalArgumentException("Rapp name not specified");
		}

		// check if rapp is already running
		try {
			if (!Util.isRappRunning(rappName)) {
				throw new HttpCommandException("Rapp is not running.");
			}
		} catch (IOException | InterruptedException e) {
			throw new HttpCommandException(e.getMessage(), e);
		}
		
		Process p;
		try {
			logp("Stopping rapp: " + rappName);
			String cmd = String.format(STOP_APP_COMMAND, Constants.WORK_DIR, rappName);
			p = Runtime.getRuntime().exec(cmd);
			logp("...");
			p.waitFor();
			if (p.exitValue() != 0) {
				log("failed");
				throw new RuntimeException("Error stopping rapp: " + rappName);
			} else {
				log("done");
			}
			return "Rapp stopped: " + rappName;
		} catch (IOException | InterruptedException e) {
			return e.getMessage();
		}
	}

	public String getRappName() {
		return rappName;
	}

	public void setRappName(String rappName) {
		this.rappName = rappName;
	}
	
}
