package org.flowerplatform.rapp_manager.linux;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.flowerplatform.rapp_manager.linux.command.CompileCommand;
import org.flowerplatform.rapp_manager.linux.command.DeleteCommand;
import org.flowerplatform.rapp_manager.linux.command.GetLogCommand;
import org.flowerplatform.rapp_manager.linux.command.GetRappsStatusCommand;
import org.flowerplatform.rapp_manager.linux.command.GetStatusCommand;
import org.flowerplatform.rapp_manager.linux.command.RunCommand;
import org.flowerplatform.rapp_manager.linux.command.SetPropertiesCommand;
import org.flowerplatform.rapp_manager.linux.command.StopCommand;
import org.flowerplatform.rapp_manager.linux.command.UpdateSourceFilesAndCompileCommand;
import org.flowerplatform.rapp_manager.linux.command.UpdateSourceFilesCommand;
import org.flowerplatform.tiny_http_server.HttpServer;

public class Main {

	public static String VERSION = null;
	
	/**
	 * Internal properties file, packed within current jar.
	 */
	protected static Properties internalProperties;
	
	public static void main(String[] args) throws Exception {
		logf("Rappmanager starting at %s", new Date().toString());
		
		prepareProperties();
		
		int serverPort = 65500;
		
		//check and create folder structure
		File f;
		f = new File(Constants.LIB_DIR);
		f.mkdir();
		f = new File(Constants.LOG_DIR);
		f.mkdir();
		f = new File(Constants.RAPPS_DIR);
		f.mkdir();
		f = new File(Constants.PID_DIR);
		f.mkdir();

		HttpServer server = new HttpServer(serverPort, false);
		server.registerCommand("updateSourceFiles", UpdateSourceFilesCommand.class);
		server.registerCommand("compile", CompileCommand.class);
		server.registerCommand("updateSourceFilesAndCompile", UpdateSourceFilesAndCompileCommand.class);
		server.registerCommand("run", RunCommand.class);
		server.registerCommand("stop", StopCommand.class);
		server.registerCommand("delete", DeleteCommand.class);
		server.registerCommand("getLog", GetLogCommand.class);
		server.registerCommand("getStatus", GetStatusCommand.class);
		server.registerCommand("getRappsStatus", GetRappsStatusCommand.class);
		server.registerCommand("setProperties", SetPropertiesCommand.class);
		
		// start apps with "startAtBoot" flag set to true
		log("Autostarting rapps...");
		try {
			for (String rappName : Util.getInstalledRapps()) {
				try {
					if (!Util.getStartAtBootFlag(rappName) || Util.isRappRunning(rappName)) {
						continue;
					}
					RunCommand cmd = new RunCommand();
					cmd.setRappName(rappName);
					cmd.run();
				} catch (Throwable e) {
					log("Could not start rapp: " + rappName, e);
				}
			}
			log("Autostarting rapps finnished.");
			
		} catch (Throwable t) {
			log("Error autostarting rapps", t);
		}
		
	}
	
	public static void logp(String message) {
		System.out.print(message);
	}

	public static void log(String message) {
		System.out.println(message);
	}

	public static void logf(String message, Object... args) {
		System.out.println(String.format(message, args));
	}
	
	public static void log(String message, Throwable t) {
		System.out.println(message);
		t.printStackTrace(System.out);
	}
	
	private static void prepareProperties() {
		try {
			internalProperties = readInternalProperties();
			logf("Loaded properties %s", ""+internalProperties);
			VERSION = internalProperties.getProperty("app.version");
		} catch (Throwable th) {
			th.printStackTrace(System.err);
		}
	}
	
	private static Properties readInternalProperties() {
		Properties properties = new Properties();
		InputStream is = null; 
		try {
			is = Main.class.getClassLoader().getResourceAsStream("org/flowerplatform/all.properties");
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
}
