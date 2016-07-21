package org.flowerplatform.rapp_manager.arduino_ide.library_manager.compatibility;

import java.io.IOException;
import java.lang.reflect.Field;

import org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin;
import org.flowerplatform.rapp_manager.arduino_ide.library_manager.ArduinoLibrary;
import org.flowerplatform.rapp_manager.arduino_ide.library_manager.RequiredLibraryWrapper;
import org.flowerplatform.rapp_manager.library_manager.Library;

import cc.arduino.contributions.ConsoleProgressListener;
import cc.arduino.contributions.libraries.LibraryInstaller;
import processing.app.Base;

/**
 * Starting v1.6.6.
 * 
 * @author Cristian Spiescu
 */
public class LibraryInstallerWrapperArduino extends AbstractLibraryInstallerWrapperArduino {
	
	
	protected void initLibraryInstaller() {
		try {
			Field libraryInstallerField = Base.class.getDeclaredField("libraryInstaller");
			libraryInstallerField.setAccessible(true);
			installer = (LibraryInstaller) libraryInstallerField.get(Base.INSTANCE);
		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			FlowerPlatformPlugin.log("Error while obtaining LibraryInstaller", e);
		}
	}

	@Override
	public void remove(Library lib) throws IOException {
		installer.remove(((ArduinoLibrary) lib).getUserLibrary(), new ConsoleProgressListener());
	}

	@Override
	public void install(Library lib, Library replacedLib) throws Exception {
		installer.install(new RequiredLibraryWrapper(lib), ((ArduinoLibrary) replacedLib).getUserLibrary(), new ConsoleProgressListener());
	}

	@Override
	public Library createLibrary() {
		return new ArduinoLibrary();
	}
}
