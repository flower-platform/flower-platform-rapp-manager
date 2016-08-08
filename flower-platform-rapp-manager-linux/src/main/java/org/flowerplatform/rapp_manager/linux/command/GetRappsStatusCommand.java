package org.flowerplatform.rapp_manager.linux.command;

import java.io.File;
import java.io.FileFilter;

import org.flowerplatform.rapp_manager.linux.Constants;
import org.flowerplatform.rapp_manager.linux.ServerStatus;
import org.flowerplatform.rapp_manager.linux.Util;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;

/**
 * Returns the global status of the application (i.e. general indicator such as ok/not ok).
 * 
 * @author Claudiu Matei
 */
public class GetRappsStatusCommand implements IHttpCommand {
	public Object run() throws HttpCommandException {
		File rappsDir = new File(Constants.RAPPS_DIR);
		File[] rappDirs = rappsDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
	
		ServerStatus status = new ServerStatus();
		Util.fillSimpleStatus(status);
		
		status.setRapps(Util.getRappsStatus(rappDirs, true));
		return status;
	}
}
