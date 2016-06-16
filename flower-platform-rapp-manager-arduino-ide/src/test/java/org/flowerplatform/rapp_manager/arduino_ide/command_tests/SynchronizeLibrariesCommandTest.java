package org.flowerplatform.rapp_manager.arduino_ide.command_tests;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.flowerplatform.rapp_manager.arduino_ide.command.SynchronizeLibrariesCommand;
import org.flowerplatform.rapp_manager.arduino_ide.library_manager.Library;
import org.flowerplatform.rapp_manager.arduino_ide.library_manager.MatchedLibrary;
import org.flowerplatform.rapp_manager.arduino_ide.library_manager.MatchedLibrary.Status;
import org.flowerplatform.rapp_manager.arduino_ide.library_manager.compatibility.AbstractLibraryInstallerWrapper;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SynchronizeLibrariesCommandTest {
		
	MatchedLibrary.Status[] expectedStatus = {Status.OK, Status.UNKNOWN, Status.NEEDS_DOWNLOAD, Status.NEEDS_DOWNLOAD, Status.NEEDS_DOWNLOAD, Status.NEEDS_DOWNLOAD};
	List<Library> installedLibraries;
	List<Library> requiredLibraries;
	
	static public String[] headerListFromIncludePath(File path) throws IOException {
	    String[] list = path.list(new FilenameFilter() {
			
	    	public boolean accept(File dir, String name) {
	            if (name.endsWith(".h")) {
	              return true;
	            }
	            return false;
	          }
		});
	    
	    return list;
	  }
	
	@Before
	public void initData() {
		/******* init installed libraries *******/
		
		installedLibraries = new ArrayList<Library>();
		/** add H/C lib1 **/
		Library lib = new Library();
		lib.setHeaderFiles(new String[]{"file1.h", "file2.h"});
		lib.setName("lib1");
		lib.setVersion("1.1.0");
		installedLibraries.add(lib);
		
		/** add H/C lib2 **/
		lib = new Library();
		lib.setHeaderFiles(new String[]{"file3.h", "file4.h", "file5.h"});
		lib.setName("lib2");
		lib.setVersion("1.2.0");
		installedLibraries.add(lib);
		
		/** add H/C lib3 **/
		lib = new Library();
		lib.setHeaderFiles(new String[]{"file7.h"});
		lib.setName("lib3");	
		installedLibraries.add(lib);
		
		/******* init required libraries *******/
		
		requiredLibraries = new ArrayList<Library>();
		
		lib = new Library();		
		lib.setHeaderFiles(new String[]{"file1.h", "file2.h"});
		lib.setName("lib1");
		lib.setVersion("1.1.0");
		requiredLibraries.add(lib);	
		
		lib = new Library();		
		lib.setHeaderFiles(new String[]{"file2.h"});
		lib.setName("lib2");
		lib.setVersion("1.3.0");
		requiredLibraries.add(lib);
		
		lib = new Library();		
		lib.setHeaderFiles(new String[]{"file2.h"});
		lib.setName("lib2");
		lib.setVersion("1.1.0");
		requiredLibraries.add(lib);
		
		lib = new Library();		
		lib.setHeaderFiles(new String[]{"file6.h"});
		lib.setName("lib4");
		requiredLibraries.add(lib);
		
		lib = new Library();		
		lib.setHeaderFiles(new String[]{"file7.h"});
		lib.setName("lib3");
		requiredLibraries.add(lib);
		
		lib = new Library();		
		lib.setHeaderFiles(new String[]{"file8.h"});
		lib.setName("lib2");
		lib.setVersion("1.2.0");
		requiredLibraries.add(lib);
	}
	
	@Test
	public void synchronizeLibraries() throws IOException, HttpCommandException {
		SynchronizeLibrariesCommand synchronizeCommand = new SynchronizeLibrariesCommand();

		// set input for command 
		synchronizeCommand.setRequiredLibraries(requiredLibraries);
		synchronizeCommand.setDryRun(true);
				
		// Mocking the installer for test
		AbstractLibraryInstallerWrapper installer = Mockito.mock(AbstractLibraryInstallerWrapper.class);		
		when(installer.getInstalledLibraries()).thenReturn(installedLibraries);
		
		// inject mocked installer in command
		synchronizeCommand.setInstaller(installer);

		
		@SuppressWarnings("unchecked")
		List<MatchedLibrary> result = (List<MatchedLibrary>) synchronizeCommand.run();
		int i = 0;
		for(MatchedLibrary lib : result) { 
			assertEquals(lib.getStatus(),expectedStatus[i]);
			i++;
		}
		System.out.println("Test Passed");
	}
}
