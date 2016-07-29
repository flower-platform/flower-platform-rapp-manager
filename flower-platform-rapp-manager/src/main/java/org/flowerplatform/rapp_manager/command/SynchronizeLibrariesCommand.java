package org.flowerplatform.rapp_manager.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.flowerplatform.rapp_manager.library_manager.AbstractLibraryInstallerWrapper;
import org.flowerplatform.rapp_manager.library_manager.Library;
import org.flowerplatform.rapp_manager.library_manager.MatchedLibrary;
import org.flowerplatform.rapp_manager.library_manager.MatchedLibrary.Action;
import org.flowerplatform.rapp_manager.library_manager.MatchedLibrary.Status;
import org.flowerplatform.rapp_manager.util.AbstractLogger;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Silviu Negoita
 */
public class SynchronizeLibrariesCommand implements IHttpCommand {
	private List<Library> requiredLibraries;
	private boolean dryRun;
	private boolean duplicateLibraries;
	AbstractLogger logger;

	@JsonIgnore
	protected transient AbstractLibraryInstallerWrapper installer;
	
	public AbstractLogger getLogger() {
		return logger;
	}

	public void setLogger(AbstractLogger logger) {
		this.logger = logger;
	}

	public List<Library> getRequiredLibraries() {
		return requiredLibraries;
	}

	public void setRequiredLibraries(List<Library> requiredLibraries) {
		this.requiredLibraries = requiredLibraries;
	}

	public boolean isDryRun() {
		return dryRun;
	}

	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	public boolean isDuplicateLibraries() {
		return duplicateLibraries;
	}

	public void setDuplicateLibraries(boolean duplicateLibraries) {
		this.duplicateLibraries = duplicateLibraries;
	}
	
	public Object run() throws HttpCommandException {
		if (dryRun) {
			return analizeAndPopulate(requiredLibraries);
		} else {
			List<MatchedLibrary> result = analizeAndPopulate(requiredLibraries);
			applyActions(result);
			return result;
		}
	}
	
	public List<MatchedLibrary> analizeAndPopulate(List<Library> requiredLibs) {
		List<MatchedLibrary> matchedLibraries = new ArrayList<MatchedLibrary>();
		// index all the required libs by header file
		Map<String, Library> lookup = new HashMap<String, Library>();
		for (Library lib : requiredLibs) {
			for (String headerFile : lib.getHeaderFiles()) {
				lookup.put(headerFile, lib);
			}
		}
		
		// iteration over all libs on the disk
		for (Library lib : installer.getInstalledLibraries()) {
			MatchedLibrary entry = new MatchedLibrary();
			entry.setExistingLibrary(installer.createLibrary());
			entry.setRequiredLibrary(installer.createLibrary());
			entry.getExistingLibrary().setHeaderFiles(lib.getHeaderFiles());
			
			// we try to match a required lib, based on the lib from the disk (based on header files)
			Set<String> remainingHeaderFilesToCheck = null;
			for (String headerFile : entry.getExistingLibrary().getHeaderFiles()) {
				if (remainingHeaderFilesToCheck == null) {
					// no match found yet; try again
					Library requiredLib = lookup.get(headerFile);
					if (requiredLib == null) {
						// try next file; maybe this lib IS among the required ones, but this particular
						// header file is not needed
						continue;
					}
					// aha, we found a match...
					matchedLibraries.add(entry);
					if (requiredLib.isMatched()) {
						duplicateLibraries = true;
					}
					requiredLib.setMatched(true);
					entry.setRequiredLibrary(requiredLib);
					entry.setName(requiredLib.getName());
					entry.setExistingLibrary(lib);
					// let's continue the iteration, to make sure that all required
					// header files exist on the disk
					remainingHeaderFilesToCheck = new HashSet<String>(Arrays.asList(requiredLib.getHeaderFiles()));
				}
				// if we are here => we have found a library
				remainingHeaderFilesToCheck.remove(headerFile);
			}
			
			if (remainingHeaderFilesToCheck != null) {
				// i.e. we found a match
				if (remainingHeaderFilesToCheck.isEmpty()) {
					// all the required header files have been matched
					// what about the libraries names?
					if (!entry.getExistingLibrary().getName().equals(entry.getRequiredLibrary().getName())) {
						entry.setStatus(Status.NEEDS_DELETE);
						entry.setAction(Action.DELETE);
					} else 
					// what about the version?
					if (entry.getExistingLibrary().getVersion() == null) {
							entry.setStatus(Status.UNKNOWN);
							entry.setAction(Action.DOWNLOAD);
					} else {
						versionCompare((String) entry.getRequiredLibrary().getVersion(), (String) entry.getExistingLibrary().getVersion());
						if (versionCompare((String) entry.getRequiredLibrary().getVersion(), (String) entry.getExistingLibrary().getVersion()) < 0) {
							entry.setStatus(Status.NEEDS_UPDATE);
							entry.setAction(Action.DOWNLOAD);
						} else {
							entry.setStatus(Status.OK);
							entry.setAction(Action.NONE);
						}
					}
				} else {
					// there are still required header files; probably the existing lib is older
					entry.setStatus(Status.NEEDS_UPDATE);
					entry.setAction(Action.DOWNLOAD);
				}
			} // else => the current lib is not among the required ones
		}
		
		// and now let's see if all required libs have been matched
		for (Library requiredLib : requiredLibs) {
			if (requiredLib.isMatched()) {
				continue;
			}
			// aha, this one was not matched
			MatchedLibrary entry = new MatchedLibrary();
			entry.setExistingLibrary(installer.createLibrary());
			entry.setRequiredLibrary(installer.createLibrary());
			matchedLibraries.add(entry);
			
			entry.setRequiredLibrary(requiredLib);
			entry.setName((String) requiredLib.getName());

			entry.setStatus(Status.NEEDS_DOWNLOAD);
			entry.setAction(Action.DOWNLOAD);
		}		
		return matchedLibraries;
	}
	
	public static int versionCompare(String str1, String str2) {
	    String[] vals1 = str1.split("\\.");
	    String[] vals2 = str2.split("\\.");
	    int i = 0;
	    // set index to first non-equal ordinal or length of shortest version string
	    while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
	      i++;
	    }
	    // compare first non-equal ordinal number
	    if (i < vals1.length && i < vals2.length) {
	        int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
	        return Integer.signum(diff);
	    }
	    // the strings are equal or one string is a substring of the other
	    // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
	    return Integer.signum(vals1.length - vals2.length);
	}
	
	protected void applyActions(List<MatchedLibrary> entries) {		
		try {
			for (MatchedLibrary entry : entries) {
				logger.log("For required library: " + entry.getName() + ", applying action: " + entry.getAction());
				switch (entry.getAction()) {
				case DELETE:
					if (entry.getExistingLibrary() == null) {
						break;
					}
					installer.remove(entry.getExistingLibrary());
					break;
				case DOWNLOAD:
					installer.install(entry.getRequiredLibrary(), entry.getExistingLibrary());
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			logger.log("Error while applying actions", e);
		}
	}
	
	@JsonIgnore
	public void setInstaller(AbstractLibraryInstallerWrapper installer) {
		this.installer = installer;
	}

}
