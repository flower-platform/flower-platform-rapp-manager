package org.flowerplatform.rapp_manager.test.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.flowerplatform.rapp_manager.SourceFileDto;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class Util {

	public static SourceFileDto getTestResourceContent(String resourceFileName) throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = classloader.getResourceAsStream(resourceFileName);
		
		return new SourceFileDto(
				resourceFileName, 
				IOUtils.toString(inputStream, "UTF-8")
		);
	}

}
