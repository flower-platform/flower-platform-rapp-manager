package org.flowerplatform.rapp_manager.library_manager;

public class Library {
	private String name;
	private String url;
	private String version;
	private String[] headerFiles;
	private boolean matched;
	
	public boolean isMatched() {
		return matched;
	}
	public void setMatched(boolean matched) {
		this.matched = matched;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String[] getHeaderFiles() {
		return headerFiles;
	}
	public void setHeaderFiles(String[] headerFiles) {
		this.headerFiles = headerFiles;
	}
}
