package org.flowerplatform.rapp_manager.arduino_ide.command;

import java.util.List;

import org.flowerplatform.rapp_manager.arduino_ide.library_manager.Library;
import org.flowerplatform.rapp_manager.arduino_ide.library_manager.MatchedLibraries;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;

public class SynchronizeLibrariesCommand implements IHttpCommand{
	
	List<Library> requiredLibraries;
	boolean dryRun;
		
	@Override
	public Object run() throws HttpCommandException {
		if(dryRun) {
			return analizeAndPopulate(requiredLibraries);
		} else {
			return applyActions(analizeAndPopulate(requiredLibraries));
		}
	}
	
	public List<MatchedLibraries> analizeAndPopulate(List<Library> requiredLibraries) {
		//TODO pentru fiecare library, populeaza library->userLibrary
		//TODO intoarce o lista de MatchedLibraries
		return null;
	}
	
	protected List<MatchedLibraries> applyActions(List<MatchedLibraries> entries) {
		//TODO pentru fiecare entries, aplica actiunea setata.
		//TODO intoarce entries. de vazut daca se schimba ceva in ele la rulare actiuni.
		return entries;
	}
	
}
