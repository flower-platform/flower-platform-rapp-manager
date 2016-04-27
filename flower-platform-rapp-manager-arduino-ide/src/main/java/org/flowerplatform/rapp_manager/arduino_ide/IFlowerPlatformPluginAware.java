package org.flowerplatform.rapp_manager.arduino_ide;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface IFlowerPlatformPluginAware {

	@JsonIgnore
	void setFlowerPlatformPlugin(FlowerPlatformPlugin plugin);
	
}
