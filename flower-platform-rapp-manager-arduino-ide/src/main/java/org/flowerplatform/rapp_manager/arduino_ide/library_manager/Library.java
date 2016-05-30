package org.flowerplatform.rapp_manager.arduino_ide.library_manager;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import processing.app.packages.UserLibrary;

@JsonIgnoreProperties(value = { "userLibrary" })
public class Library {
	private String name;
	private String url;
	private String version;
	private String[] headerFiles;
	boolean matched;
	private String checksum;
	
	
	public boolean isMatched() {
		return matched;
	}
	public void setMatched(boolean matched) {
		this.matched = matched;
	}
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
	public String[] getHeaderFiles() {
		return headerFiles;
	}
	public void setHeaderFiles(String[] headerFiles) {
		this.headerFiles = headerFiles;
	}
	public UserLibrary getUserLibrary() {
		return userLibrary;
	}
	public void setUserLibrary(UserLibrary userLibrary) {
		this.userLibrary = userLibrary;
	}
}
