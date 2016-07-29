package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.flowerplatform.rapp_manager.SourceFileDto;
import org.flowerplatform.rapp_manager.command.AbstractUpdateSourceFilesCommand;
import org.flowerplatform.rapp_manager.linux.FileUtils;
import org.flowerplatform.tiny_http_server.HttpCommandException;

/**
 * Loads the given files into Arduino IDE, and invokes compile on them.
 * 
 * @author Claudiu Matei
 * @author Andrei Taras
 */
public class UpdateSourceFilesCommand extends AbstractUpdateSourceFilesCommand {
	
	@Override
	public Object run() throws HttpCommandException {
		File rappDir = FileUtils.getRappDir(rappId);
		// Ensure that all intermediary folders are in place.
		rappDir.mkdirs();
		
		try {
			// Make sure the working folder is clean (i.e. no unnecessary files)
			FileUtils.deleteFilesFromDir(rappDir);
		} catch (IOException e) {
			throw new HttpCommandException(e.getMessage(), e);
		}
		
		logger.log("Writing files to folder + " + rappDir);
		// save files to disk
		for (SourceFileDto srcFile : files) {
			logger.log("Writing sourceFileDto " + srcFile + " to disk.");
			// Note that we need to make sure that file names match the linux filesystem restrictions (i.e. no "/" characters)
			try (FileOutputStream out = new FileOutputStream(rappDir.getAbsolutePath() + File.separator + FileUtils.rappIdToFilesystemName(srcFile.getName()))) {
				out.write(srcFile.getContents().getBytes());
			} catch (IOException e) {
				logger.log("Error while saving file: " + srcFile.getName());
				throw new HttpCommandException(e);
			}
		}
		
		return null;
	}
}
