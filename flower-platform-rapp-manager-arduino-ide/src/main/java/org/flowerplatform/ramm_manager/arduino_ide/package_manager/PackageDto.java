package org.flowerplatform.ramm_manager.arduino_ide.package_manager;

public class PackageDto {
	private String packageName;
	private String platformVersion;
	

	private String platformArch;
	private boolean isInstalled;
	
	public String getPlatformVersion() {
		return platformVersion;
	}
	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
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
	public boolean isInstalled() {
		return isInstalled;
	}
	public void setInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}
	
	@Override
	public String toString() {
		return "[ " + packageName + ", " + platformArch + ", " + platformVersion + ", isInstalled: " + isInstalled + " ]";
	}
}
