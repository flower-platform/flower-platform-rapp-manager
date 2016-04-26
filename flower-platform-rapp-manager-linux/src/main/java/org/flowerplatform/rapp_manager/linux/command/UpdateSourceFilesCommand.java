package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.RAPPS_DIR;
import static org.flowerplatform.rapp_manager.linux.Main.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.flowerplatform.rapp_manager.SourceFileDto;
import org.flowerplatform.rapp_manager.command.AbstractUpdateSourceFilesCommand;
import org.flowerplatform.tiny_http_server.HttpCommandException;

/**
 * Loads the given files into Arduino IDE, and invokes compile on them.
 * @author Claudiu Matei
 * @author Andrei Taras
 */
public class UpdateSourceFilesCommand extends AbstractUpdateSourceFilesCommand {
	
	/**
	 * Deletes all the files contained in the folder given as parameter.
	 * 
	 * @throws HttpCommandException if any problem occurrs.
	 */
	private void deleteFilesFromFolder(File dir) throws HttpCommandException {
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		for (File f : dir.listFiles()) {
			try {
				if (!(f.delete())) {
					throw new HttpCommandException("Can't delete file " + f.getAbsolutePath());
				}
			} catch (HttpCommandException hce) { 
				throw hce;
			} catch (Throwable th) {
				throw new HttpCommandException( String.format("Error while deleting file %s . Message is \"%s\".", f.getAbsolutePath(), th.getMessage()) );
			}
		}
	}

	@Override
	public Object run() throws HttpCommandException {
		File appDir = new File(String.format("%s/%s/%s", System.getProperty("user.home"), RAPPS_DIR, rAppName));
		appDir.mkdirs();
		
		// Make sure the working folder is clean (i.e. no unnecessary files)
		deleteFilesFromFolder(appDir);
		
		// save files to disk
		for (SourceFileDto srcFile : files) {
			try (FileOutputStream out = new FileOutputStream(appDir.getAbsolutePath() + File.separator + srcFile.getName())) {
				out.write(srcFile.getContents().getBytes());
			} catch (IOException e) {
				log("Error while saving file: " + srcFile.getName());
				throw new HttpCommandException(e);
			}
		}
		
		return null;

	}
	
}
