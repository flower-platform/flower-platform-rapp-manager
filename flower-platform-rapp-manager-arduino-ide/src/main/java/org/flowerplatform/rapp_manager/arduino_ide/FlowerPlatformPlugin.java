package org.flowerplatform.rapp_manager.arduino_ide;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.flowerplatform.updateable_launcher.Downloader;

import cc.arduino.contributions.VersionHelper;
import processing.app.BaseNoGui;
import processing.app.Editor;
import processing.app.Sketch;
import processing.app.SketchData;
import processing.app.tools.Tool;

/**
 * @author Cristian Spiescu
 */
public class FlowerPlatformPlugin implements Tool {

	public static final String FLOWER_PLATFORM = "Flower platform";

	public static final String BOARD_MENU_TEXT_KEY = "Board";
	public static final String PROGRAMMER_MENU_TEXT_KEY = "Programmer";
	
	/**
	 * The name of the folder in which we store temp data related to work happening within flower platform.
	 * Please note this name is not absolute, but relative (i.e. just the folder name, not the full path)
	 */
	public static final String FLOWER_PLATFORM_WORK_FOLDER_NAME = "flower-platform-work";

	protected Editor editor;
	protected Properties globalProperties;
	
	/**
	 * Internal properties file, packed within current jar.
	 */
	protected Properties internalProperties;
	
	/**
	 * Enables a bunch of utility functions which should get invoked only when
	 * debugging the application.
	 */
	public static final boolean DEBUG_MODE = true;
	
	public Properties getGlobalProperties() {
		return globalProperties;
	}

	public Editor getEditor() {
		return editor;
	}

	@Override
	public void init(final Editor editor) {
		initProperties();
		
		int serverPort = Integer.parseInt(globalProperties.getProperty("commandServerPort"));
		final HttpServerInitializer serverInitializer = new HttpServerInitializer(serverPort, this);

		this.editor = editor;
		editor.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
				// initialize the menu
				JMenu menu = new JMenu("Flower Platform");
				JMenuItem menuCheckForUpdate = new JMenuItem("Check for new version...");
				menuCheckForUpdate.addActionListener(event -> checkForUpdate());
				menu.add(menuCheckForUpdate);
				
				editor.getJMenuBar().add(menu, editor.getJMenuBar().getComponentCount() - 1);
				editor.getJMenuBar().revalidate();
				
				checkForUpdate();
			}

			@Override
			public void componentResized(ComponentEvent e) {}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
		
