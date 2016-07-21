package org.flowerplatform.rapp_manager.arduino_ide.package_manager;

import static processing.app.I18n.tr;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.github.zafarkhaja.semver.Version;

import cc.arduino.Constants;
import cc.arduino.contributions.DownloadableContributionsDownloader;
import cc.arduino.contributions.GPGDetachedSignatureVerifier;
import cc.arduino.contributions.ProgressListener;
import cc.arduino.contributions.VersionHelper;
import cc.arduino.contributions.packages.ContributedPackage;
import cc.arduino.contributions.packages.ContributedPlatform;
import cc.arduino.contributions.packages.ContributionInstaller;
import cc.arduino.contributions.packages.ContributionsIndex;
import cc.arduino.contributions.packages.ContributionsIndexer;
import cc.arduino.utils.MultiStepProgress;
import cc.arduino.utils.Progress;
import processing.app.BaseNoGui;
import processing.app.PreferencesData;

public class PackagesInstallerWrapper extends AbstractPackagesInstallerWrapper {
	
	ContributionInstaller contributionInstaller;
	ContributionsIndexer contributionIndexer;
	
	ProgressListener progressListener;
	
	public PackagesInstallerWrapper(String packageUrl) throws Exception {progressListener = new ProgressListener() {
		@Override
			public void onProgress(Progress progress) {
				System.out.println(progress.getStatus());
			}
		};
		// delete all .tmp files from settings directory. any .tmp file can cause system instability.
		clearDirectoryFromTemporaryFiles(BaseNoGui.getSettingsFolder());
		// get contribution installer from Arduino IDE
		contributionInstaller = new ContributionInstaller(BaseNoGui.getPlatform(), new GPGDetachedSignatureVerifier());
		
		// get contribution indexer from Arduino IDE
		contributionIndexer = BaseNoGui.indexer;
		
		// set this property because is used by updateIndex method for Arduino IDE. this holds urls to contribution packages.
		packageUrl = packageUrl.replace("\r\n", "\n").replace("\r", "\n").replace("\n", ",");
		String additionalURLs = PreferencesData.get(Constants.PREF_BOARDS_MANAGER_ADDITIONAL_URLS, "");
		
		if (additionalURLs != null && additionalURLs.length() > 4) {
			// preserve old additional urls and add the new one. 
			if (!additionalURLs.contains(packageUrl)) { // add only if it is not already there.
				PreferencesData.set(Constants.PREF_BOARDS_MANAGER_ADDITIONAL_URLS, additionalURLs + "," +  packageUrl);
				reDownloadJsonsAndSync();
			}
		} else { 
			// just set this new url to preferences
			PreferencesData.set(Constants.PREF_BOARDS_MANAGER_ADDITIONAL_URLS, packageUrl);
			// this is new URL and we need to download JSONs
			reDownloadJsonsAndSync();
		}
		// invoke save command in Arduino IDE to write settings in file
		saveSettingsInArduinoIDE();
	}
	@Override
	public Object[] install(String name, String architecture, String version, String url) throws Exception {
		// refresh info about available packages
		reDownloadJsonsAndSync();
		ContributedPlatform founded = findPlatformByNameArchVersion(name, architecture, version);
		if (founded != null) { 
			// if we found anything we don't do nothing. 	
			if (founded.isInstalled())
				return new String[] {"Platform already installed. Exiting"};
			else {
				List<String> installResult = contributionInstaller.install(founded, progressListener);
				return installResult.toArray(new String[installResult.size()]);
			}
		} else { //if we don't find this platform in already downloaded packages, we try to index from specified url and manually search for it in index
			File downloadedJson = download(url);
			ContributionsIndex index = parseIndexFromFile(downloadedJson);
			Version toFindVersion = VersionHelper.valueOf(version);
			// iterate over all contributed packages available
			for(ContributedPackage contributedPackage : parseAndGetContributedPackages(index))
				if (contributedPackage.getName().equals(name))
				// iterate over all platforms inside required package
				for (ContributedPlatform contributedPlatform : parseAndGetContributedPackages(index).get(0).getPlatforms()) {
					if (architecture.equals(contributedPlatform.getArchitecture()) 
							&& toFindVersion.equals(VersionHelper.valueOf(contributedPlatform.getVersion()))
							) {
						List<String> installResult = contributionInstaller.install(contributedPlatform, progressListener);
						return installResult.toArray(new String[installResult.size()]);
					}
				}
		}
		// we can't find and install specified platform even with the url given. hope that this will not happen
		return null;
	}

