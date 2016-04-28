package org.flowerplatform.rapp_manager.arduino_ide.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.flowerplatform.rapp_manager.SourceFileDto;
import org.flowerplatform.rapp_manager.arduino_ide.command.CompileCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.UpdateSourceFilesCommand;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.junit.Test;

/**
 * Tests related to the compilation process in Arduino IDE
 * 
 * @author Silviu Negoita
 */
public class UpdateSourceAndCompileTest {

	private static SourceFileDto getTestResourceContent(String resourceFileName) throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = classloader.getResourceAsStream(resourceFileName);
		
		return new SourceFileDto(
				resourceFileName, 
				IOUtils.toString(inputStream, "UTF-8")
		);
	}

	/**
	 * Tests that Arduino IDE correctly fails the compilation process if given invalid CPP code.
	 * 
	 * @throws HttpCommandException
	 * @throws IOException
	 */
	@Test(expected=HttpCommandException.class)
	public void testCompileFails() throws HttpCommandException, IOException {
		SourceFileDto fileToTest = getTestResourceContent("simpleNotCompile.ino");
		
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
		SourceFileDto fileToTest = getTestResourceContent("simpleCompile.ino");
		
		UpdateSourceFilesCommand testUpdateSourceFiles = new UpdateSourceFilesCommand();
		testUpdateSourceFiles.setFiles(Collections.singletonList(fileToTest));
		testUpdateSourceFiles.setFlowerPlatformPlugin(TestSuiteArduinoIde.flowerPlatformPlugin);
		testUpdateSourceFiles.run();
		
		CompileCommand testCompile = new CompileCommand();
		testCompile.setFlowerPlatformPlugin(TestSuiteArduinoIde.flowerPlatformPlugin);
		testCompile.run();
	}
}
