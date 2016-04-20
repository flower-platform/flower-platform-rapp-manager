package org.flowerplatform.rapp_manager;

import java.io.File;

import org.flowerplatform.tiny_http_server.HttpServer;

public class Main {

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
		server.registerCommand("startApp", StartAppCommand.class);
		server.registerCommand("stopApp", StopAppCommand.class);
	}
	
}
