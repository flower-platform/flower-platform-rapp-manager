package org.flowerplatform.rapp_manager.arduino_ide.test;

import org.flowerplatform.rapp_manager.arduino_ide.command.SynchronizePackagesCommand;
import org.junit.Test;

public class SynchronizePackagesTest {
	private final String HC_PLATFORM_ARCHITECTURE = "esp8266";
	private final String HC_PLATFORM_VERSION = "2.1.0";
	private final String HC_PACK_NAME = "esp8266";
	private final String HC_PACK_URL = "http://arduino.esp8266.com/versions/2.2.0/package_esp8266com_index.json";
	
	@Test
	public void test2() throws Exception {
		// init command
		SynchronizePackagesCommand syncCommand = new SynchronizePackagesCommand();
		syncCommand.setPackageName(HC_PACK_NAME);
		syncCommand.setPlatformArch(HC_PLATFORM_ARCHITECTURE);
		syncCommand.setPlatformVersion(HC_PLATFORM_VERSION);
		syncCommand.setPackageUrl(HC_PACK_URL);
		// try to install. it will fail if something is wrong
		syncCommand.setToInstall(true);
		syncCommand.run();
		// try to remove. it will fail if something is wrong
		syncCommand.setToInstall(false);
		syncCommand.run();
		
		
	}
}
