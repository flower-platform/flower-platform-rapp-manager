package org.flowerplatform.rapp_manager.arduino_ide.command;

import org.flowerplatform.ramm_manager.arduino_ide.package_manager.AbstractPackagesInstallerWrapper;
import org.flowerplatform.ramm_manager.arduino_ide.package_manager.PackagesInstallerWrapper;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;

/**
 * This command is used to install/remove a platform inside a package. 
 * A platform is characterized from a package name(which contains platform to handle) platform name and platform version. 
 * When install selected, url to download from should be provided
 * @author Silviu Negoita
 *
 */
public class SynchronizePackagesCommand implements IHttpCommand {
	
	private String packageName;
	private String packageUrl;
	private String platformArch;
	private String platformVersion;
	private boolean dryRun;
	private boolean toInstall;
	
	public boolean isToInstall() {
		return toInstall;
	}

	public void setToInstall(boolean toInstall) {
		this.toInstall = toInstall;
	}

	public boolean isDryRun() {
		return dryRun;
	}

	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPlatformArch() {
		return platformArch;
	}

	public void setPlatformArch(String platformArch) {
		this.platformArch = platformArch;
	}
	
	public String getPlatformVersion() {
		return platformVersion;
	}

	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
	}

	public String getPackageUrl() {
		return packageUrl;
	}

	public void setPackageUrl(String packageUrl) {
		this.packageUrl = packageUrl;
	}

	
	@Override
	public Object run() throws HttpCommandException {
		try {
			AbstractPackagesInstallerWrapper installer =  new PackagesInstallerWrapper();
			if (dryRun) {
				return installer.findPlatformByNameArchVersion(packageName, platformArch, platformVersion);
			} else {
				if (toInstall)
					return installer.install(packageName, platformArch, platformVersion, packageUrl);
				else 
					return installer.remove(packageName, platformArch, platformVersion);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// hope this will not reach
		return null;
	}

}
