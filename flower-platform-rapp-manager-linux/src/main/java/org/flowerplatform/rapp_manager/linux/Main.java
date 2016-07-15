package org.flowerplatform.rapp_manager.linux;

import java.io.File;
import java.util.Date;

import org.flowerplatform.rapp_manager.linux.command.CompileCommand;
import org.flowerplatform.rapp_manager.linux.command.DeleteCommand;
import org.flowerplatform.rapp_manager.linux.command.GetLogCommand;
import org.flowerplatform.rapp_manager.linux.command.GetStatusCommand;
import org.flowerplatform.rapp_manager.linux.command.RunCommand;
import org.flowerplatform.rapp_manager.linux.command.SetPropertiesCommand;
import org.flowerplatform.rapp_manager.linux.command.StopCommand;
import org.flowerplatform.rapp_manager.linux.command.UpdateSourceFilesAndCompileCommand;
import org.flowerplatform.rapp_manager.linux.command.UpdateSourceFilesCommand;
import org.flowerplatform.tiny_http_server.HttpServer;

public class Main {

	public static void main(String[] args) throws Exception {
		logf("Rappmanager starting at %s", new Date().toString());
		
		int serverPort = 65500;
		String homeDir = System.getProperty("user.home");
		
		//check and create folder structure
		File f;
		f = new File(homeDir + "/lib");
		f.mkdir();
		f = new File(homeDir + "/log");
		f.mkdir();
		f = new File(homeDir + "/rapps");
		f.mkdir();
		f = new File(homeDir + "/run");
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
	
}
