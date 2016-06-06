package org.flowerplatform.rapp_manager.arduino_ide.test;

import java.io.IOException;
import java.util.Collections;

import org.flowerplatform.rapp_manager.SourceFileDto;
import org.flowerplatform.rapp_manager.arduino_ide.command.CompileCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.UpdateSourceFilesCommand;
import org.flowerplatform.rapp_manager.test.util.Util;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.junit.Test;

/**
 * Tests related to the compilation process in Arduino IDE
 * 
 * @author Silviu Negoita
 */
public class UpdateSourceAndCompileTest {

	/**
	 * Tests that Arduino IDE correctly fails the compilation process if given invalid CPP code.
	 * 
	 * @throws HttpCommandException
	 * @throws IOException
	 */
	@Test(expected=HttpCommandException.class)
	public void testCompileFails() throws HttpCommandException, IOException {
		SourceFileDto fileToTest = Util.getTestResourceContent("simpleNotCompile.ino");
		
		UpdateSourceFilesCommand testUpdateSourceFiles = new UpdateSourceFilesCommand();
		testUpdateSourceFiles.setFiles(Collections.singletonList(fileToTest));
		testUpdateSourceFiles.setFlowerPlatformPlugin(TestSuiteArduinoIde.flowerPlatformPlugin);
		testUpdateSourceFiles.run();
		CompileCommand testCompile = new CompileCommand();
		testCompile.setFlowerPlatformPlugin(TestSuiteArduinoIde.flowerPlatformPlugin);
		// This line should throw HttpCommandException.
		testCompile.run();
	}

	/**
	 * Tests that Arduino IDE correctly reports compilation as successful if given valid CPP code.
	 */
	@Test
	public void testCompileSucceeds() throws HttpCommandException, IOException {
		SourceFileDto fileToTest = Util.getTestResourceContent("simpleCompile.ino");
		
		UpdateSourceFilesCommand testUpdateSourceFiles = new UpdateSourceFilesCommand();
		testUpdateSourceFiles.setFiles(Collections.singletonList(fileToTest));
		testUpdateSourceFiles.setFlowerPlatformPlugin(TestSuiteArduinoIde.flowerPlatformPlugin);
		testUpdateSourceFiles.run();
		
		CompileCommand testCompile = new CompileCommand();
		testCompile.setFlowerPlatformPlugin(TestSuiteArduinoIde.flowerPlatformPlugin);
		testCompile.run();
	}

}
