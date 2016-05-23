package org.flowerplatform.rapp_manager.arduino_ide.library_manager;

import java.util.List;

import processing.app.packages.UserLibrary;

public class Library {
	private String name;
	private String url;
	private String version;
	private List<String> headerFiles;
	private transient UserLibrary userLibrary;
	
	
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
	public List<String> getHeaderFiles() {
		return headerFiles;
	}
	public void setHeaderFiles(List<String> headerFiles) {
		this.headerFiles = headerFiles;
	}
	public UserLibrary getUserLibrary() {
		return userLibrary;
	}
	public void setUserLibrary(UserLibrary userLibrary) {
		this.userLibrary = userLibrary;
	}
}
