package org.flowerplatform.rapp_manager.arduino_ide.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.flowerplatform.rapp_manager.arduino_ide.command.CheckPackageIfInstalledCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.InstallPackageCommand;
import org.junit.Before;
import org.junit.Test;

public class SynchronizePackageCommandTest {
	private final String HC_PLATFORM_ARCHITECTURE = "esp8266";
	private final String HC_PLATFORM_VERSION = "2.2.0";
	private final String HC_PACK_NAME = "esp8266";
	private final String HC_PACK_URL = "http://arduino.esp8266.com/versions/2.2.0/package_esp8266com_index.json";
	
	CheckPackageIfInstalledCommand checkPackageIfInstalledCommand;
	InstallPackageCommand installPackageCommand;
	
	@Before 
	public void initCommands() {
		installPackageCommand = new InstallPackageCommand();
		installPackageCommand.setPackageName(HC_PACK_NAME);
		installPackageCommand.setPlatformArch(HC_PLATFORM_ARCHITECTURE);
		installPackageCommand.setPlatformVersion(HC_PLATFORM_VERSION);
		installPackageCommand.setPackageUrl(HC_PACK_URL);
		installPackageCommand.setToInstall(true);
		
		checkPackageIfInstalledCommand = new CheckPackageIfInstalledCommand();
		checkPackageIfInstalledCommand.setPackageName(HC_PACK_NAME);
		checkPackageIfInstalledCommand.setPlatformArch(HC_PLATFORM_ARCHITECTURE);
		checkPackageIfInstalledCommand.setPlatformVersion(HC_PLATFORM_VERSION);
		checkPackageIfInstalledCommand.setPackageUrl(HC_PACK_URL);
	}
	
	@Test
	public void testInstall() throws Exception {
		String[] installResult;
		boolean checkResult;
		
		checkResult = (boolean) checkPackageIfInstalledCommand.run();
		// if package is not installed
		if (!checkResult) {
			installResult = (String[]) installPackageCommand.run();
			if (installResult == null) {
				fail("Return of the install command should not be null");
			}
			checkResult = (boolean) checkPackageIfInstalledCommand.run();
			System.out.println("RESULT LA CHECK :" + checkResult);
			assertEquals(checkResult, true);
		} { // if package already installed
			// we just run the instalation command and hope that we dont get null
			installResult = (String[]) installPackageCommand.run();
			if (installResult == null) {
				fail("Return of the install command should not be null");
			}
		}
	}
}
