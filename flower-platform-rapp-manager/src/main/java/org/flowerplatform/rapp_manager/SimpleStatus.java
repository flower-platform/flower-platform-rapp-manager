package org.flowerplatform.rapp_manager;

/**
 * DTO for basic status info.
 * 
 * @author Andrei Taras
 */
public class SimpleStatus {
	public SimpleStatus() {
	}
	public SimpleStatus(String version, String hostSystemName, String hostSystemVersion) {
		this.version = version;
		this.hostSystemName = hostSystemName;
		this.hostSystemVersion = hostSystemVersion;
	}

	private String version;
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
