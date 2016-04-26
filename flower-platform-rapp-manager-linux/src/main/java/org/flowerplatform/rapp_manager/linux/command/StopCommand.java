package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.log;
import static org.flowerplatform.rapp_manager.linux.Main.logp;

import java.io.IOException;

import org.flowerplatform.tiny_http_server.IHttpCommand;


/**
 * 
 * @author Claudiu Matei
 *
 */
public class StopCommand implements IHttpCommand {

	private static final String START_APP_COMMAND = "/opt/flower-platform/bin/stop-app %s %s";
	
	private String rAppName;
	
	public Object run() {
		if (rAppName == null) {
			throw new IllegalArgumentException("rApp name not specified");
		}
		Process p;
		try {
			logp("Stopping rApp: " + rAppName);
			String cmd = String.format(START_APP_COMMAND, System.getProperty("user.home"), rAppName);
			p = Runtime.getRuntime().exec(cmd);
			logp("...");
			p.waitFor();
			if (p.exitValue() != 0) {
				log("failed");
				throw new RuntimeException("Error stopping rApp: " + rAppName);
			} else {
				log("done");
			}
			return "rApp stopped: " + rAppName;
		} catch (IOException | InterruptedException e) {
			return e.getMessage();
		}
	}

	public String getrAppName() {
		return rAppName;
	}

	public void setrAppName(String board) {
		this.rAppName = board;
	}
	
}
