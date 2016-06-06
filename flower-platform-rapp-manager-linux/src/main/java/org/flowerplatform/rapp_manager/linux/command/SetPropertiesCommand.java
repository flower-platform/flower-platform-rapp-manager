package org.flowerplatform.rapp_manager.linux.command;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.flowerplatform.rapp_manager.linux.Util;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;


/**
 * 
 * @author Claudiu Matei
 *
 */
public class SetPropertiesCommand implements IHttpCommand {

	private String rappName;
	
	private Map<String, Object> properties;
	
	public Object run() throws HttpCommandException {
		try {
			Properties rappProperties = Util.getProperties(rappName);
			for (Entry<String, Object> property : properties.entrySet()) {
				rappProperties.setProperty(property.getKey(), property.getValue().toString());
			}
			Util.saveProperties(rappName, rappProperties);
		} catch (Exception e) {
			throw new HttpCommandException(e.getMessage(), e);
		}
		return null;
	}

	public String getRappName() {
		return rappName;
	}

	public void setRappName(String rappName) {
		this.rappName = rappName;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
}
