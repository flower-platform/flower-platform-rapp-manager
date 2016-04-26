package org.flowerplatform.rapp_manager.linux.command;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import org.flowerplatform.tiny_http_server.IHttpCommand;


/**
 * 
 * @author Claudiu Matei
 *
 */
public class GetLogCommand implements IHttpCommand {

	private static Map<String, Long> logOffsets = new HashMap<>();
	
	private String rAppName;
	
	public Object run() {
		if (rAppName == null) {
			throw new IllegalArgumentException("rApp name not specified");
		}
		String logFileName = String.format("%s/log/%s.log", System.getProperty("user.home"), rAppName);
		try (RandomAccessFile logFile = new RandomAccessFile(logFileName, "r")) {
			Long offset = logOffsets.remove(rAppName);
			if (offset == null) {
				offset = logFile.length();
			}
			
			// wait for some new data to be written to the log file, but no mode than 8 seconds
			for (int i = 0; i < 40 && logFile.length() <= offset; i++) {
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
			if (offset + 2048 < fileLength) {
				sb.append("\n[...]\n");
				offset = fileLength - 2048;
			}
			logOffsets.put(rAppName, fileLength);
			logFile.seek(offset);
			byte[] data = new byte[(int)(fileLength - offset)];
			logFile.readFully(data);

			sb.append(new String(data));
			return sb.toString();
			
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public String getrAppName() {
		return rAppName;
	}

	public void setrAppName(String board) {
		this.rAppName = board;
	}
	
}
