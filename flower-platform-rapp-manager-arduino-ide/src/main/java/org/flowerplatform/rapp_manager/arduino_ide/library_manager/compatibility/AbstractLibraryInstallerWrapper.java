package org.flowerplatform.rapp_manager.arduino_ide.library_manager.compatibility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin;
import org.flowerplatform.rapp_manager.arduino_ide.library_manager.Library;

import cc.arduino.contributions.libraries.ContributedLibrary;
import cc.arduino.contributions.libraries.LibraryInstaller;
import processing.app.Base;
import processing.app.BaseNoGui;
import processing.app.packages.LibraryList;
import processing.app.packages.UserLibrary;

/**
 * Needed because between 1.6.5 and 1.6.6 the signature was modified
 * 
 * @author Cristian Spiescu
 */
public abstract class AbstractLibraryInstallerWrapper {
	
	protected LibraryInstaller installer;
	
	abstract public void remove(ContributedLibrary lib) throws Exception;
	abstract public void install(ContributedLibrary lib, ContributedLibrary replacedLib) throws Exception;
	
	public AbstractLibraryInstallerWrapper() {
		super();
		initLibraryInstaller();
	}
	
	abstract protected void initLibraryInstaller();
	

	public List<Library> getInstalledLibraries()  {
		List<Library> result = new ArrayList<Library>();
		LibraryList allLibraries = Base.INSTANCE.getIDELibs();
		
		for (UserLibrary uLib: Base.INSTANCE.getUserLibs()) {
			allLibraries.add(uLib);
		}
 		for(UserLibrary uLib : allLibraries) {
 			Library lib = new Library();
 			lib.setName(uLib.getName());
 			lib.setVersion(uLib.getVersion());
 			lib.setUrl(uLib.getWebsite());
 			lib.setUserLibrary(uLib);
 			try {
				lib.setHeaderFiles(Base.headerListFromIncludePath(uLib.getSrcFolder()));
			} catch (IOException e) {
				FlowerPlatformPlugin.log("Cannot get header files for dir = " + uLib.getSrcFolder(), e);
			}
 			result.add(lib);
 		}
 		return result;
	}
}