package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;

import org.flowerplatform.rapp_manager.command.AbstractCompileCommand;
import org.flowerplatform.rapp_manager.linux.CompilationException;
import org.flowerplatform.rapp_manager.linux.Constants;


/**
 * 
 * @author Claudiu Matei
 *
 */
public class CompileCommand extends AbstractCompileCommand {

	private static final String COMPILE_COMMAND = "python -m py_compile %s";
	
	private static final String PY_EXTENSION = ".py";
	
	public Object run() {
		if (rappName == null) {
			throw new IllegalArgumentException("Rapp name not specified");
		}
		Process p;
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("Compiling " + rappName + "... ");
			File rappDir = new File(String.format(Constants.RAPP_DIR_PATTERN, System.getProperty("user.home"), rappName));
			File[] pyFiles = rappDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(PY_EXTENSION);
				}
			});
			for (File f : pyFiles) {
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
					compilationErrors.append(result.toString("UTF8"));
				}
				
				p.waitFor();
				
				if (p.exitValue() != 0) {
					sb.append("\n" + compilationErrors);
					log(sb.toString());
					throw new CompilationException(sb.toString());
				}
			}
			sb.append("OK");
			log(sb.toString());
			return sb.toString().getBytes();
		} catch (IOException | InterruptedException e) {
			return e.getMessage();
		}
	}

}
