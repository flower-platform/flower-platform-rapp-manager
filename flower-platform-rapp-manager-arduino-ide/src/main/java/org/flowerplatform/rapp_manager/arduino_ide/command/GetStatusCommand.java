package org.flowerplatform.rapp_manager.arduino_ide.command;

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
		//Don't actually do anything, but return a simple status.
		return new Status();
	}

	public static class Status {
		private String version = BaseNoGui.VERSION_NAME;

		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
	}
}
