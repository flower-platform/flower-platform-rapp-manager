package org.flowerplatform.rapp_manager.arduino_ide.command;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin;
import org.flowerplatform.rapp_manager.arduino_ide.IFlowerPlatformPluginAware;
import org.flowerplatform.rapp_manager.arduino_ide.util.Util;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;
import org.flowerplatform.tiny_http_server.ReflectionException;

import processing.app.Editor;

/**
 * Retrieves the log from Arduino IDE and sends it to the clients.
 * 
 * TODO : not implemented yet.
 * 
 * @author Andrei Taras
 */
public class GetLogCommand implements IHttpCommand, IFlowerPlatformPluginAware {
	
	private FlowerPlatformPlugin plugin;
	
	@Override
	public void setFlowerPlatformPlugin(FlowerPlatformPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public Object run() throws HttpCommandException {
		try {
			processing.app.EditorConsole editorConsole = Util.getPrivateField(Editor.class, plugin.getEditor(), "console");
			DefaultStyledDocument document = Util.getPrivateField(processing.app.EditorConsole.class, editorConsole, "document");
			
			return document.getText(0, document.getLength());
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
			throw new ReflectionException("Reflection error : " + e.getMessage(), e);
		} catch (BadLocationException ble) {
			throw new HttpCommandException("Error while attempting to read console.");
		}
	}
}
