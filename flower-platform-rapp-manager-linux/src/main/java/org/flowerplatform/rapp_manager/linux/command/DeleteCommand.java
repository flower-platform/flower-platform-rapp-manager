package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.logger;

import java.io.File;
import java.io.IOException;

import org.flowerplatform.rapp_manager.command.AbstractRappCommand;
import org.flowerplatform.rapp_manager.linux.FileUtils;
import org.flowerplatform.tiny_http_server.HttpCommandException;

/**
 * 
 * @author Claudiu Matei
 */
public class DeleteCommand extends AbstractRappCommand {

	private static final String DELETE_COMMAND = "rm -rf %s";
	
	public Object run() throws HttpCommandException {
		if (rappId == null) {
			throw new HttpCommandException("Rapp name not specified");
		}
		
		logger.log("Deleting rapp: " + rappId);
		File rappDir = FileUtils.getRappDir(rappId);
		if (!rappDir.exists()) {
			HttpCommandException e = new HttpCommandException(String.format("Rapp not found: %s", rappId));
			logger.log(e.getMessage(), e);
			throw e;
		}
		Process p;
		try {
			p = Runtime.getRuntime().exec(String.format(DELETE_COMMAND, rappDir));
			p.waitFor();
			if (p.exitValue() != 0) {
				HttpCommandException e = new HttpCommandException("Error deleting rapp: " + rappId);
				logger.log (e.getMessage(), e);
				throw e;
			}
		} catch (IOException | InterruptedException ex) {
			HttpCommandException e = new HttpCommandException(ex.getMessage(), ex);
			logger.log(e.getMessage(), e);
			throw e;
		}
		return null;
	}
}
