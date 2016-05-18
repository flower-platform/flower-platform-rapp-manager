package org.flowerplatform.rapp_manager.linux.test;

import java.io.IOException;
import java.util.Collections;

import org.flowerplatform.rapp_manager.SourceFileDto;
import org.flowerplatform.rapp_manager.linux.CompilationException;
import org.flowerplatform.rapp_manager.linux.command.CompileCommand;
import org.flowerplatform.rapp_manager.linux.command.UpdateSourceFilesCommand;
import org.flowerplatform.rapp_manager.test.util.Util;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.junit.Test;

/**
 * @author Claudiu Matei
 */
public class UpdateSourceFilesAndCompile {

	@Test
	public void testSuccessScenario() throws HttpCommandException, IOException {
		UpdateSourceFilesCommand cmdUpdateSourceFiles = new UpdateSourceFilesCommand();
		SourceFileDto file = Util.getTestResourceContent("RaspberryApp.py");
		cmdUpdateSourceFiles.setFiles((Collections.singletonList(file)));
		cmdUpdateSourceFiles.setRappName("RaspberryApp");
		cmdUpdateSourceFiles.run();
		
		CompileCommand cmdCompile = new CompileCommand();
		cmdCompile.setRappName("RaspberryApp");
		cmdCompile.run();
	}
	
	@Test(expected=CompilationException.class)
	public void testFailScenario() throws HttpCommandException, IOException {
		UpdateSourceFilesCommand cmdUpdateSourceFiles = new UpdateSourceFilesCommand();
		SourceFileDto file = Util.getTestResourceContent("RaspberryAppCompilationError.py");
		cmdUpdateSourceFiles.setFiles((Collections.singletonList(file)));
		cmdUpdateSourceFiles.setRappName("RaspberryAppCompilationError");
		cmdUpdateSourceFiles.run();
		
		CompileCommand cmdCompile = new CompileCommand();
		cmdCompile.setRappName("RaspberryAppCompilationError");
		cmdCompile.run();
	}

}
