package org.flowerplatform.ramm_manager.arduino_ide.package_manager;

import static processing.app.I18n.tr;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.github.zafarkhaja.semver.Version;

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

public class PackagesInstallerWrapper extends AbstractPackagesInstallerWrapper {
	
	ContributionInstaller contributionInstaller;
	ContributionsIndexer contributionIndexer;
	
	ProgressListener progressListener;
	
	public PackagesInstallerWrapper() throws Exception {progressListener = new ProgressListener() {
		@Override
		public void onProgress(Progress progress) {
			System.out.println(progress.getStatus());
		}
		};
		contributionInstaller = new ContributionInstaller(BaseNoGui.getPlatform(), new GPGDetachedSignatureVerifier());
		contributionInstaller.updateIndex(progressListener);
		
		contributionIndexer = BaseNoGui.indexer;
		
	}
	@Override
	public List<String> install(String name, String architecture, String version, String url) throws Exception {
		ContributedPlatform founded = contributionIndexer.getIndex().findPlatform(name, architecture, version);
		if (founded != null) { 
			// if we find specified platform in already downloaded and indexed package
			// and it is not installed, install. 				
			if (!founded.isInstalled()) {
				return contributionInstaller.install(founded, progressListener);
			} else {
				return Arrays.asList("Platform already installed. Exiting");
			}
		} else { //if we don't find this platform in already downloaded packages, we try to index from specified url and manually search for it
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
						return contributionInstaller.install(contributedPlatform, progressListener);
					}
				}
		}
		// we can't find and install specified platform even with the url given. hope that this will not happen
		return null;
	}

	@Override
	public List<String> remove(String name, String architecture, String version) throws Exception {
		ContributedPlatform founded = contributionIndexer.getIndex().findPlatform(name, architecture, version);
		if (founded != null && founded.isInstalled())
			return contributionInstaller.remove(founded);
		
		return Arrays.asList("Platform given for remove not found. Exiting");
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
	 * Method used by GWT client as command of a dry run. This only scan current installed packages and return some 
	 * informations about given platform to find.
	 * @param name
	 * @param architecture
	 * @param version
	 * @return
	 */
	public PackageDto findPlatformByNameArchVersion(String name, String architecture, String version) {
		ContributedPlatform founded = contributionIndexer.getIndex().findPlatform(name, architecture, version);
		if (founded == null) return null;
		
		PackageDto result = new PackageDto();
		result.setInstalled(founded.isInstalled());
		result.setPlatformArch(founded.getName());
		result.setPlatformArch(founded.getArchitecture());
		result.setPackageName(founded.getParentPackage().getName());
		return result;
	}
	
}
