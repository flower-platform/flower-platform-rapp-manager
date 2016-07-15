package org.flowerplatform.rapp_manager.command;

import org.flowerplatform.tiny_http_server.IHttpCommand;

/**
 * Base class for all commands that operate around the notion of "Rapp", and thus
 * need a rappId as parameter.
 *  
 * @author Andrei Taras
 */
public abstract class AbstractRappCommand implements IHttpCommand {
	protected String rappId;

	public String getRappId() {
		return rappId;
	}
	public void setRappId(String rappId) {
		this.rappId = rappId;
	}
}
