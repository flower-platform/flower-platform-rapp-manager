package org.flowerplatform.rapp_manager.command;

import org.flowerplatform.tiny_http_server.IHttpCommand;

public abstract class AbstractRunCommand implements IHttpCommand {

	protected String rAppName;

	public String getrAppName() {
		return rAppName;
	}

	public void setrAppName(String rAppName) {
		this.rAppName = rAppName;
	}
	
}
