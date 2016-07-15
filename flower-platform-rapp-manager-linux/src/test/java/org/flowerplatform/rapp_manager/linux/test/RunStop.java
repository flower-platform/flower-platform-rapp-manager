package org.flowerplatform.rapp_manager.linux.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import org.flowerplatform.rapp_manager.linux.Constants;
import org.flowerplatform.rapp_manager.linux.Util;
import org.flowerplatform.rapp_manager.linux.command.RunCommand;
import org.flowerplatform.rapp_manager.linux.command.StopCommand;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.junit.Assert;
import org.junit.Test;

public class RunStop {

	@Test
	public void testRunAndStopCommands() throws HttpCommandException, IOException, ReflectiveOperationException, SecurityException, InterruptedException {
		RunCommand cmdRun = new RunCommand();
		cmdRun.setRappId("TestApp");
		cmdRun.run();
		try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace(); }
		
		File f;
		
		f = new File(String.format(Constants.PID_FILE_PATTERN, "TestApp"));
		Assert.assertTrue("PID file was created", f.exists());

		int pid = -1;
		try (Scanner scanner = new Scanner(new FileInputStream(f))) {
			pid = scanner.nextInt();	
		}

		Assert.assertTrue("Process is running", Util.isProcessRunning(pid));
		
		f = new File(String.format(Constants.LOG_FILE_PATTERN, "TestApp"));
		Assert.assertTrue("Log file was created", f.exists());
		
		try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace(); }
		
		StopCommand cmdStop = new StopCommand();
		cmdStop.setRappId("TestApp");
		cmdStop.run();
		
		try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace(); }

		f = new File(String.format(Constants.PID_FILE_PATTERN, "TestApp"));
		Assert.assertFalse("PID file was deleted", f.exists());
		
		Assert.assertFalse("Process is stopped", Util.isProcessRunning(pid));
		
	}

}
