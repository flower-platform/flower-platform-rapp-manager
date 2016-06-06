package org.flowerplatform.rapp_manager.linux;

import java.util.List;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class ServerStatus {

	private String version;
	
	private List<RappDescriptor> rapps;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<RappDescriptor> getRapps() {
		return rapps;
	}

	public void setRapps(List<RappDescriptor> rapps) {
		this.rapps = rapps;
	}
	
}
