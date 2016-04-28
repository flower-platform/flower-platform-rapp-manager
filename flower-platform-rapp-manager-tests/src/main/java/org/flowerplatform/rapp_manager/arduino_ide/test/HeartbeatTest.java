package org.flowerplatform.rapp_manager.arduino_ide.test;

import static org.junit.Assert.assertEquals;

import org.flowerplatform.rapp_manager.arduino_ide.command.HeartbeatCommand;
import org.flowerplatform.tiny_http_server.FlexResponse;
import org.junit.Test;

/**
 * Test if HeartBeat command returns STATUS_OK
 * @author Silviu Negoita
 *
 */
public class HeartbeatTest {
	
	@Test
	public void heartbeat() {
		HeartbeatCommand hCommand = new HeartbeatCommand();
		FlexResponse result = (FlexResponse) hCommand.run();
		
		assertEquals(result.getMessage(), HeartbeatCommand.MESSAGE_OK);
	}
}
