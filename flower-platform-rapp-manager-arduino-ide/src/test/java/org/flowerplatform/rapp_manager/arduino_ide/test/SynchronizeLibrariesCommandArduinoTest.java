package org.flowerplatform.rapp_manager.arduino_ide.test;

import java.util.Arrays;

import org.flowerplatform.rapp_manager.arduino_ide.command.SynchronizeLibrariesCommand;
import org.flowerplatform.rapp_manager.arduino_ide.library_manager.Library;
import org.flowerplatform.rapp_manager.arduino_ide.library_manager.compatibility.AbstractLibraryInstallerWrapper;
import org.flowerplatform.rapp_manager.arduino_ide.library_manager.compatibility.LibraryInstallerWrapper;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.junit.Test;

/**
 * In this test we check if any error comes up when invoke Ardunio IDE commands needed by synchronize command.
 * @author Silviu Negoita
 *
 */
public class SynchronizeLibrariesCommandArduinoTest {
	@Test
	public void test() throws HttpCommandException {
		// check 
		AbstractLibraryInstallerWrapper installer = new LibraryInstallerWrapper();
		// First we install correct library
		Library toSend = new Library();
		toSend.setHeaderFiles(new String[] {"DHTSensor.h"});
		toSend.setName("DHT sensor library");
		toSend.setUrl("https://github.com/flower-platform/DHT-sensor-library/archive/master.zip");
		toSend.setVersion("1.2.3");
		
		SynchronizeLibrariesCommand syncLibCommand = new SynchronizeLibrariesCommand();
		syncLibCommand.setRequiredLibraries(Arrays.asList(toSend));
		syncLibCommand.setInstaller(installer);
		syncLibCommand.run();
		
		// Secondly we rename to library to suggest remove and rerun test.
		toSend.setName("DHT sensor library_wrong");
		syncLibCommand.setRequiredLibraries(Arrays.asList(toSend));
		syncLibCommand.run();
	}
}
