package org.flowerplatform.rapp_manager.arduino_ide;

import static org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin.log;

import java.io.IOException;
import java.net.BindException;
import java.util.Timer;
import java.util.TimerTask;

import org.flowerplatform.rapp_manager.arduino_ide.command.CompileCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.GetBoardsCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.GetLogCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.GetSelectedBoard;
import org.flowerplatform.rapp_manager.arduino_ide.command.GetStatusCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.SetOptionsCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.SetSelectedBoardCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.UpdateSourceFilesAndCompileCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.UpdateSourceFilesCommand;
import org.flowerplatform.rapp_manager.arduino_ide.command.UploadToBoardCommand;
import org.flowerplatform.tiny_http_server.CommandFactory;
import org.flowerplatform.tiny_http_server.HttpServer;
import org.flowerplatform.tiny_http_server.IHttpCommand;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Contains the logic necessary for initializing our {@link HttpServer} instance,
 * and for initializing our retry mechanism (i.e. if server can't bind on the given port
 * due to a {@link BindException}, then we wait and we retry binding until we succeed).
 * 
 * @author Andrei Taras
 */
public class HttpServerInitializer {

	private static final String ID = ("" + Math.random()).substring(2);
			
	/**
	 * The port we're trying to initialize our {@link HttpServer} instance on.
	 */
	private int port;
	
	/**
	 * The {@link FlowerPlatformPlugin} instance that we link this {@link HttpServer} instance 
	 * to.
	 */
	private FlowerPlatformPlugin flowerPlatformPlugin;
	
	/**
	 * The timer instance that we use to periodically check to see if port is now free.
	 */
	private Timer timer;
	
	/**
	 * The {@link HttpServer} instance that we use.
	 */
	private HttpServer server;
	
	/**
	 * Global switch that tells us if binding was successful. When set to true,
	 * we no longer try to re-bind on that given port.
	 */
	private Boolean bindingSucceded = false;
	
	public HttpServerInitializer(int port, FlowerPlatformPlugin flowerPlatformPlugin) {
		this.port = port;
		this.flowerPlatformPlugin = flowerPlatformPlugin;
		if (flowerPlatformPlugin == null) {
			throw new NullPointerException("The plugin cannot be null.");
		}
		
		initRetryMechanism();
	}
	
	/**
	 * Cleans up internals.
	 * This is necessary in order to clean up the timer. Since when hiding a particular editor, the JVM
	 * doesn't exit if there are other instances, the timer also continues to run. We want to 
	 * be able to cancel the timer.
	 */
	public void destroy() {
		debug("Destroy called.");
		timer.cancel();
		timer.purge();
		
		if (server != null) {
			try {
				server.stop();
			} catch (Exception ignored) {}
		}
	}
	
	private void initRetryMechanism() {
		timer = new Timer(true);
		
		timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					if (bindingSucceded) {
						debug("Detected successful binding. Cancelling timer...");
						// No need to retry anymore once we succeeded.
						timer.cancel();
						debug("Timer canceled.");
					} else {
						debug("Attempting to init server...");
						initServer();
					}
				}
			}, 
			0,   // Don't wait; run the task immediately 
			1000 // Repeat this forever, every 1000 milliseconds
		);
	}
	
	private void initServer() {
		try {
			server = new HttpServer(port);
		} catch (BindException be) {
			debug("Can't init server. Port is busy.");
			// BindException is a special one. It means that port is already taken. So we should
			// just ignore this exception, and wait until the next run to see if port got free.
			return;
		} catch (Throwable th) {
			log("General error while attempting to initialize the HTTP server.", th);
		}
		
		// set command factory, in order to inject plugin reference into the IFlowerPlatformPluginAware command instances
		server.setCommandFactory(new CommandFactory() {
			@Override
			public Object createCommandInstance(Class<? extends IHttpCommand> commandClass, Object data) {
				ObjectMapper mapper = new ObjectMapper();
				IHttpCommand command;
				try {
					command = mapper.readValue((String) data, commandClass);
					if (command instanceof IFlowerPlatformPluginAware) {
						((IFlowerPlatformPluginAware) command).setFlowerPlatformPlugin(flowerPlatformPlugin);
					}
					return command;
				} catch (IOException e) {
					e.printStackTrace(System.err);
					throw new RuntimeException("Cannot create command object", e);
				}
			}
		});
		server.registerCommand("uploadToBoard", UploadToBoardCommand.class);
		server.registerCommand("updateSourceFiles", UpdateSourceFilesCommand.class);
		server.registerCommand("compile", CompileCommand.class);
		server.registerCommand("updateSourceFilesAndCompile", UpdateSourceFilesAndCompileCommand.class);
		server.registerCommand("getBoards", GetBoardsCommand.class);
		server.registerCommand("getSelectedBoard", GetSelectedBoard.class);
		//server.registerCommand("getBoardsWithDetails", GetBoardsWithDetails.class);
		server.registerCommand("setSelectedBoard", SetSelectedBoardCommand.class);
		server.registerCommand("setOptions", SetOptionsCommand.class);
		server.registerCommand("getStatus", GetStatusCommand.class);
		server.registerCommand("getLog", GetLogCommand.class);

		debug("Binding successful on port " + port);
		bindingSucceded = true;
	}
	
	private static void debug(String message) {
		FlowerPlatformPlugin.debug("[ID=" + ID + "]" + message);
	}
}
