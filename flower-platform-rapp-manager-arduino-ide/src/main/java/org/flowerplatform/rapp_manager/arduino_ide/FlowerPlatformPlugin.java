package org.flowerplatform.rapp_manager.arduino_ide;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

import javax.swing.JMenu;

import org.flowerplatform.rapp_manager.arduino_ide.command.CompileCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.GetBoardsCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.GetLogCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.GetSelectedBoard;
import org.flowerplatform.rapp_manager.arduino_ide.command.GetStatusCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.SetOptionsCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.SetSelectedBoardCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.UpdateSourceFilesAndCompileCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.UpdateSourceFilesCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.UploadToBoardCommand;
import org.flowerplatform.tiny_http_server.CommandFactory;
import org.flowerplatform.tiny_http_server.HttpServer;
import org.flowerplatform.tiny_http_server.IHttpCommand;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	public Properties getGlobalProperties() {
		return globalProperties;
	}

	public Editor getEditor() {
		return editor;
	}

	@Override
	public void init(final Editor editor) {
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
		
		try {
			int serverPort = Integer.parseInt(globalProperties.getProperty("commandServerPort"));
			HttpServer server = new HttpServer(serverPort);
			// set command factory, in order to inject plugin reference into the IFlowerPlatformPluginAware command instances
			server.setCommandFactory(new CommandFactory() { 
				@Override
				public Object createCommandInstance(Class<? extends IHttpCommand> commandClass, Object data) {
					ObjectMapper mapper = new ObjectMapper();
					IHttpCommand command;
					try {
						command = mapper.readValue((String) data, commandClass);
						if (command instanceof IFlowerPlatformPluginAware) {
							((IFlowerPlatformPluginAware) command).setFlowerPlatformPlugin(FlowerPlatformPlugin.this);
						}
						return command;
					} catch (IOException e) {
						e.printStackTrace(System.err);
						throw new RuntimeException("Cannot create command object", e);
					}
				}
			});
			server.registerCommand("uploadToBoard", UploadToBoardCommand.class);
			server.registerCommand("updateSourceFiles", UpdateSourceFilesCommand.class);
			server.registerCommand("compile", CompileCommand.class);
			server.registerCommand("updateSourceFilesAndCompile", UpdateSourceFilesAndCompileCommand.class);
			server.registerCommand("getBoards", GetBoardsCommand.class);
			server.registerCommand("getSelectedBoard", GetSelectedBoard.class);
			//server.registerCommand("getBoardsWithDetails", GetBoardsWithDetails.class);
			server.registerCommand("setSelectedBoard", SetSelectedBoardCommand.class);
			server.registerCommand("setOptions", SetOptionsCommand.class);
			server.registerCommand("getStatus", GetStatusCommand.class);
			server.registerCommand("getLog", GetLogCommand.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.editor = editor;
		editor.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
				// initialize the menu
				JMenu menu = new JMenu("Flower Platform");
				
				editor.getJMenuBar().add(menu, editor.getJMenuBar().getComponentCount() - 1);
				editor.getJMenuBar().revalidate();
			}

			@Override
			public void componentResized(ComponentEvent e) {}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
	}

	@Override
	public void run() {
	}

	@Override
	public String getMenuTitle() {
		return FLOWER_PLATFORM;
	}
	
	public static void log(String message) {
		System.out.println(message);
	}
	
	public static void log(String message, Throwable t) {
		System.out.println(message);
		t.printStackTrace(System.out);
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
}
