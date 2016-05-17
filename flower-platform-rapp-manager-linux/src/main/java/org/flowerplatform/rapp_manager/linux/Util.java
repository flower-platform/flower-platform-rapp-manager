package org.flowerplatform.rapp_manager.linux;

import static org.flowerplatform.rapp_manager.linux.Constants.PID_FILE_PATTERN;
import static org.flowerplatform.rapp_manager.linux.Constants.RAPPS_DIR_PATTERN;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class Util {

	/**
	 * Check if a rapp is running.
	 * 
	 * @param rappName
	 * @return
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static boolean isRappRunning(String rappName) throws IOException, InterruptedException {
		String pidFile = String.format(PID_FILE_PATTERN, System.getProperty("user.home"), rappName);
		File f = new File(pidFile);
		if (!f.exists()) {
			return false;
		}

		// read pid from file
		int pid;
		try (Scanner scanner = new Scanner(new FileInputStream(f))) {
			pid = scanner.nextInt();	
		}
		
		// check if process with id "pid" is running
		String cmd = String.format("ps -p %s", pid);
		long t = System.currentTimeMillis();
		Process p = Runtime.getRuntime().exec(cmd);
		System.out.println("run " + (System.currentTimeMillis() - t));
		p.waitFor();
		
		return p.exitValue() == 0;
		
	}

	/**
	 * Gets the "startAtBoot" flag from rapp's properties file if exists, or returns false if properties file does not exist.
	 * @param rappName
	 * @return
	 * @throws IOException
	 */
	public static boolean getStartAtBootFlag(String rappName) throws IOException {
		File rappsDir = new File(String.format(RAPPS_DIR_PATTERN, System.getProperty("user.home")));
		File rappDir = new File(rappsDir.getAbsolutePath() + File.separator + rappName);
		if (!rappDir.exists()) {
			throw new RuntimeException(String.format("Rapp not found: %s", rappName));
		}
		File rappPropertiesFile = new File(rappDir.getAbsolutePath() + File.separator + Constants.RAPP_PROPERTIES_FILE_NAME);
		if (rappPropertiesFile.exists()) {
			try (FileInputStream in = new FileInputStream(rappPropertiesFile)) {
				Properties rappProperties = new Properties();
				rappProperties.load(in);
				return rappProperties.getProperty("startAtBoot", "false").equalsIgnoreCase("true");
			}
		}
		return false;
	}

	/**
	 * @author Claudiu Matei
	 */
	public static void deleteFilesFromFolder(File dir) throws IOException {
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		for (File f : dir.listFiles()) {
			if (!(f.delete())) {
				throw new IOException("Can't delete file " + f.getAbsolutePath());
			}
		}
	}

	
}
