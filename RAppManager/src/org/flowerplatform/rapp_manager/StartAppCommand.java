package org.flowerplatform.rapp_manager;

import java.io.IOException;

import org.flowerplatform.tiny_http_server.IHttpCommand;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class StartAppCommand implements IHttpCommand {

	private String rAppName;
	
	public Object run() {
		if (rAppName == null) {
			throw new IllegalArgumentException("rApp name not specified");
		}
		Process p;
		try {
			System.out.print("Starting rApp: " + rAppName);
			String cmd = String.format("/opt/flower-platform/bin/start-app %s %s", System.getProperty("user.home"), rAppName);
			p = Runtime.getRuntime().exec(cmd);
			System.out.print("...");
			p.waitFor();
			if (p.exitValue() != 0) {
				System.out.println("failed");
				throw new RuntimeException("Error starting rApp: " + rAppName);
			} else {
				System.out.println("done");
			}
			return "rApp started: " + rAppName;
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
