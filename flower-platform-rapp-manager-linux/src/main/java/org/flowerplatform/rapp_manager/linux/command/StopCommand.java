package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.log;
import static org.flowerplatform.rapp_manager.linux.Main.logp;

import java.io.IOException;
import java.io.InputStream;

import org.flowerplatform.rapp_manager.command.AbstractRappCommand;
import org.flowerplatform.rapp_manager.linux.Constants;
import org.flowerplatform.rapp_manager.linux.FileUtils;
import org.flowerplatform.rapp_manager.linux.Util;
import org.flowerplatform.tiny_http_server.HttpCommandException;

/**
 * @author Claudiu Matei
 */
public class StopCommand extends AbstractRappCommand {

	/**
	 * The format of the command line invocation :
	 * stop-app home_dir rappId filesystemName
	 */
	private static final String STOP_APP_COMMAND = "/bin/bash " + Constants.BIN_PATH + "/stop-app %s %s %s";
	
	public Object run() throws HttpCommandException {
		if (rappId == null) {
			throw new IllegalArgumentException("Rapp name not specified");
		}

		// check if rapp is already running
		try {
			if (!Util.isRappRunning(rappId)) {
				throw new HttpCommandException("Rapp is not running.");
			}
		} catch (IOException | InterruptedException e) {
			throw new HttpCommandException(e.getMessage(), e);
		}
		
		Process p;
		try {
			logp("Stopping rapp: " + rappId);
			String cmd = String.format(STOP_APP_COMMAND, Constants.WORK_DIR, rappId, FileUtils.rappIdToFilesystemName(rappId));
			p = Runtime.getRuntime().exec(cmd);
			logp("...");
			p.waitFor();
			String processOutput = processOutputAsString(p);
			
			if (p.exitValue() != 0) {
				log("failed : " + processOutput);
				log(Util.slurp(p.getErrorStream(), 1024));
				throw new RuntimeException("Error stopping rapp " + rappId + "; The output of the process was: \n\n" + processOutput);
			} else {
				log("done : " + processOutput);
			}
			return "Rapp stopped: " + rappId;
		} catch (IOException | InterruptedException e) {
			log("Error stopping app", e);
			throw new HttpCommandException(e.getMessage());
		}
	}
	
	// Converts the output of the given process to a String
	private static String processOutputAsString(Process p) throws UnsupportedOperationException, IOException {
		StringBuilder result = new StringBuilder();
		InputStream stdoutStream = p.getInputStream(), stderrStream = p.getErrorStream();
		
		if (stdoutStream != null) {
			result.append("Normal output:\n");
			result.append(Util.slurp(stdoutStream, 4096));
		}
		if (stderrStream != null) {
			if (result.length() > 0) {
				result.append("\n\n");
			}
			result.append("Err output:\n");
			result.append(Util.slurp(stderrStream, 4096));
		}
		
		return result.toString();
	}
}
