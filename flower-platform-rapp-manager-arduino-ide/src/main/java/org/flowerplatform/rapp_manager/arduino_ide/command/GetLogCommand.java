package org.flowerplatform.rapp_manager.arduino_ide.command;

import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;

/**
 * Retrieves the log from Arduino IDE and sends it to the clients.
 * 
 * TODO : not implemented yet.
 * 
 * @author Andrei Taras
 */
public class GetLogCommand implements IHttpCommand {
	protected String rappName;

	@Override
	public Object run() throws HttpCommandException {
		return null;
	}

	public String getRappName() {
		return rappName;
	}
	public void setRappName(String rappName) {
		this.rappName = rappName;
	}
}
