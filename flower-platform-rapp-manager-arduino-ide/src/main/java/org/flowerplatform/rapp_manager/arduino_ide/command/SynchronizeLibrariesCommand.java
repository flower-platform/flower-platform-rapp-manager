package org.flowerplatform.rapp_manager.arduino_ide.command;

import java.util.List;

import org.flowerplatform.rapp_manager.arduino_ide.library_manager.Library;
import org.flowerplatform.rapp_manager.arduino_ide.library_manager.MatchedLibraries;
import org.flowerplatform.rapp_manager.arduino_ide.library_manager.compatibility.AbstractLibraryInstallerWrapper;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SynchronizeLibrariesCommand implements IHttpCommand{
	
	List<Library> requiredLibraries;
	boolean dryRun;
	
	@JsonIgnore
	protected transient AbstractLibraryInstallerWrapper installer;
	
	

	@Override
	public Object run() throws HttpCommandException {
		if(dryRun) {
			return analizeAndPopulate(requiredLibraries);
		} else {
			List<MatchedLibraries> result = analizeAndPopulate(requiredLibraries);
			applyActions(result);
			return result;
		}
	}
	
	public List<MatchedLibraries> analizeAndPopulate(List<Library> requiredLibraries) {
		//TODO pentru fiecare library, populeaza library->userLibrary
		//TODO intoarce o lista de MatchedLibraries
		//TODO apeleaza installer.getInstalledLibraries()
		return null;
	}
	
	protected void applyActions(List<MatchedLibraries> entries) {
		//TODO pentru fiecare entries, aplica actiunea setata.
	}
	
	@JsonIgnore
	public void setInstaller(AbstractLibraryInstallerWrapper installer) {
		//TODO prin aceasta metoda se face injectarea isntallerului.
		this.installer = installer;
	}
}
