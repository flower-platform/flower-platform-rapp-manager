package org.flowerplatform.rapp_manager.linux;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class Constants {

	public static final String SERVER_VERSION = "0.0.1";
	
	public static final String RAPPS_DIR_PATTERN = "%s/rapps";

	public static final String RAPP_DIR_PATTERN = RAPPS_DIR_PATTERN + "/%s";

	public static final String PID_FILE_PATTERN = "%s/run/%s.pid";

	public static final String RAPP_PROPERTIES_FILE_NAME = "rapp.properties";

	public static final String PROPERTY_START_AT_BOOT = "startAtBoot";

	public static final String BIN_PATH;
	
	static {
		BIN_PATH = "/opt/flower-platform/bin";
	}
	
}
