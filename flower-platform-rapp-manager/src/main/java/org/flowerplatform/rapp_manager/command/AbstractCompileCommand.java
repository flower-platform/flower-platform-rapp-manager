package org.flowerplatform.rapp_manager.command;

import org.flowerplatform.tiny_http_server.IHttpCommand;

public abstract class AbstractCompileCommand implements IHttpCommand {
	
	protected String rappName;

	public String getRappName() {
		return rappName;
	}

	public void setRappName(String rappName) {
		this.rappName = rappName;
	}
	
}
