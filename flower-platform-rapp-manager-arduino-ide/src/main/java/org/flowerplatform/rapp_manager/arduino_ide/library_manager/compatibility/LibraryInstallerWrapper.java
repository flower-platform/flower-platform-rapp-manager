package org.flowerplatform.rapp_manager.arduino_ide.library_manager.compatibility;

import java.io.IOException;
import java.lang.reflect.Field;

import org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin;

import cc.arduino.contributions.ConsoleProgressListener;
import cc.arduino.contributions.libraries.ContributedLibrary;
import cc.arduino.contributions.libraries.LibraryInstaller;
import processing.app.Base;

/**
 * Starting v1.6.6.
 * 
 * @author Cristian Spiescu
 */
public class LibraryInstallerWrapper extends AbstractLibraryInstallerWrapper {
	
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
	public void remove(ContributedLibrary lib) throws IOException {
		installer.remove(lib, new ConsoleProgressListener());
	}

	@Override
	public void install(ContributedLibrary lib, ContributedLibrary replacedLib) throws Exception {
		installer.install(lib, replacedLib, new ConsoleProgressListener());
	}

}
