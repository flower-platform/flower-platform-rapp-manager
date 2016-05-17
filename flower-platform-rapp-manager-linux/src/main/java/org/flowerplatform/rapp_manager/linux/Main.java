package org.flowerplatform.rapp_manager.linux;

import java.io.File;

import org.flowerplatform.rapp_manager.linux.command.CompileCommand;
import org.flowerplatform.rapp_manager.linux.command.DeleteCommand;
import org.flowerplatform.rapp_manager.linux.command.GetLogCommand;
import org.flowerplatform.rapp_manager.linux.command.GetStatusCommand;
import org.flowerplatform.rapp_manager.linux.command.RunCommand;
import org.flowerplatform.rapp_manager.linux.command.StopCommand;
import org.flowerplatform.rapp_manager.linux.command.UpdateSourceFilesCommand;
import org.flowerplatform.tiny_http_server.HttpServer;

public class Main {

	public static final String RAPPS_DIR = "rapps";

	public static void main(String[] args) throws Exception {
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
		server.registerCommand("run", RunCommand.class);
		server.registerCommand("stop", StopCommand.class);
		server.registerCommand("delete", DeleteCommand.class);
		server.registerCommand("getLog", GetLogCommand.class);
		server.registerCommand("getStatus", GetStatusCommand.class);
	}

	public static void logp(String message) {
		System.out.print(message);
	}

	public static void log(String message) {
		System.out.println(message);
	}
	
	public static void log(String message, Throwable t) {
		System.out.println(message);
		t.printStackTrace(System.out);
	}
	
}
