package org.flowerplatform.rapp_manager.arduino_ide.model;

import java.util.List;

public class BoardProperty {
	private String name;
	private List<BoardOption> options;
	
	public BoardProperty() {
	}
	
	public BoardProperty(String name, List<BoardOption> options) {
		this.name = name;
		this.options = options;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<BoardOption> getOptions() {
		return options;
	}
	public void setOptions(List<BoardOption> options) {
		this.options = options;
	}

	@Override
	public String toString() {
		return "BoardProperty [name=" + name + ", options=" + options + "]";
	}
}
