package org.flowerplatform.rapp_manager.arduino_ide.test;

import org.flowerplatform.rapp_manager.arduino_ide.command.UploadToBoardCommand;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.junit.Test;

/**
 * This test should always fail due to missing board
 * @author Silviu Negoita
 *
 */
public class UploadToBoardFailTest {
	
	@Test(expected=HttpCommandException.class)
	public void upload() throws HttpCommandException {
		UploadToBoardCommand commandTest = new UploadToBoardCommand();
		commandTest.setFlowerPlatformPlugin(TestSuiteArduinoIde.flowerPlatformPlugin);
		// this command should fail
		commandTest.run();
		
	}
}
