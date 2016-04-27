package org.flowerplatform.rapp_manager.arduino_ide.util;

/**
 * Simple listener, which gets invoked both when "something" (a runnable, a process, etc) starts,
 * and when it ends.
 * 
 * @author Andrei Taras
 */
public interface StartEndListener {
	void start();
	void end();
}
