package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.log;
import static org.flowerplatform.rapp_manager.linux.Main.logp;

import java.io.IOException;

import org.flowerplatform.rapp_manager.command.AbstractRunCommand;


/**
 * 
 * @author Claudiu Matei
 *
 */
public class RunCommand extends AbstractRunCommand {

	private static final String START_APP_COMMAND = "/opt/flower-platform/bin/start-app %s %s";
	
	public Object run() {
		if (rAppName == null) {
			throw new IllegalArgumentException("rApp name not specified");
		}
		Process p;
		try {
			logp("Starting rApp: " + rAppName);
			String cmd = String.format(START_APP_COMMAND, System.getProperty("user.home"), rAppName);
			p = Runtime.getRuntime().exec(cmd);
			logp("...");
			p.waitFor();
			if (p.exitValue() != 0) {
				log("failed");
				throw new RuntimeException("Error starting rApp: " + rAppName);
			} else {
				log("done");
			}
			return "rApp started: " + rAppName;
		} catch (IOException | InterruptedException e) {
			return e.getMessage();
		}
	}

}
