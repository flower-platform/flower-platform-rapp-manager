package org.flowerplatform.rapp_manager.arduino_ide.library_manager.compatibility;

import java.io.File;
import java.io.IOException;

import cc.arduino.contributions.libraries.ContributedLibrary;
import cc.arduino.contributions.libraries.LibraryInstaller;
import processing.app.Base;
import processing.app.packages.LibraryList;

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
	

	protected LibraryList getInstalledLibraries() {
		//TODO metoda ce intoarce o lista de UserLibrary: Base.INSTANCE.getUserLibs()
		return Base.INSTANCE.getUserLibs();
	}
	protected String[] getheaderListFromIncludePath(File path) throws IOException {
		//TODO metoda folosita pentru analiare libs existente : return Base.headerListFromIncludePath(path);
		//TODO am pus aici pentru a putea fi mockuita
		return Base.headerListFromIncludePath(path);
	}
}
