package org.flowerplatform.rapp_manager.linux;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class Constants {

	public static final String BIN_PATH;

	public static final String WORK_DIR = System.getProperty("user.home"); 
	
	public static final String RAPPS_DIR = WORK_DIR + "/rapps";

	public static final String RAPP_DIR_PATTERN = RAPPS_DIR + "/%s";

	public static final String LOG_DIR = WORK_DIR + "/log";
	
	public static final String LOG_FILE_PATTERN = LOG_DIR + "/%s.log";
	
	public static final String PID_DIR = WORK_DIR + "/run";

	public static final String PID_FILE_PATTERN = PID_DIR + "/%s.pid";

	public static final String LIB_DIR = WORK_DIR + "/lib";
	
	public static final String RAPP_PROPERTIES_FILE_NAME = "rapp.properties";

	public static final String PROPERTY_START_AT_BOOT = "startAtBoot";
	
	/**
	 * The extension of a Python source-code file.
	 */
	public static final String PY_EXTENSION = ".py";
	
	static {
		BIN_PATH = "/opt/flower-platform/bin";
	}
	
}
