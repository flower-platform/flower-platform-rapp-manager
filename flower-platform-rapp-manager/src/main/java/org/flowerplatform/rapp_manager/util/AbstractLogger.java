package org.flowerplatform.rapp_manager.util;

/**
 * Abstract logger class to be extended by Arduino rapp manager or raspbery rapp manager
 * @author Silviu Negoita
 */
public abstract class AbstractLogger {
	
	public abstract void log(String message);
	
	public abstract void log(String message, Throwable t);
}
