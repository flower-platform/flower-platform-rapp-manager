package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.log;

import java.io.File;
import java.io.RandomAccessFile;

import org.flowerplatform.rapp_manager.command.AbstractRappCommand;
import org.flowerplatform.rapp_manager.linux.Constants;
import org.flowerplatform.rapp_manager.linux.FileUtils;
import org.flowerplatform.rapp_manager.linux.LogFilesCache;
import org.flowerplatform.rapp_manager.linux.LogFilesCache.SessionInfo;
import org.flowerplatform.tiny_http_server.HttpCommandException;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class GetLogCommand extends AbstractRappCommand {

	private static final int RESPONSE_MINIMUM_TIME = 1000; // multiple of 200ms

	private static final int MAX_SEND_SIZE = 10240; // max number of bytes to be sent in response; the last MAX_SEND_SIZE bytes are sent, previous bytes are ignored

	/**
	 * The http code of the error thrown when the given token can no longer be used for communication, 
	 * and the client should issue a new request, if it wants the info.
	 */
	private static final int HTTP_CODE_TOKEN_NO_LONGER_VALID = 310;
	
	private String token;

	public Object run() throws HttpCommandException {
		LogFilesCache.get().maybePerformMaintenance();
		
		if (rappId == null) {
			throw new IllegalArgumentException("Rapp name not specified");
		}
		if (token == null) {
			throw new IllegalArgumentException("Token not specified");
		}
		String offsetKey = rappId + "~" + token;
		
		// if log file does not exist, return;
		File logFilePath = new File(String.format(Constants.LOG_FILE_PATTERN, FileUtils.rappIdToFilesystemName(rappId)));
		if (!logFilePath.exists()) {
			return "".getBytes();
		}
		
		try (RandomAccessFile logFile = new RandomAccessFile(logFilePath, "r")) {
			SessionInfo sessionInfo = LogFilesCache.get().getOrCreateSession(offsetKey);
			if (sessionInfo.offset == null) {
				sessionInfo.offset = Math.max(0, logFile.length() - MAX_SEND_SIZE);
			}
			
			// wait for some new data to be written to the log file; wait no less than a second and no more than 8 seconds
			for (int i = 0; i < 40 && (i < RESPONSE_MINIMUM_TIME / 200 || logFile.length() == sessionInfo.offset); i++) { 
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
			
			// Quick sanity check; this can happen when the corresponding app has been restarted, and thus
			// the log was also restarted.
			// In this case, we send a custom HTTP code, to tell the client that it needs to issue a new request.
			if (sessionInfo.offset > fileLength) {
				LogFilesCache.get().invalidate(offsetKey);
				log("GetLog request with token " + token + " got automatically invalidated.");
				throw new HttpCommandException(HTTP_CODE_TOKEN_NO_LONGER_VALID, "Given token is no longer valid. Please issue a new request with a new token.");
			}
			
			StringBuffer sb = new StringBuffer();
			
			// Ensure that we don't grab a chunk larger than we want to process.
			if (sessionInfo.offset + MAX_SEND_SIZE < fileLength) {
				sb.append("\n[...]\n");
				sessionInfo.offset = fileLength - MAX_SEND_SIZE;
			}
			
			long start = sessionInfo.offset;
			sessionInfo.offset = fileLength;
			logFile.seek(start);
			
			byte[] data = new byte[(int)(fileLength - start)];
			logFile.readFully(data);

			sb.append(new String(data));
			return sb.toString().getBytes();
			
		} catch (HttpCommandException hce) {
			// A HttpCommandException always passes through.
			throw hce;
		} catch (Throwable th) {
			log("Error calculating log bytes", th);
			throw new HttpCommandException(th.getMessage(), th);
		}
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
