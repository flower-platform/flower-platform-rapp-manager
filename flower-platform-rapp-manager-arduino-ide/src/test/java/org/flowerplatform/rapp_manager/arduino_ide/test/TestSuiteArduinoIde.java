package org.flowerplatform.rapp_manager.arduino_ide.test;

import org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin;
import org.flowerplatform.rapp_manager.arduino_ide.test.utils.ArduinoIdeWrapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;

import processing.app.Base;

@RunWith(WildcardPatternSuite.class)
@SuiteClasses({"SynchronizePackageCommandTest.class", "!*TestSuiteArduinoIde.class"})
public class TestSuiteArduinoIde {

	public static FlowerPlatformPlugin flowerPlatformPlugin;

	@BeforeClass 
	public static void launchArduinoIde() {
		System.out.println("Starting Arduino ide.");
		ArduinoIdeWrapper.launchArduinoIde();

//		flowerPlatformPlugin = new FlowerPlatformPlugin();
//		while (Base.INSTANCE.getActiveEditor() == null) {
//			try { Thread.sleep(100); } catch (Exception e) { }
//		}
//		flowerPlatformPlugin.init(Base.INSTANCE.getActiveEditor());
	}

	@AfterClass
	public static void shutdownArduinoIde() {
		System.out.println("Stopping Arduino ide.");
		ArduinoIdeWrapper.stopArduinoIde();
	}
}
