package org.flowerplatform.rapp_manager.test.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.commons.io.IOUtils;
import org.flowerplatform.rapp_manager.SourceFileDto;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class TestUtil {

	public static SourceFileDto getTestResourceContent(String resourceFileName) throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = classloader.getResourceAsStream(resourceFileName);
		
		return new SourceFileDto(
				resourceFileName, 
				IOUtils.toString(inputStream, "UTF-8")
		);
	}

	public static void modifyFieldValue(Class<?> c, String fieldName, Object value, Object instance) throws ReflectiveOperationException, SecurityException {
		Field field = c.getDeclaredField(fieldName);
		field.setAccessible(true);
		
		Field fieldModifiers = Field.class.getDeclaredField("modifiers");
	    fieldModifiers.setAccessible(true);
	    fieldModifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	    
	    field.set(instance, value);
	}

}
