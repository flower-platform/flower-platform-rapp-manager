package org.flowerplatform.ramm_manager.arduino_ide.package_manager;

import java.util.List;


public abstract class AbstractPackagesInstallerWrapper {
	public abstract List<String> install(String name, String architecture, String version, String url) throws Exception;
	public abstract List<String> remove(String name, String architecture, String version) throws Exception;
	public abstract PackageDto findPlatformByNameArchVersion(String name, String architecture, String version);
}
