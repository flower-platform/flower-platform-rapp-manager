package org.flowerplatform.rapp_manager.linux;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class RappDescriptor {

	private String rappId;
	
	private Long uploadTimestamp;
	
	private Boolean startAtBoot;
	
	private Boolean running;

	public String getRappId() {
		return rappId;
	}

	public void setRappId(String rappId) {
		this.rappId = rappId;
	}

	public Long getUploadTimestamp() {
		return uploadTimestamp;
	}

	public void setUploadTimestamp(Long uploadTimestamp) {
		this.uploadTimestamp = uploadTimestamp;
	}

	public Boolean getStartAtBoot() {
		return startAtBoot;
	}

	public void setStartAtBoot(Boolean startAtBoot) {
		this.startAtBoot = startAtBoot;
	}

	public Boolean isRunning() {
		return running;
	}

	public void setRunning(Boolean running) {
		this.running = running;
	}
	
}
