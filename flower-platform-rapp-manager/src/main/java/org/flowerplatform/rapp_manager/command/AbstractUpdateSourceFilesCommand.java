package org.flowerplatform.rapp_manager.command;

import java.util.List;

import org.flowerplatform.rapp_manager.SourceFileDto;
import org.flowerplatform.tiny_http_server.IHttpCommand;

/**
 * Abstract command for updating source files.
 * @author Claudiu Matei
 */
public abstract class AbstractUpdateSourceFilesCommand implements IHttpCommand {

	protected String rappName;
	
	protected List<SourceFileDto> files; 

	public String getRappName() {
		return rappName;
	}

	public void setRappName(String rappName) {
		this.rappName = rappName;
	}

	public List<SourceFileDto> getFiles() {
		return files;
	}

	public void setFiles(List<SourceFileDto> files) {
		this.files = files;
	}
	
}
