package org.flowerplatform.ramm_manager.arduino_ide.package_manager;

import java.util.List;

import cc.arduino.contributions.packages.ContributedPlatform;


/**
 * Wrapper for install/remove platform for Arduino IDE
 * @author Silviu Negoita
 *
 */
public abstract class AbstractPackagesInstallerWrapper {
	/**
	 * Method used to install a platform in Arduino IDE.
	 * @param name package name
	 * @param architecture platform architecture
	 * @param version platform version
	 * @param url platform url to download from
	 * @return list of messages regarding install process(errors or just some logs)
	 * @throws Exception
	 */
	public abstract Object[] install(String name, String architecture, String version, String url) throws Exception;
	
	/**
	 * Method used to remove a platform from Arduini IDE.
	 * @param name package name
	 * @param architecture platform architecture
	 * @param version platform version
	 * @return list of messages regarding remove process(errors or just some logs)
	 * @throws Exception
	 */
	public abstract Object[] remove(String name, String architecture, String version) throws Exception;
	
	/**
	 * This method is used when dryRun option was set in command. It only scan current installed packages and return some 
	 * informations about given platform to find.
	 * @param name package name
	 * @param architecture platform architecture
	 * @param version platform version
	 * @return PackageDto 
	 */
	public abstract ContributedPlatform findPlatformByNameArchVersion(String name, String architecture, String version)  throws Exception;
}
