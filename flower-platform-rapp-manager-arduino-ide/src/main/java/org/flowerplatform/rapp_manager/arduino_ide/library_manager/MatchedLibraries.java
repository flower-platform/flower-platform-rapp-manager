package org.flowerplatform.rapp_manager.arduino_ide.library_manager;

import java.util.Map;

import processing.app.packages.UserLibrary;

/**
 * @author Cristian Spiescu
 */
public class MatchedLibraries {
	
	public enum Status { 
		OK("OK"), NEEDS_DOWNLOAD("Needs download"), NEEDS_UPDATE("Needs update"), UNKNOWN("Unknown");
		
		private String label;

		private Status(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}
		
	};
	
	public enum Action {
		DOWNLOAD("Download"), DELETE("Delete"), NONE("Nothing to do");
		
		private String label;
	
		private Action(String label) {
			this.label = label;
		}
	
		@Override
		public String toString() {
			return label;
		}
		
	};	
	
	private Library existingLibrary;
	
	private Library requiredLibrary;
	
	private Status status = Status.UNKNOWN;
	
	private Action action = Action.NONE;
	
	private String name;

	

	

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MatchedLibraries() {
		super();
	}
}
