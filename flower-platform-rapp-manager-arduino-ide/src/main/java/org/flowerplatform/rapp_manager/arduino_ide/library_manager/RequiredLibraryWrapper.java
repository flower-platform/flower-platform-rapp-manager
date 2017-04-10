package org.flowerplatform.rapp_manager.arduino_ide.library_manager;
import org.flowerplatform.rapp_manager.library_manager.Library;

import processing.app.packages.UserLibrary;

/**
 * @author Cristian Spiescu
 */
public class RequiredLibraryWrapper extends UserLibrary {
	private Library requiredLibrary;
	
	public RequiredLibraryWrapper(Library requiredLibrary) {
		super();
		this.requiredLibrary = requiredLibrary;
	}
	
	@Override
	public String getUrl() {
		return (String) requiredLibrary.getUrl();
	}

	@Override
	public String getArchiveFileName() {
		return getName() + ".zip";
	}

	@Override
	public String getName() {
		return requiredLibrary.getName();
	}

	/**
	 * For pre 1.6.6, the checksum IS needed (by <code>DownloadableContributionsDownloader.download()</code>). So we calculate it
	 * to make the system happy. Further versions verify if the checksum is present.
	 */
//	@Override
//	public String getChecksum() {
//		if (librariesIndexer != null && checksum == null) {
//			try {
//				checksum = FileHash.hash(new File(librariesIndexer.getStagingFolder(), getArchiveFileName()), "SHA-256");
//			} catch (NoSuchAlgorithmException | IOException e) {
//				FlowerPlatformPlugin.log("Cannot calculate checksum", e);
//			}
//		}
//		return checksum;
//	}
	
}
