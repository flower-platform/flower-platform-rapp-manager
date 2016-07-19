package org.flowerplatform.rapp_manager.arduino_ide.command;

import org.flowerplatform.ramm_manager.arduino_ide.package_manager.AbstractPackagesInstallerWrapper;
import org.flowerplatform.ramm_manager.arduino_ide.package_manager.PackagesInstallerWrapper;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;

import cc.arduino.Constants;
import processing.app.BaseNoGui;
import processing.app.PreferencesData;

public class InstallPackageCommand implements IHttpCommand{
	private String packageName;
	private String packageUrl;
	private String platformArch;
	private String platformVersion;
	private boolean toInstall;
	
	public boolean isToInstall() {
		return toInstall;
	}

	public void setToInstall(boolean toInstall) {
		this.toInstall = toInstall;
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
			AbstractPackagesInstallerWrapper installer =  new PackagesInstallerWrapper(packageUrl);
			if (toInstall)
				return installer.install(packageName, platformArch, platformVersion, packageUrl);
			else 
				return installer.remove(packageName, platformArch, platformVersion);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// hope this will not reach
		return null;
	}

}
