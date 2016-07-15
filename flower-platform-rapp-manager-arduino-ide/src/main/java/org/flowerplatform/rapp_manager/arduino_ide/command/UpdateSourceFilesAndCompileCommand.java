package org.flowerplatform.rapp_manager.arduino_ide.command;

import java.util.List;

import org.flowerplatform.rapp_manager.SourceFileDto;
import org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin;
import org.flowerplatform.rapp_manager.arduino_ide.IFlowerPlatformPluginAware;
import org.flowerplatform.rapp_manager.command.AbstractRappCommand;
import org.flowerplatform.tiny_http_server.HttpCommandException;

/**
 * Aggregate between {@link UpdateSourceFilesCommand} and {@link CompileCommand}
 * This class is just a convenient way to allow the user to perform both uploading source
 * files and performing compilation at the same time.
 * 
 * @author Andrei Taras
 */
public class UpdateSourceFilesAndCompileCommand extends AbstractRappCommand implements IFlowerPlatformPluginAware {

	private FlowerPlatformPlugin plugin;
	
	protected List<SourceFileDto> files;

	@Override
	public Object run() throws HttpCommandException {
		UpdateSourceFilesCommand updateSourceFilesCommand = new UpdateSourceFilesCommand();
		updateSourceFilesCommand.setFlowerPlatformPlugin(plugin);
		updateSourceFilesCommand.setRappId(rappId);
		updateSourceFilesCommand.setFiles(files);
		updateSourceFilesCommand.run();

		CompileCommand compileCommand = new CompileCommand();
		compileCommand.setFlowerPlatformPlugin(plugin);
		compileCommand.setRappId(rappId);
		return compileCommand.run();
	}

	public List<SourceFileDto> getFiles() {
		return files;
	}
	public void setFiles(List<SourceFileDto> files) {
		this.files = files;
	}

	@Override
	public void setFlowerPlatformPlugin(FlowerPlatformPlugin plugin) {
		this.plugin = plugin;
	}
}
