package org.flowerplatform.rapp_manager.arduino_ide.command;

import org.flowerplatform.rapp_manager.arduino_ide.package_manager.AbstractPackagesInstallerWrapper;
import org.flowerplatform.rapp_manager.arduino_ide.package_manager.PackagesInstallerWrapper;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;

import cc.arduino.contributions.packages.ContributedPlatform;


public class CheckPackageIfInstalledCommand implements IHttpCommand {
	private String packageName;
	private String platformArch;
	private String platformVersion;
	private String packageUrl;
	
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
	
	@Override
	public Object run() throws HttpCommandException {
		try {
			AbstractPackagesInstallerWrapper installer =  new PackagesInstallerWrapper(packageUrl);
			ContributedPlatform founded = installer.findPlatformByNameArchVersion(packageName, platformArch, platformVersion);
			if (founded != null && founded.isInstalled()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// hope this will not reach
		return false;
	}

	public String getPackageUrl() {
		return packageUrl;
	}

	public void setPackageUrl(String packageUrl) {
		this.packageUrl = packageUrl;
	}
}
