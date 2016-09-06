package org.flowerplatform.rapp_manager.arduino_ide.launcher;

import java.io.File;
import java.net.URLClassLoader;

import org.flowerplatform.updatable_code.UpdatableCodeLoader;

import processing.app.Editor;
import processing.app.tools.Tool;

public class FlowerPlatformPluginLauncher implements Tool {
	
	static final String PPLUGIN_PATH = new File(FlowerPlatformPluginLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getAbsolutePath();
	static final String PREVIOUS_BIN_PATH = PPLUGIN_PATH + "/previous-version/";
	static final String CURRENT_BIN_PATH = PPLUGIN_PATH + "/current-version/";
	static final String NEW_BIN_PATH = PPLUGIN_PATH + "/new-version/";
	static final String CLASS_NAME = "org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin";
	
	UpdatableCodeLoader launcher;
	URLClassLoader loader;
	Tool flowerPlatformPlugin;
	
	public void init(Editor editor) {
		this.launcher = new UpdatableCodeLoader(PREVIOUS_BIN_PATH, CURRENT_BIN_PATH, NEW_BIN_PATH);
		loader = launcher.load();
		try {
			flowerPlatformPlugin = (Tool) Class.forName(CLASS_NAME, true, loader).newInstance();
			flowerPlatformPlugin.init(editor);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		flowerPlatformPlugin.run();
	}

	public String getMenuTitle() {
		return flowerPlatformPlugin.getMenuTitle();
	}
	
}
