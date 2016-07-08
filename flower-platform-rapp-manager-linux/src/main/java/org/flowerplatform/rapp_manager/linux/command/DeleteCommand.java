package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.log;

import java.io.File;
import java.io.IOException;

import org.flowerplatform.rapp_manager.linux.Constants;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;


/**
 * 
 * @author Claudiu Matei
 *
 */
public class DeleteCommand implements IHttpCommand {

	private static final String DELETE_COMMAND = "rm -rf %s";
	
	private String rappName;
	
	public Object run() throws HttpCommandException {
		log("Deleting rapp: " + rappName);
		if (rappName == null) {
			throw new HttpCommandException("Rapp name not specified");
		}
		File rappDir = new File(String.format(Constants.RAPP_DIR_PATTERN, System.getProperty("user.home"), rappName));
		if (!rappDir.exists()) {
			HttpCommandException e = new HttpCommandException(String.format("Rapp not found: %s", rappName));
			log(e.getMessage(), e);
			throw e;
		}
		Process p;
		try {
			p = Runtime.getRuntime().exec(String.format(DELETE_COMMAND, rappDir));
			p.waitFor();
			if (p.exitValue() != 0) {
				HttpCommandException e = new HttpCommandException("Error deleting rapp: " + rappName);
				log (e.getMessage(), e);
				throw e;
			}
		} catch (IOException | InterruptedException ex) {
			HttpCommandException e = new HttpCommandException(ex.getMessage(), ex);
			log(e.getMessage(), e);
			throw e;
		}
		return null;
	}

	public String getRappName() {
		return rappName;
	}

	public void setRappName(String board) {
		this.rappName = board;
	}
	
}
