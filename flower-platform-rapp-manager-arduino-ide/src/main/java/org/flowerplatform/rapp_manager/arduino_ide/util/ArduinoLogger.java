package org.flowerplatform.rapp_manager.arduino_ide.util;

import org.flowerplatform.rapp_manager.util.AbstractLogger;

public class ArduinoLogger extends AbstractLogger {

	@Override
	public void log(String message) {
		System.out.println(message);		
	}
	
	@Override
	public void log(char c) {
		System.out.print(c);
	}

	@Override
	public void log(String message, Throwable t) {
		System.out.println(message);
		t.printStackTrace(System.out);
	}

	@Override
	public void log(String message, Object... args) {
		System.out.println(String.format(message, args));
	}

	@Override
	public void logSameLine(String message) {
		System.out.print(message);
	}
}
