package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.log;

import java.io.File;
import java.io.IOException;

import org.flowerplatform.rapp_manager.command.AbstractCompileCommand;
import org.flowerplatform.rapp_manager.linux.CompilationException;
import org.flowerplatform.rapp_manager.linux.FileUtils;
import org.flowerplatform.rapp_manager.linux.Util;
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
					
					p.waitFor();
					
					if (p.exitValue() != 0) {
						String compilationErrors = Util.slurp(p.getErrorStream());
						compilationLog.append(compilationErrors);
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
			log("Error occurred.", e);
			throw new HttpCommandException(e.getMessage(), e);
		} finally {
			log(compilationLog.toString());
		}
	}

}
