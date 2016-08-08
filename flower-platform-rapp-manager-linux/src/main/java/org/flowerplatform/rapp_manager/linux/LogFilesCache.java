package org.flowerplatform.rapp_manager.linux;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Keeps track of what we already served for various customers, for various log files
 * for our apps.
 * 
 * @author Andrei Taras
 */
public class LogFilesCache {
	private Map<String, SessionInfo> sessions = new HashMap<>(); 	// <fileName~token, (fileOffset, expiresAt)>
	
	private static final long SESSION_TIMEOUT_INTERVAL = 10 * 60 * 1000L;
	
	private long nextSessionCleanUpTimestamp = getExpiresAt();
	
	private static LogFilesCache INSTANCE = new LogFilesCache();
	private LogFilesCache() {};
	
	public static LogFilesCache get() {
		return INSTANCE;
	}
	
	//--------------------------------------------------------------------
	
	/**
	 * Performs maintenance work for this cache. Looks internally to see if there's any
	 * need to evict some sessions from the cache.
	 */
	public synchronized void maybePerformMaintenance() {
		Set<String> keysToRemove = new HashSet<>();
		
		if (System.currentTimeMillis() > nextSessionCleanUpTimestamp) {
			for (Map.Entry<String, SessionInfo> entry : sessions.entrySet()) {
				if (System.currentTimeMillis() > entry.getValue().expiresAt) {
					keysToRemove.add(entry.getKey());
				}
			}

			nextSessionCleanUpTimestamp = getExpiresAt();
		}
	}
	
	/**
	 * If the given key is not found, then a corresponding session is created.
	 */
	public synchronized SessionInfo getOrCreateSession(String key) {
		SessionInfo session = sessions.get(key);
		if (session == null) {
			session = new SessionInfo();
			sessions.put(key, session);
		}
		// Update the expiresAt flag with each access.
		session.expiresAt = getExpiresAt();
		
		return session;
	}
	
	public synchronized void invalidate(String key) {
		sessions.remove(key);
	}
	
	private static long getExpiresAt() {
		return System.currentTimeMillis() + SESSION_TIMEOUT_INTERVAL;
	}
	
	/**
	 * DTO for keeping relevant info about our session.
	 * 
	 * @author Andrei Taras
	 */
	public static final class SessionInfo {
		public Long offset;
		public Long expiresAt;
		
		public SessionInfo() {
		}
		public SessionInfo(Long offset, Long expiresAt) {
			this.offset = offset;
			this.expiresAt = expiresAt;
		}
	}
}
