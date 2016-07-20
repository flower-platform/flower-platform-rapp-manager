package org.flowerplatform.rapp_manager.arduino_ide.test;

import org.flowerplatform.rapp_manager.arduino_ide.command.CheckPackageIfInstalledCommand;
import org.junit.Test;

public class CheckPackageIfInstalledCommandTest {
	private final String HC_PLATFORM_ARCHITECTURE = "esp8266";
	private final String HC_PLATFORM_VERSION = "2.2.0";
	private final String HC_PACK_NAME = "esp8266";
	private final String HC_PACK_URL = "http://arduino.esp8266.com/versions/2.2.0/package_esp8266com_index.json";
	
	@Test
	public void test2() throws Exception {
		// init command
		CheckPackageIfInstalledCommand checkIfInstalledCommand = new CheckPackageIfInstalledCommand();
		checkIfInstalledCommand.setPackageName(HC_PACK_NAME);
		checkIfInstalledCommand.setPlatformArch(HC_PLATFORM_ARCHITECTURE);
		checkIfInstalledCommand.setPlatformVersion(HC_PLATFORM_VERSION);
		checkIfInstalledCommand.setPackageUrl(HC_PACK_URL);
		boolean result = (boolean) checkIfInstalledCommand.run();
		if (result) {
			System.out.println("Test passed - INSTALLED *************************************************************************************");
		} else {
			System.out.println("Not installed ---");
		}
	}
}
