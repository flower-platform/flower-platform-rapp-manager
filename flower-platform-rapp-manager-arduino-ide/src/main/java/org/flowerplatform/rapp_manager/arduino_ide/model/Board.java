package org.flowerplatform.rapp_manager.arduino_ide.model;

import java.util.List;

/**
 * Holds information about a given board, its properties, etc.
 * This is intended to be a wrapper over the boards listed in the "Tools" menu
 * along with the properties exposed when selecting a particular board.
 * 
 * @author Andrei Taras
 */
public class Board {
	/**
	 * Name of the board, as displayed in the Tools/Boards menu.
	 */
	private String name;

	/**
	 * True, if this is the currently selected board, false otherwise.
	 */
	private boolean selected;
	
	private List<BoardProperty> properties;
	
	public Board() {
	}
	public Board(String name, boolean selected, List<BoardProperty> properties) {
		this.name = name;
		this.selected = selected;
		this.properties = properties;
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
	public List<BoardProperty> getProperties() {
		return properties;
	}
	public void setProperties(List<BoardProperty> properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return "Board [name=" + name + ", selected=" + selected + ", properties=" + properties + "]";
	}
}
