package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.flowerplatform.rapp_manager.command.AbstractCompileCommand;
import org.flowerplatform.rapp_manager.linux.CompilationException;
import org.flowerplatform.rapp_manager.linux.FileUtils;
import org.flowerplatform.tiny_http_server.HttpCommandException;

/**
 * 
 * @author Claudiu Matei
 */
public class CompileCommand extends AbstractCompileCommand {
	private static final String COMPILE_COMMAND = "python -m py_compile %s";
	
	public Object run() throws HttpCommandException {
		if (rappId == null) {
			throw new IllegalArgumentException("Rapp name not specified");
		}
		Process p;
		StringBuilder compilationLog = new StringBuilder();
		try {
			compilationLog.append("Compiling " + rappId + "... \n");
			
			File[] pyFiles = FileUtils.getPyFiles(rappId);
			if (pyFiles != null && pyFiles.length > 0) {
				for (File f : pyFiles) {
					compilationLog.append("Processing file " + f + " \n");
					String cmd = String.format(COMPILE_COMMAND, f.getPath());
					p = Runtime.getRuntime().exec(cmd);
					
					// read error stream
					StringBuilder compilationErrors = new StringBuilder(); 
					try (InputStream in = p.getErrorStream()) {
						ByteArrayOutputStream result = new ByteArrayOutputStream();
						byte[] buf = new byte[1024];
						int n;
						while ((n = in.read(buf)) != -1) {
						    result.write(buf, 0, n);
						}
						compilationErrors.append(result.toString("UTF8") + "\n");
					}
					
					p.waitFor();
					
					if (p.exitValue() != 0) {
						compilationLog.append(compilationErrors);
						logger.log(compilationLog.toString());
						throw new CompilationException(compilationLog.toString());
					}
				}
			} else {
				compilationLog.append("No *.py files provided. Nothing to compile ! \n");
				throw new CompilationException(compilationLog.toString());
			}
			compilationLog.append("Successfully compiled application " + rappId + "\n");
			return compilationLog.toString();
		} catch (IOException | InterruptedException e) {
			logger.log("Error occurred.", e);
			throw new HttpCommandException(e.getMessage(), e);
		} finally {
			logger.log(compilationLog.toString());
		}
	}
}
