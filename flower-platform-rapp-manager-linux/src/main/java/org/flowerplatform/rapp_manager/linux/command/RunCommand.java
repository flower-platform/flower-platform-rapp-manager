package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.log;
import static org.flowerplatform.rapp_manager.linux.Main.logp;

import java.io.IOException;

import org.flowerplatform.rapp_manager.command.AbstractRunCommand;
import org.flowerplatform.rapp_manager.linux.Constants;
import org.flowerplatform.rapp_manager.linux.Util;
import org.flowerplatform.tiny_http_server.HttpCommandException;


/**
 * 
 * @author Claudiu Matei
 *
 */
public class RunCommand extends AbstractRunCommand {

	private static final String START_APP_COMMAND = Constants.BIN_PATH + "/start-app %s %s";
	
	public Object run() throws HttpCommandException {
		if (rappName == null) {
			throw new HttpCommandException("Rapp name not specified");
		}
		
		// check if rapp is already running
		try {
			if (Util.isRappRunning(rappName)) {
				throw new HttpCommandException("Rapp is already running.");
			}
		} catch (IOException | InterruptedException e) {
			throw new HttpCommandException(e.getMessage(), e);
		}
		
		// try to start rapp
		Process p;
		try {
			logp("Starting rapp: " + rappName);
			String cmd = String.format(START_APP_COMMAND, System.getProperty("user.home"), rappName);
			System.out.println("Executing cmd: " + cmd);
			p = Runtime.getRuntime().exec(cmd);
			logp("...");
			p.waitFor();
			if (p.exitValue() != 0) {
				log("failed");
				throw new HttpCommandException("Error starting rapp: " + rappName);
			} else {
				log("done");
			}
			return "Rapp started: " + rappName;
		} catch (IOException | InterruptedException e) {
			throw new HttpCommandException(e.getMessage(), e);
		}
	}

}
