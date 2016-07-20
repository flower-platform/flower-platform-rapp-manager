package org.flowerplatform.rapp_manager.arduino_ide.test;
import java.util.List;

import org.flowerplatform.rapp_manager.arduino_ide.command.InstallPackageCommand;
import org.junit.Test;

public class InstallPackageCommandTest {
	private final String HC_PLATFORM_ARCHITECTURE = "esp8266";
	private final String HC_PLATFORM_VERSION = "2.2.0";
	private final String HC_PACK_NAME = "esp8266";
	private final String HC_PACK_URL = "http://arduino.esp8266.com/versions/2.2.0/package_esp8266com_index.json";
	
	@Test
	public void testInstall() throws Exception {
		// init command
		InstallPackageCommand installCommand = new InstallPackageCommand();
		installCommand.setPackageName(HC_PACK_NAME);
		installCommand.setPlatformArch(HC_PLATFORM_ARCHITECTURE);
		installCommand.setPlatformVersion(HC_PLATFORM_VERSION);
		installCommand.setPackageUrl(HC_PACK_URL);
		installCommand.setToInstall(true);
		List<String> result = (List<String>) installCommand.run();
		if (result == null) {
			System.out.println("TEST INSTALL FAILED");
		} else {
			System.out.println("Test install passed ------------------------------------------------------" + result);
		}
//		syncCommand.run();
	}
}
