package org.flowerplatform.rapp_manager.arduino_ide.library_manager;

import org.flowerplatform.rapp_manager.library_manager.Library;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import processing.app.packages.UserLibrary;


@JsonIgnoreProperties(value = { "userLibrary" })
public class ArduinoLibrary extends Library {
	private String name;
	private String url;
	private String version;
	private String[] headerFiles;
	private boolean matched;
	private UserLibrary userLibrary;
	
	public UserLibrary getUserLibrary() {
		return userLibrary;
	}
	public void setUserLibrary(UserLibrary userLibrary) {
		this.userLibrary = userLibrary;
	}
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
