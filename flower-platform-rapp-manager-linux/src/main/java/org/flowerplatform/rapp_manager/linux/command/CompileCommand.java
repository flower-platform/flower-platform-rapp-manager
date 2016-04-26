package org.flowerplatform.rapp_manager.linux.command;

import static org.flowerplatform.rapp_manager.linux.Main.RAPPS_DIR;
import static org.flowerplatform.rapp_manager.linux.Main.log;
import static org.flowerplatform.rapp_manager.linux.Main.logp;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.flowerplatform.rapp_manager.command.AbstractCompileCommand;


/**
 * 
 * @author Claudiu Matei
 *
 */
public class CompileCommand extends AbstractCompileCommand {

	private static final String COMPILE_COMMAND = "python -m py_compile %s";
	
	private static final String PY_EXTENSION = ".py";
	
	public Object run() {
		if (rAppName == null) {
			throw new IllegalArgumentException("rApp name not specified");
		}
		Process p;
		try {
			log("Compiling rApp: " + rAppName);
			File appDir = new File(String.format("%s/%s/%s", System.getProperty("user.home"), RAPPS_DIR, rAppName));
			File[] pyFiles = appDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(PY_EXTENSION);
				}
			});
			for (File f : pyFiles) {
				String cmd = String.format(COMPILE_COMMAND, f.getPath());
				p = Runtime.getRuntime().exec(cmd);
				logp(String.format("Compiling %s...", f.getPath()));
				p.waitFor();
				if (p.exitValue() != 0) {
					log("failed");
					throw new RuntimeException("Error compiling: " + f.getName());
				}
				log("done");
			}
			log("Compilation finished successfully");
			return "rApp compiled: " + rAppName;
		} catch (IOException | InterruptedException e) {
			return e.getMessage();
		} 	
	}

}
