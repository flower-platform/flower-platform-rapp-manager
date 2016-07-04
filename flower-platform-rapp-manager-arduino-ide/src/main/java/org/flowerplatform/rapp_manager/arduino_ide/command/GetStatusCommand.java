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
		Status status = new Status();
		status.setVersion("1.0");
		status.setHostSystemName("Arduino IDE");
		status.setHostSystemVersion(BaseNoGui.VERSION_NAME);
		
		return status;
	}

	public static class Status {
		public Status() {
		}
		public Status(String version, String hostSystemName, String hostSystemVersion) {
			this.version = version;
			this.hostSystemName = hostSystemName;
			this.hostSystemVersion = hostSystemVersion;
		}

		private String version = BaseNoGui.VERSION_NAME;
		private String hostSystemName;
		private String hostSystemVersion;
		
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public String getHostSystemName() {
			return hostSystemName;
		}
		public void setHostSystemName(String hostSystemName) {
			this.hostSystemName = hostSystemName;
		}
		public String getHostSystemVersion() {
			return hostSystemVersion;
		}
		public void setHostSystemVersion(String hostSystemVersion) {
			this.hostSystemVersion = hostSystemVersion;
		}
	}
}
