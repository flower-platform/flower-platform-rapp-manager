package org.flowerplatform.rapp_manager.library_manager;

/**
 * @author Cristian Spiescu
 */
public class MatchedLibrary {
	/**
	 * @author Silviu Negoita
	 */
	public enum Status { 
		OK("OK"), NEEDS_DOWNLOAD("Needs download"), NEEDS_UPDATE("Needs update"), UNKNOWN("Unknown"), NEEDS_DELETE("Needs delete");
		
		private String label;

		Status(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}
	};
	/**
	 * @author Silviu Negoita
	 */
	public enum Action {
		DOWNLOAD("Download"), DELETE("Delete"), NONE("Nothing to do");
		
		private String label;
	
		Action(String label) {
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

	public Library getExistingLibrary() {
		return existingLibrary;
	}

	public void setExistingLibrary(Library existingLibrary) {
		this.existingLibrary = existingLibrary;
	}

	public Library getRequiredLibrary() {
		return requiredLibrary;
	}

	public void setRequiredLibrary(Library requiredLibrary) {
		this.requiredLibrary = requiredLibrary;
	}

}
