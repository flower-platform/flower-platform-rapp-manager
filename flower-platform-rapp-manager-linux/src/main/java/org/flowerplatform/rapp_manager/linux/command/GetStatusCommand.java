package org.flowerplatform.rapp_manager.linux.command;

import org.flowerplatform.rapp_manager.SimpleStatus;
import org.flowerplatform.rapp_manager.linux.Util;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;


/**
 * Returns the global status of the application (i.e. general indicator such as ok/not ok).
 * 
 * @author Claudiu Matei
 */
public class GetStatusCommand implements IHttpCommand {
	public Object run() throws HttpCommandException {
		SimpleStatus status = new SimpleStatus();
		Util.fillSimpleStatus(status);
		
		return status;
	}

	
}
