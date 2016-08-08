package org.flowerplatform.rapp_manager.arduino_ide.command;

import org.flowerplatform.rapp_manager.SimpleStatus;
import org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin;
import org.flowerplatform.rapp_manager.arduino_ide.IFlowerPlatformPluginAware;
import org.flowerplatform.tiny_http_server.IHttpCommand;

import processing.app.BaseNoGui;

/**
 * Heartbeat/Status command, allowing a client to verify if the Arduino IDE is available or 
 * not (i.e. started).
 * 
 * @author Andrei Taras
 */
public class GetStatusCommand implements IHttpCommand, IFlowerPlatformPluginAware {

	public static final String MESSAGE_OK = "Ok";

	private FlowerPlatformPlugin plugin;

	@Override
	public void setFlowerPlatformPlugin(FlowerPlatformPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public Object run() {
		SimpleStatus status = new SimpleStatus();
		status.setVersion(plugin.getVersion());
		status.setHostSystemName("Arduino IDE");
		status.setHostSystemVersion(BaseNoGui.VERSION_NAME);
		
		return status;
	}
}
