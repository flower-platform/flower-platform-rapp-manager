package org.flowerplatform.rapp_manager.arduino_ide.model;

/**
 * Represents an option in the menu; represends a custom board setting (some boards have extra 
 * settings).
 * 
 * @author Andrei Taras
 */
public class BoardOption {
	private String name;
	private boolean selected;
	
	public BoardOption() {
	}
	
	public BoardOption(String name, boolean selected) {
		this.name = name;
		this.selected = selected;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return "BoardOption [name=" + name + ", selected=" + selected + "]";
	}
}
