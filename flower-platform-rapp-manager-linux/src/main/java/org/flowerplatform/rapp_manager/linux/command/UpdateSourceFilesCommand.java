package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.flowerplatform.rapp_manager.SourceFileDto;
import org.flowerplatform.rapp_manager.command.AbstractUpdateSourceFilesCommand;
import org.flowerplatform.rapp_manager.linux.Constants;
import org.flowerplatform.rapp_manager.linux.Util;
import org.flowerplatform.tiny_http_server.HttpCommandException;

/**
 * Loads the given files into Arduino IDE, and invokes compile on them.
 * @author Claudiu Matei
 * @author Andrei Taras
 */
public class UpdateSourceFilesCommand extends AbstractUpdateSourceFilesCommand {
	
	@Override
	public Object run() throws HttpCommandException {
		File rappDir = new File(String.format(Constants.RAPP_DIR_PATTERN, System.getProperty("user.home"), rappName));
		rappDir.mkdirs();
		
		// Make sure the working folder is clean (i.e. no unnecessary files)
		try {
			Util.deleteFilesFromFolder(rappDir);
		} catch (IOException e) {
			throw new HttpCommandException(e.getMessage(), e);
		}
		
		// save files to disk
		for (SourceFileDto srcFile : files) {
			try (FileOutputStream out = new FileOutputStream(rappDir.getAbsolutePath() + File.separator + srcFile.getName())) {
				out.write(srcFile.getContents().getBytes());
			} catch (IOException e) {
				log("Error while saving file: " + srcFile.getName());
				throw new HttpCommandException(e);
			}
		}
		
		return null;

	}
	
}
