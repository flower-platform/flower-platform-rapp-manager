package org.flowerplatform.rapp_manager.linux.test;

import java.io.IOException;

import org.flowerplatform.rapp_manager.linux.command.RunCommand;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.junit.Test;

public class Run {

	@Test
	public void test() throws HttpCommandException, IOException, ReflectiveOperationException, SecurityException {
		RunCommand cmdRun = new RunCommand();
		cmdRun.setRappName("TestApp");
		cmdRun.run();
	}

}
