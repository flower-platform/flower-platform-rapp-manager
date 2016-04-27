package org.flowerplatform.rapp_manager.arduino_ide.command;

import org.flowerplatform.tiny_http_server.FlexResponse;
import org.flowerplatform.tiny_http_server.IHttpCommand;

/**
 * Heartbeat command, allowing a client to verify if the Arduino IDE is available or 
 * not (i.e. started). 
 * 
 * @author Andrei Taras
 */
public class HeartbeatCommand implements IHttpCommand {
	
	public static final String MESSAGE_OK = "Ok";
	
	@Override
	public Object run() {
		//Don't actually do anything, but return a simple status.
		return new FlexResponse(FlexResponse.CODE_OK, MESSAGE_OK);
	}

}