	@Override
	public Object[] remove(String name, String architecture, String version) throws Exception {
		ContributedPlatform founded = findPlatformByNameArchVersion(name, architecture, version);
		if (founded != null && founded.isInstalled()) {
			List<String> removeResult = contributionInstaller.remove(founded);
			return removeResult.toArray(new String[removeResult.size()]);
		}
		return new String[] {"Platform given for remove not found. Exiting"};
	}
	
	/**
	 * This method download a file from specified url in ArduinoIde workspace. Logic copied from ArduinoIde code.
	 */
	public File download(String indexUrl) throws Exception {
		MultiStepProgress progress = new MultiStepProgress(1);
		String statusText = tr("Downloading platforms index...");
		URL url = new URL(indexUrl);
		String[] urlPathParts = url.getFile().split("/");
		File outputFile = BaseNoGui.indexer.getIndexFile(urlPathParts[urlPathParts.length - 1]);
		File tmpFile = new File(outputFile.getAbsolutePath() + ".tmp");
		DownloadableContributionsDownloader downloader = new DownloadableContributionsDownloader(null);
		downloader.download(url, tmpFile, progress, statusText, progressListener);
		
		Files.deleteIfExists(outputFile.toPath());
		Files.move(tmpFile.toPath(), outputFile.toPath());
		
		return outputFile;
	}
	
	/**
	 * This method return a list of ContributedPackage after it resolve all toolsDependencies.
	 * Logic copied from ArduinoIDE
	 * @param index
	 * @return
	 */
	public List<ContributedPackage> parseAndGetContributedPackages(ContributionsIndex index) {
		List<ContributedPackage> packages = index.getPackages();
	    Collection<ContributedPackage> packagesWithTools = packages.stream()
	      .filter(input -> input.getTools() != null && !input.getTools().isEmpty())
	      .collect(Collectors.toList());
	    
		for (ContributedPackage pack : index.getPackages()) {
		      for (ContributedPlatform platform : pack.getPlatforms()) {
		        // Set a reference to parent packages
		        platform.setParentPackage(pack);

		        // Resolve tools dependencies (works also as a check for file integrity)
		        platform.resolveToolsDependencies(packagesWithTools);
		      }
		    }
		return packages;
	}
	
	/**
	 * This method receive a json file to parse it. It use reflection to run a private method ("parseIndex(File json)") 
	 * from contributionIndexer
	 * @param jsonFile 
	 * @return ContributionsIndex witch will be used to install or remove specific platform from a package.
	 * @throws Exception
	 */
	public ContributionsIndex parseIndexFromFile(File jsonFile) throws Exception {
		Method parseIndex = contributionIndexer.getClass().getDeclaredMethod("parseIndex", File.class);
		parseIndex.setAccessible(true);
		return (ContributionsIndex) parseIndex.invoke(contributionIndexer, jsonFile);
	}
	
	/**
	 * This method force to redownload all JSONS from available URLs( default and aditionalURLs) and reload data in memory.
	 * This is very useful when we need to download resycronize data from HDD with Arduino IDE
	 * @throws Exception 
	 */
	public void reDownloadJsonsAndSync() throws Exception {
		// this command redownload jsons ( both from AdruinoIDE and additional URLs) - it can block due to server failure.
		contributionInstaller.updateIndex(progressListener);
		// this command parse previous downloaded JSONs and load them in memory for further analysis
		BaseNoGui.indexer.parseIndex();
		BaseNoGui.indexer.syncWithFilesystem(BaseNoGui.getHardwareFolder());
	}
	
	public ContributedPlatform findPlatformByNameArchVersion(String name, String architecture, String version) throws Exception {
		// search a platform in data previously loaded
		return contributionIndexer.getIndex().findPlatform(name, architecture, version);
	}
	
	public void clearDirectoryFromTemporaryFiles(File directory) {
		File fList[] = directory.listFiles();
        for (File f : fList) {
            if (f.getName().endsWith(".tmp")) {
                f.delete(); 
            }}
	}
	/**
	 * This method use java reflection to invoke save method form Arduino IDE. This method write all settings in file.
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	public void saveSettingsInArduinoIDE() throws Exception {
		Method save = PreferencesData.class.getDeclaredMethod("save");
		save.setAccessible(true);
		save.invoke(null);
	}
}
