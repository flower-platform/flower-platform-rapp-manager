package org.flowerplatform.rapp_manager.arduino_ide.command;

import static org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin.log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.flowerplatform.rapp_manager.SourceFileDto;
import org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin;
import org.flowerplatform.rapp_manager.arduino_ide.IFlowerPlatformPluginAware;
import org.flowerplatform.rapp_manager.command.AbstractUpdateSourceFilesCommand;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.ReflectionException;

import processing.app.BaseNoGui;
import processing.app.Editor;

/**
 * Loads the given files into Arduino IDE, and invokes compile on them.
 * @author Claudiu Matei
 * @author Andrei Taras
 */
public class UpdateSourceFilesCommand extends AbstractUpdateSourceFilesCommand implements IFlowerPlatformPluginAware {

	private static final String STANDARD_EXTENSION = "ino";
	
	private FlowerPlatformPlugin plugin;
	
	@Override
	public void setFlowerPlatformPlugin(FlowerPlatformPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Deletes all the files contained in the folder given as parameter.
	 * 
	 * @throws HttpCommandException if any problem occurrs.
	 */
	private static void deleteFilesFromFolder(File dir) throws HttpCommandException {
		for (File f : dir.listFiles()) {
			try {
				if (!(f.delete())) {
					throw new HttpCommandException("Can't delete file " + f.getAbsolutePath());
				}
			} catch (HttpCommandException hce) { 
				throw hce;
			} catch (Throwable th) {
				throw new HttpCommandException( String.format("Error while deleting file %s . Message is \"%s\".", f.getAbsolutePath(), th.getMessage()) );
			}
		}
	}

	/**
	 * Persists the given files on disk, and opens the *.ino file in the editor.
	 */
	private void saveAndOpenInEditor(File dir) throws HttpCommandException, ReflectionException {
		if (files == null || files.size() == 0) {
			throw new HttpCommandException("Cannot perform operation. No source files provided.");
		}

		boolean found = false;
		// Scan the list of files; if any ends with *.ino => rename it to FLOWER_PLATFORM_WORK_FOLDER_NAME
		// because this is required by Arduino IDE.
		for (SourceFileDto srcFile : files) {
			if (srcFile.getName().endsWith("." + STANDARD_EXTENSION)) {
				// The *.ino file needs to have the same name as the folder it is found in.
				srcFile.setName(FlowerPlatformPlugin.FLOWER_PLATFORM_WORK_FOLDER_NAME + "." + STANDARD_EXTENSION);
				found = true;
				break;
			}
		}

		if (!found) {
			// If no *.ino files were found, then just grab the first one, rename it, and use that as the main source.
			files.get(0).setName(FlowerPlatformPlugin.FLOWER_PLATFORM_WORK_FOLDER_NAME + "." + STANDARD_EXTENSION);
		}
		
		// update source files
		for (SourceFileDto srcFile : files) {
			File f = new File(dir.getAbsolutePath() + File.separator + srcFile.getName());
			try {
				BaseNoGui.saveFile(srcFile.getContents(), f);
				log("File updated: " + f);
			} catch (IOException e1) {
				log("Error while saving file = " + f, e1);
			}
		}
		
		// reload project
		Editor editor = plugin.getEditor();
	    editor.internalCloseRunner();
		try {
			Method handleOpenInternal = Editor.class.getDeclaredMethod("handleOpenInternal", File.class);
			handleOpenInternal.setAccessible(true);
		    handleOpenInternal.invoke(editor, new File(dir.getAbsolutePath() + File.separator + FlowerPlatformPlugin.FLOWER_PLATFORM_WORK_FOLDER_NAME + ".ino"));
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
			throw new ReflectionException("Reflection error.", e1);
		}
		log("Sketch reloaded from Flowerino repository");
	}
	
	@Override
	public Object run() throws HttpCommandException {
		File dir = FlowerPlatformPlugin.getFlowerPlatformWorkFolder();
		
		// Make sure the working folder is clean (i.e. no unnecessary files)
		deleteFilesFromFolder(dir);

		// Open the INO file in the editor, as if the user selected File->Open
		saveAndOpenInEditor(dir);
		
		return null;
	}
	
}
