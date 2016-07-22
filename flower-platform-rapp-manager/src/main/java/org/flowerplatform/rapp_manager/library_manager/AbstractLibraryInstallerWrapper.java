package org.flowerplatform.rapp_manager.library_manager;

import java.util.List;

/**
 * Needed because between 1.6.5 and 1.6.6 the signature was modified
 * 
 * @author Cristian Spiescu
 */
public abstract class AbstractLibraryInstallerWrapper {
	
	public abstract void remove(Library lib) throws Exception;
	public abstract void install(Library lib, Library replacedLib) throws Exception;
	
	public AbstractLibraryInstallerWrapper() {
		super();
		initLibraryInstaller();
	}
	public abstract List<Library> getInstalledLibraries();
	
	protected abstract void initLibraryInstaller();
	
	public abstract Library createLibrary();
}
