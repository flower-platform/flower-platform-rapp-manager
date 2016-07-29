package org.flowerplatform.rapp_manager.linux.library_installer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.flowerplatform.rapp_manager.library_manager.AbstractLibraryInstallerWrapper;
import org.flowerplatform.rapp_manager.library_manager.Library;
import org.flowerplatform.rapp_manager.linux.Constants;
import org.flowerplatform.rapp_manager.linux.FileUtils;
import org.flowerplatform.rapp_manager.linux.Main;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class LibraryInstaller extends AbstractLibraryInstallerWrapper {

	public static final String CMD_REMOVE_LIRBRARY = "/opt/flower-platform/bin/remove-library %s";

	public static final String CMD_INSTALL_LIRBRARY = "/opt/flower-platform/bin/install-library %s";
	
	@Override
	public void remove(Library lib) throws Exception {
		Main.logger.log(String.format("Removing library %s...", lib.getName()));
		Process p = Runtime.getRuntime().exec(String.format(CMD_REMOVE_LIRBRARY, lib.getName()));
		for (int c; (c = p.getInputStream().read()) != -1; ) {
			Main.logger.logSameLine("" + (char) c);
		}
		for (int c; (c = p.getErrorStream().read()) != -1; ) {
			Main.logger.logSameLine("" + (char) c);
		}
		p.waitFor();
	}

	@Override
	public void install(Library lib, Library replacedLib) throws Exception {
		Main.logger.log("Preparing folder...");
		File tmpDir = new File(Constants.TMP_DIR + "/" + lib.getName());
		FileUtils.deleteRecursively(tmpDir.toPath());
		tmpDir.mkdirs();
		String tmpPath = tmpDir.getAbsolutePath();
		
		Main.logger.log("Downloading and extracting library files...");
		InputStream in = new URL(lib.getUrl()).openStream();
		ZipInputStream zip = new ZipInputStream(in);
		
		File setupDir = null;
		for (ZipEntry entry; (entry = zip.getNextEntry()) != null; ) {
			String name = entry.getName();
			if (name.endsWith("/")) {
				new File(tmpPath + "/" + name).mkdirs();
				continue;
			}
			if (name.endsWith("setup.py")) {
				setupDir = new File(tmpPath + "/" + name.substring(0, name.lastIndexOf('/')));
			}
			Path out = new File(tmpPath + "/" + name).toPath();
			Files.copy(zip, out);
		}
		
		if (setupDir == null) {
			throw new RuntimeException(String.format("Could not find \"setup.py\" in library %s", lib.getName()));
		}
		
		Main.logger.log(String.format("Installing library %s...", lib.getName()));
		Process p = Runtime.getRuntime().exec(String.format(CMD_INSTALL_LIRBRARY, setupDir));
		for (int c; (c = p.getInputStream().read()) != -1; ) {
			Main.logger.logSameLine("" + (char) c);
		}
		for (int c; (c = p.getErrorStream().read()) != -1; ) {
			Main.logger.logSameLine("" + (char) c);
		}
		p.waitFor();
	}

	@Override
	public List<Library> getInstalledLibraries() {
		List<Library> res = new ArrayList<Library>();
		File libsDir = new File(Constants.LIB_DIR);
		
		File pthFile = new File(libsDir.getAbsolutePath() + "/easy-install.pth");
		if (!pthFile.exists()) {
			return res;
		}
		
		// parse easy-install.ph and find egg files included in path
		List<File> eggs = new ArrayList<>();
		try (BufferedReader pthIn = new BufferedReader(new FileReader(pthFile))) {
			for (String s; (s = pthIn.readLine()) != null;) {
				if (s.endsWith(".egg")) {
					eggs.add(new File(libsDir.getAbsolutePath() + "/" + s));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e); 
		}

		for (File egg : eggs) {
			ZipFile zip = null; 
			try {
				Library library = new Library();
				zip = new ZipFile(egg);
				ZipEntry entry = zip.getEntry("EGG-INFO/PKG-INFO");
				try (BufferedReader in = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)))) {
					// read library name and version from egg
					for (String s; (s = in.readLine()) != null;) {
						if (s.startsWith("Name")) {
							library.setName(s.substring(s.indexOf(':') + 1).trim());
						} else if (s.startsWith("Version")) {
							library.setVersion(s.substring(s.indexOf(':') + 1).trim());
						}
					}
				}
				zip.close();
				res.add(library);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				try { zip.close(); } catch (Exception e) { throw new RuntimeException(e); }
			}
		}

		return res;
		
	}

	@Override
	protected void initLibraryInstaller() {
		
	}

	@Override
	public Library createLibrary() {
		return new Library();
	}
	
	public static void main(String[] args) throws Exception {
		List<Library> libraries = new LibraryInstaller().getInstalledLibraries();
		for (Library library : libraries) {
			Main.logger.log(library.getName() + " " + library.getVersion());
		}
		
		Library lib = new Library();
		lib.setName("Adafruit-DHT");
		lib.setUrl("https://github.com/flower-platform/Adafruit_Python_DHT/archive/master.zip");
		new LibraryInstaller().install(lib, null);
		
		lib.setName("flower-platform-raspberry-pi-runtime");
		lib.setUrl("https://github.com/flower-platform/flower-platform-raspberry-pi-runtime/archive/master.zip");
		new LibraryInstaller().install(lib, null);

//		new LibraryInstaller().remove(lib);
		
	}
	
}