		// We need to notify our initializer to stop polling whenever this window is closed.
		editor.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				serverInitializer.destroy();
			}
		});
	}

	@Override
	public void run() {
	}

	private void checkForUpdate() {
		new Thread(() -> { 
			String latestVersion = null; 
			try (Scanner scanner = new Scanner(new URL(globalProperties.getProperty("update.versionUrl", null)).openStream())) {
				latestVersion = scanner.nextLine();
				if (VersionHelper.valueOf(latestVersion).greaterThan(VersionHelper.valueOf(getVersion()))) {
					if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, "A newer version of Flower Platform Plugin is available. It's recommended to upgrade.\n"
							+ "Installed version = " + getVersion()
							+ ". Latest version = " + latestVersion
							+ ".\n\n"
							+ "Do you want to upgrade to the latest version?", "Information", JOptionPane.YES_NO_OPTION)) {
						downloadUpdate();
					}
				}
			} catch (IOException e) {
				log("Error getting latest Flower Platform Plugin version", e);
			} 
		}).start();
	}
	
	private void downloadUpdate() {
		File updateLocation = new File(BaseNoGui.getSketchbookFolder() + "/tools/FlowerPlatformPluginLauncher/tool/new-version");
		updateLocation.mkdirs();
		log("Downloading update...");
		try {
			Downloader.downloadAndUnzip(globalProperties.getProperty("update.downloadUrl", null), updateLocation);
			log("Download complete.");
			JOptionPane.showMessageDialog(null, "The latest version of Flower Platform Plugin was downloaded. Please restart Arduino IDE for the changes to take effect.");
		} catch (Exception e) {
			updateLocation.delete();
			e.printStackTrace();
		}
	}
	
	@Override
	public String getMenuTitle() {
		return FLOWER_PLATFORM;
	}
	
	private void initProperties() {
		try {
			internalProperties = readInternalProperties();
		} catch (Throwable th) {
			th.printStackTrace(System.err);
		}
		
		// get/create global properties
		globalProperties = readProperties(getGlobalPropertiesFile());
		boolean writeProperties = false;
		if (globalProperties.getProperty("commandServerPort") == null) {
			globalProperties.put("commandServerPort", "9000");
			writeProperties = true;
		}
		if (globalProperties.getProperty("update.versionUrl") == null) {
			globalProperties.put("update.versionUrl", "http://hub.flower-platform.com/update/version.txt");
			writeProperties = true;
		}
		if (globalProperties.getProperty("update.downloadUrl") == null) {
			globalProperties.put("update.downloadUrl", "http://hub.flower-platform.com/update/flower-platform-arduino-ide.zip");
			writeProperties = true;
		}
		if (globalProperties.getProperty("otaUpload.serverSignature") == null) {
			try {
				globalProperties.put("otaUpload.serverSignature", new String(Base64.getEncoder().encode(SecureRandom.getInstanceStrong().generateSeed(32))));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			writeProperties = true;
		}
		if (globalProperties.getProperty("otaUpload.method") == null) {
			globalProperties.put("otaUpload.method", "0");
			writeProperties = true;
		}
		if (writeProperties) {
			writeProperties(globalProperties, getGlobalPropertiesFile());
		}
	}
	
	public static void log(String message) {
		System.out.println(message);
		debugLogToFile(message);
	}
	
	public static void log(String message, Throwable t) {
		System.out.println(message);
		t.printStackTrace(System.out);
		debugExceptionToFile(t);
	}
	
	public static void debug(String message) {
		if (!DEBUG_MODE) {
			return;
		}
		log(message);
	}
	
	public File getProjectPropertiesFile() {
		return new File(editor.getSketch().getFolder(), ".flowerino-link");
	}
	
	public File getGlobalPropertiesFile() {
		return new File(BaseNoGui.getSketchbookFolder(), ".flower-platform");
	}

	public Properties readProperties(File file) {
		Properties properties = new Properties();
		if (file.exists()) {
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				properties.load(is);
			} catch (IOException e1) {
				log("Error while opening " + ".flowerino-link" + " file from " + file.getAbsolutePath(), e1);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e1) {
						log("Error while opening " + ".flowerino-link" + " file from " + file.getAbsolutePath(), e1);
					}
				}
			}
		}
		return properties;
	}
	
	private Properties readInternalProperties() {
		Properties properties = new Properties();
		InputStream is = null; 
		try {
			is = this.getClass().getClassLoader().getResourceAsStream("org/flowerplatform/all.properties");
			properties.load(is);
		} catch (IOException ex) {
			log("Error opening internal properties file.");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e1) {
					log("Error while opening internal properties file");
				}
			}
		}
		
		return properties;
	}
	
	public void writeProperties(Properties properties, File file) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			properties.store(os, null);
		} catch (IOException e1) {
			log("Error while saving " + ".flowerino-link" + " file in " + file.getAbsolutePath(), e1);
		} finally {
			try {
				os.close();
			} catch (IOException e1) {
				log("Error while saving " + ".flowerino-link" + " file in " + file.getAbsolutePath(), e1);
			}
		}
		log("Config info successfully saved in " + file.getAbsolutePath());
	}
	
	public void writeGlobalProperties() {
		writeProperties(globalProperties, getGlobalPropertiesFile());
	}

	public static File getBuildFolder(Sketch sketch) throws IOException {
		SketchData sketchData = null;
		try {
			Field f;
			f = sketch.getClass().getDeclaredField("data");
			f.setAccessible(true);
			sketchData = (SketchData) f.get(sketch); 
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		} 
		return BaseNoGui.getBuildFolder(sketchData);
	}

	public static File getFlowerPlatformWorkFolder() {
		File f = new File(System.getProperty("user.home") + "/" + FLOWER_PLATFORM_WORK_FOLDER_NAME);
		f.mkdirs();
		return f;
	}
	
	/**
	 * Returns the version of the plugin.
	 * @return
	 */
	public String getVersion() {
		return internalProperties.getProperty("app.version");
	}
	
	/**
	 * Utility function to write a message to a log file; used only in debug mode.
	 * 
	 * Sometimes, plugin initialization happens very early, so the editor is not yet available => sometimes 
	 * we do not see the error messages appearing in the editor's console because the console might not be 
	 * ready yet, at this stage.
	 */
	public static void debugLogToFile(String message) {
		String debugFileName = "D:/arduino_ide_debug_log.txt";
		
		if (message == null || !DEBUG_MODE) {
			return;
		}
		File file = new File(debugFileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
		    Files.write(Paths.get(debugFileName), (message + "\n").getBytes(), StandardOpenOption.APPEND);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Utility function to write an exception's stacktrace into file; used only in debug mode.
	 */
	public static void debugExceptionToFile(Throwable th) {
		if (th == null || !DEBUG_MODE) {
			return;
		}
		StringWriter sw = new StringWriter();
		th.printStackTrace(new PrintWriter(sw));
		
		debugLogToFile(sw.toString());
	}
}
