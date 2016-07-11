package org.flowerplatform.rapp_manager.linux.command;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.flowerplatform.rapp_manager.linux.Constants;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;


/**
 * 
 * @author Claudiu Matei
 *
 */
public class GetLogCommand implements IHttpCommand {

	private static final long SESSION_TIMEOUT_INTERVAL = 10 * 60 * 1000L;
	
	private static final int RESPONSE_MINIMUM_TIME = 1000; // multiple of 200ms

	private static final int MAX_SEND_SIZE = 10240; // max number of bytes to be sent in response; the last MAX_SEND_SIZE bytes are sent, previous bytes are ignored
	
	private static Map<String, Long> logOffsets = new ConcurrentHashMap<>(); 	// <fileName~token, offset>

	private static Map<String, Long> sessionExpirationTimestamps = new ConcurrentHashMap<>(); 	// <fileName~token, expiration_timestamp>
	
	private long nextSessionCleanUpTimestamp = System.currentTimeMillis() + SESSION_TIMEOUT_INTERVAL;
	
	private String rappName;
	
	private String token;

	public Object run() throws HttpCommandException {
		if (System.currentTimeMillis() > nextSessionCleanUpTimestamp) {
			cleanUpSessions();
		}
		if (rappName == null) {
			throw new IllegalArgumentException("Rapp name not specified");
		}
		if (token == null) {
			throw new IllegalArgumentException("Token not specified");
		}
		String offsetKey = rappName + "~" + token;
		
		String logFileName = String.format(Constants.LOG_FILE_PATTERN, rappName);
		try (RandomAccessFile logFile = new RandomAccessFile(logFileName, "r")) {
			Long offset = logOffsets.remove(offsetKey);
			if (offset == null) {
				offset = Math.max(0, logFile.length() - 16384);
			}
			
			// wait for some new data to be written to the log file; wait no less than a second and no more than 8 seconds
			for (int i = 0; i < 40 && (i < RESPONSE_MINIMUM_TIME / 200 || logFile.length() <= offset); i++) { 
				try { Thread.sleep(200); } catch (Exception e) { }
			}
			
			// wait until log file has stable length for 50ms; wait no more than 500ms 
			long fileLength = -1;
			for (int i = 0; i < 10; i++) {
				fileLength = logFile.length();
				try { Thread.sleep(50); } catch (Exception e) { }
				if (logFile.length() == fileLength) {
					break;
				}
			}
			
			StringBuffer sb = new StringBuffer();
			// read at most MAX_SEND_SIZE bytes into sb
			if (offset + MAX_SEND_SIZE < fileLength) {
				sb.append("\n[...]\n");
				offset = fileLength - MAX_SEND_SIZE;
			}
			
			logOffsets.put(offsetKey, fileLength);
			sessionExpirationTimestamps.put(offsetKey, System.currentTimeMillis() + SESSION_TIMEOUT_INTERVAL);
			
			logFile.seek(offset);
			byte[] data = new byte[(int)(fileLength - offset)];
			logFile.readFully(data);

			sb.append(new String(data));
			return sb.toString().getBytes();
			
		} catch (IOException e) {
			throw new HttpCommandException(e.getMessage(), e);
		}
	}

	public void cleanUpSessions() {
		Iterator<Entry<String, Long>> it = sessionExpirationTimestamps.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Long> entry = it.next();
			if (System.currentTimeMillis() > entry.getValue()) {
				it.remove();
				logOffsets.remove(entry.getKey());
			}
		}
		nextSessionCleanUpTimestamp = System.currentTimeMillis() + SESSION_TIMEOUT_INTERVAL;
	}
	
	public String getRappName() {
		return rappName;
	}

	public void setRappName(String rappName) {
		this.rappName = rappName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
