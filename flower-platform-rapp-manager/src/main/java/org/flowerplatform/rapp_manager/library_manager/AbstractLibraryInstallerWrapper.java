package org.flowerplatform.rapp_manager.library_manager;

import java.util.List;

import org.flowerplatform.rapp_manager.library_manager.Library;

/**
 * Needed because between 1.6.5 and 1.6.6 the signature was modified
 * 
 * @author Cristian Spiescu
 */
public abstract class AbstractLibraryInstallerWrapper {
	
	abstract public void remove(Library lib) throws Exception;
	abstract public void install(Library lib, Library replacedLib) throws Exception;
	
	public AbstractLibraryInstallerWrapper() {
		super();
		initLibraryInstaller();
	}
	abstract public List<Library> getInstalledLibraries();
	
	abstract protected void initLibraryInstaller();
	
	abstract public Library createLibrary();
}
