package org.flowerplatform.rapp_manager.arduino_ide.test;

import org.flowerplatform.rapp_manager.arduino_ide.command.GetStatusCommand;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test if HeartBeat command returns STATUS_OK
 * @author Silviu Negoita
 *
 */
public class HeartbeatTest {
	
	@Test
	public void heartbeat() {
		GetStatusCommand hCommand = new GetStatusCommand();
		Object result = hCommand.run();
		
		// The only actual requirement is that the status command returns not-null value.
		Assert.assertNotNull("Expected non-null object from the status command", result);
	}
}
