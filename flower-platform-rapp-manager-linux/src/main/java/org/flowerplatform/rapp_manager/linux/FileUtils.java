package org.flowerplatform.rapp_manager.linux;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Collection of utilities for manipulating rapp-related filesystem. 
 * 
 * @author Andrei Taras
 */
public class FileUtils {
	
	/**
	 * Returns the name of the application folder associated with the given rapp.
	 */
	public static String getRappDirFilename(String rappId) {
		return String.format(Constants.RAPP_DIR_PATTERN, rappIdToFilesystemName(rappId));		
	}
	
	/**
	 * Same as {@link #getRappDirFilename(String)} except it returns a {@link File}
	 * instead of a file name.
	 */
	public static File getRappDir(String rappId) {
		return new File(getRappDirFilename(rappId));
	}
	
	/**
	 * Returns the list of files with the .PY extension associated with the given rappId.
	 */
	public static File[] getPyFiles(String rappId) {
		File rappDir = new File(getRappDirFilename(rappId));
		return rappDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(Constants.PY_EXTENSION);
			}
		}); 
	}
	
	/**
	 * Deletes all the files from the folder associated with the given rappId.
	 */
	public static void deleteFilesFromRappFolder(String rappId) throws IOException {
		deleteFilesFromDir(
			getRappDir(rappId)
		);
	}
	
	/**
	 * Deletes all the files from the specified folder.
	 */
	public static void deleteFilesFromDir(File dir) throws IOException {
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		for (File f : dir.listFiles()) {
			if (!(f.delete())) {
				throw new IOException("Can't delete file " + f.getAbsolutePath());
			}
		}
	}
	
	/**
	 * Converts a given {@code rappId} to a name which is suitable to be written
	 * on the file system.
	 */
	public static String rappIdToFilesystemName(String rappId) {
		if (rappId == null || rappId.length() == 0) {
			throw new NullPointerException("rappId cannot be null");
		}
		
		if (rappId.charAt(0) == '/') {
			rappId = rappId.substring(1);
		}
		
		return rappId.replaceAll("/", "|");
	}
	
	/**
	 * The reverse of {@link #rappIdToFilesystemName(String)} function; converts a filesystem
	 * folder name into a rappId.
	 */
	public static String filesystemNameToRappId(String folderName) {
		if (folderName == null || folderName.length() == 0) {
			throw new NullPointerException("rappId cannot be null");
		}

		return folderName.replaceAll("\\|", "/");
	}

	public static void deleteRecursively(final Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(final Path file, final IOException e) {
				return handleException(e);
			}

			private FileVisitResult handleException(final IOException e) {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(final Path dir, final IOException e) throws IOException {
				if (e != null) {
					return handleException(e);
				}
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		
		});
	};

}
