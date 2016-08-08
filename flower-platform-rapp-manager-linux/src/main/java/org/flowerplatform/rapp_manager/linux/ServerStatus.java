package org.flowerplatform.rapp_manager.linux;

import java.util.List;

import org.flowerplatform.rapp_manager.SimpleStatus;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class ServerStatus extends SimpleStatus {
	private List<RappDescriptor> rapps;

	public List<RappDescriptor> getRapps() {
		return rapps;
	}
	public void setRapps(List<RappDescriptor> rapps) {
		this.rapps = rapps;
	}
}
