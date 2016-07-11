package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Constants.RAPPS_DIR_PATTERN;
import static org.flowerplatform.rapp_manager.linux.Constants.SERVER_VERSION;
import static org.flowerplatform.rapp_manager.linux.Main.log;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.flowerplatform.rapp_manager.linux.RappDescriptor;
import org.flowerplatform.rapp_manager.linux.ServerStatus;
import org.flowerplatform.rapp_manager.linux.Util;
import org.flowerplatform.tiny_http_server.IHttpCommand;


/**
 * 
 * @author Claudiu Matei
 *
 */
public class GetStatusCommand implements IHttpCommand {

	public Object run() {
		File rappsDir = new File(String.format(RAPPS_DIR_PATTERN, System.getProperty("user.home")));
		File[] rappDirs = rappsDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
	
		ServerStatus status = new ServerStatus();
		status.setVersion(SERVER_VERSION);
		
		List<RappDescriptor> rapps = new ArrayList<>();
		for (File rappDir : rappDirs) {
			RappDescriptor rapp = new RappDescriptor();
			rapp.setName(rappDir.getName());
			try {
				rapp.setRunning(Util.isRappRunning(rapp.getName()));
				rapp.setStartAtBoot(Util.getStartAtBootFlag(rapp.getName()));
				rapp.setUploadTimestamp(Util.getRappTimestamp(rapp.getName()));
			} catch (IOException | InterruptedException e) {
				log(e.getMessage(), e);
				throw new RuntimeException(e.getMessage(), e);
			}
			rapps.add(rapp);	
		}
		status.setRapps(rapps);
		return status;
	}

	
}
