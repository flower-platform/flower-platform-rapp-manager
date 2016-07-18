package org.flowerplatform.rapp_manager.linux;

import static org.flowerplatform.rapp_manager.linux.Constants.PID_FILE_PATTERN;
import static org.flowerplatform.rapp_manager.linux.Constants.PROPERTY_START_AT_BOOT;
import static org.flowerplatform.rapp_manager.linux.Constants.RAPPS_DIR;
import static org.flowerplatform.rapp_manager.linux.Main.log;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
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
			rapps.add(FileUtils.filesystemNameToRappId(rappDir.getName()));
		}
		return rapps;
	}
	
	/**
	 * Check if a rapp is running.
	 * 
	 * @param rappId
	 * @return
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static boolean isRappRunning(String rappId) throws IOException, InterruptedException {
		String pidFile = String.format(PID_FILE_PATTERN, FileUtils.rappIdToFilesystemName(rappId));
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
	 * Returns the creation timestamp for the given {@code rappId}.
	 * This timestamp is actually the creation date of the {@code rappId} folder.
	 */
	public static Long getRappTimestamp(String rappId) throws IOException {
		File rappDir = FileUtils.getRappDir(rappId);		
		
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

	public static boolean getStartAtBootFlag(String rappId) throws IOException {
		Properties rappProperties = getProperties(rappId);
		return rappProperties.getProperty(PROPERTY_START_AT_BOOT, "false").equalsIgnoreCase("true");
	}

	/**
	 * Loads rapp properties from file. If properties file is not found, returns a blank Properties object. 
	 * @param rappId
	 * @return
	 * @throws IOException
	 */
	public static Properties getProperties(String rappId) throws IOException {
		File rappDir = FileUtils.getRappDir(rappId);
		if (!rappDir.exists()) {
			throw new RuntimeException(String.format("Rapp not found: %s", rappId));
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
	 * @param rappId
	 * @param properties
	 * @throws IOException
	 */
	public static void saveProperties(String rappId, Properties properties) throws IOException {
		File rappDir = FileUtils.getRappDir(rappId);
		if (!rappDir.exists()) {
			throw new RuntimeException(String.format("Rapp not found: %s", rappId));
		}
		File rappPropertiesFile = new File(rappDir.getAbsolutePath() + File.separator + Constants.RAPP_PROPERTIES_FILE_NAME);
		properties.store(new FileOutputStream(rappPropertiesFile), String.format("Properties for %s", rappId));
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
			rapp.setRappId(
				FileUtils.filesystemNameToRappId(rappDir.getName())
			);
			
			if (detailed) {
				try {
					rapp.setRunning(Util.isRappRunning(rapp.getRappId()));
					rapp.setStartAtBoot(Util.getStartAtBootFlag(rapp.getRappId()));
					rapp.setUploadTimestamp(Util.getRappTimestamp(rapp.getRappId()));
				} catch (IOException | InterruptedException e) {
					log(e.getMessage(), e);
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			rapps.add(rapp);	
		}
		
		return rapps;
	}
	
	/**
	 * Converts the given input stream into a string.
	 */
	public static String slurp(final InputStream is, final int bufferSize) throws UnsupportedEncodingException, IOException {
	    final char[] buffer = new char[bufferSize];
	    final StringBuilder out = new StringBuilder();
	    try (Reader in = new InputStreamReader(is, "UTF-8")) {
	        for (;;) {
	            int rsz = in.read(buffer, 0, buffer.length);
	            if (rsz < 0)
	                break;
	            out.append(buffer, 0, rsz);
	        }
	    } catch (UnsupportedEncodingException ex) {
	    	throw ex;
	    } catch (IOException ex) {
	    	throw ex;
	    }
	    return out.toString();
	}	
}
