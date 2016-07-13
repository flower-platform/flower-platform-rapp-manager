package org.flowerplatform.rapp_manager.linux;

import static org.flowerplatform.rapp_manager.linux.Constants.PID_FILE_PATTERN;
import static org.flowerplatform.rapp_manager.linux.Constants.PROPERTY_START_AT_BOOT;
import static org.flowerplatform.rapp_manager.linux.Constants.RAPPS_DIR;
import static org.flowerplatform.rapp_manager.linux.Constants.RAPP_DIR_PATTERN;
import static org.flowerplatform.rapp_manager.linux.Main.log;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class Util {

	public static List<String> getInstalledRapps() {
		File rappsDir = new File(RAPPS_DIR);
		File[] rappDirs = rappsDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		List<String> rapps = new ArrayList<>();
		for (File rappDir : rappDirs) {
			rapps.add(rappDir.getName());
		}
		return rapps;
	}
	
	/**
	 * Check if a rapp is running.
	 * 
	 * @param rappName
	 * @return
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static boolean isRappRunning(String rappName) throws IOException, InterruptedException {
		String pidFile = String.format(PID_FILE_PATTERN, rappName);
		File f = new File(pidFile);
		if (!f.exists()) {
			return false;
		}

		// read pid from file
		int pid;
		try (Scanner scanner = new Scanner(new FileInputStream(f))) {
			pid = scanner.nextInt();	
		}
		
		return isProcessRunning(pid);
		
	}

	public static boolean isProcessRunning(int pid) throws IOException, InterruptedException {
		// check if process with id "pid" is running
		String cmd = String.format("ps -p %s", pid);
		Process p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		
		return p.exitValue() == 0;
	}

	/**
	 * Returns the creation timestamp for the given {@code rappName}.
	 * This timestamp is actually the creation date of the {@code rappName} folder.
	 */
	public static Long getRappTimestamp(String rappName) throws IOException {
		File rappsDir = new File(RAPPS_DIR);
		File rappDir = new File(rappsDir.getAbsolutePath() + File.separator + rappName);		
		
		if (rappDir.exists()) {
			BasicFileAttributes attributes = Files.readAttributes(rappDir.toPath(), BasicFileAttributes.class);
			if (attributes != null && attributes.creationTime() != null) {
				return attributes.creationTime().toMillis();
			}
		} else {
			System.err.println("WARNING: Can't find rapp dir " + rappDir.getAbsolutePath());
		}
		
		return null;
	}

	public static boolean getStartAtBootFlag(String rappName) throws IOException {
		Properties rappProperties = getProperties(rappName);
		return rappProperties.getProperty(PROPERTY_START_AT_BOOT, "false").equalsIgnoreCase("true");
	}

	/**
	 * Loads rapp properties from file. If properties file is not found, returns a blank Properties object. 
	 * @param rappName
	 * @return
	 * @throws IOException
	 */
	public static Properties getProperties(String rappName) throws IOException {
		File rappsDir = new File(RAPPS_DIR);
		File rappDir = new File(rappsDir.getAbsolutePath() + File.separator + rappName);
		if (!rappDir.exists()) {
			throw new RuntimeException(String.format("Rapp not found: %s", rappName));
		}
		File rappPropertiesFile = new File(rappDir.getAbsolutePath() + File.separator + Constants.RAPP_PROPERTIES_FILE_NAME);
		Properties rappProperties = new Properties();
		if (rappPropertiesFile.exists()) {
			try (FileInputStream in = new FileInputStream(rappPropertiesFile)) {
				rappProperties.load(in);
			}
		}
		return rappProperties;
	}

	/**
	 * Saves properties for the specified rapp.
	 * @param rappName
	 * @param properties
	 * @throws IOException
	 */
	public static void saveProperties(String rappName, Properties properties) throws IOException {
		File rappDir = new File(String.format(Constants.RAPP_DIR_PATTERN, rappName));
		if (!rappDir.exists()) {
			throw new RuntimeException(String.format("Rapp not found: %s", rappName));
		}
		File rappPropertiesFile = new File(rappDir.getAbsolutePath() + File.separator + Constants.RAPP_PROPERTIES_FILE_NAME);
		properties.store(new FileOutputStream(rappPropertiesFile), String.format("Properties for %s", rappName));
	}
	
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
	
	/**
	 * Returns a status for each application, for the given dir set.
	 * Status can be a summary (i.e. currently, nothing but the name of the app), or 
	 * detailed (running status, etc). 
	 */
	public static List<RappDescriptor> getRappsStatus(File[] rappDirs, boolean detailed) {
		List<RappDescriptor> rapps = new ArrayList<>();
		for (File rappDir : rappDirs) {
			RappDescriptor rapp = new RappDescriptor();
			rapp.setName(rappDir.getName());
			
			if (detailed) {
				try {
					rapp.setRunning(Util.isRappRunning(rapp.getName()));
					rapp.setStartAtBoot(Util.getStartAtBootFlag(rapp.getName()));
					rapp.setUploadTimestamp(Util.getRappTimestamp(rapp.getName()));
				} catch (IOException | InterruptedException e) {
					log(e.getMessage(), e);
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			rapps.add(rapp);	
		}
		
		return rapps;
	}
}
