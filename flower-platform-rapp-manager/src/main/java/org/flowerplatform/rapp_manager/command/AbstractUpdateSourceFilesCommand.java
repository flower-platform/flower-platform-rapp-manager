package org.flowerplatform.rapp_manager.command;

import java.util.List;

import org.flowerplatform.rapp_manager.SourceFileDto;

/**
 * Abstract command for updating source files.
 * @author Claudiu Matei
 */
public abstract class AbstractUpdateSourceFilesCommand extends AbstractRappCommand {
	protected List<SourceFileDto> files; 

	public List<SourceFileDto> getFiles() {
		return files;
	}

	public void setFiles(List<SourceFileDto> files) {
		this.files = files;
	}
	
}
