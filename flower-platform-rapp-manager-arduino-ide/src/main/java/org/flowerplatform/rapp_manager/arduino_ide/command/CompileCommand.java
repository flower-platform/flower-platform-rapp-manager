package org.flowerplatform.rapp_manager.arduino_ide.command;

import static org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin.log;

import org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin;
import org.flowerplatform.rapp_manager.arduino_ide.IFlowerPlatformPluginAware;
import org.flowerplatform.rapp_manager.arduino_ide.util.RunnableWithListener;
import org.flowerplatform.rapp_manager.arduino_ide.util.StartEndListener;
import org.flowerplatform.rapp_manager.arduino_ide.util.Util;
import org.flowerplatform.rapp_manager.command.AbstractCompileCommand;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.ReflectionException;

import processing.app.Editor;

/**
 * Loads the given files into Arduino IDE, and invokes compile on them.
 * @author Claudiu Matei
 * @author Andrei Taras
 */
public class CompileCommand extends AbstractCompileCommand implements IFlowerPlatformPluginAware {

	private FlowerPlatformPlugin plugin;
	
	private CompilationResult compilationResult;

	@Override
	public void setFlowerPlatformPlugin(FlowerPlatformPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public Object run() throws HttpCommandException {
		// Trigger compilation as if the user clicked on "Verify / Compile" 
		compilationResult = new CompilationResult();
		invokeCompile(new CompilationListener() {
			@Override
			public void compilationSuccessful() {
				compilationResult.finished = true;
			}
			@Override
			public void compilationFailed(HttpCommandException error) {
				compilationResult.finished = true;
				compilationResult.error = error;
			}
		});
		
		// Wait until the compilation result becomes available
		waitForCompilationResult();
		
		if (!compilationResult.finished) {
			throw new HttpCommandException("Arduino IDE did not finished compiling in the allotted amount of time.");
		} else {
			if (compilationResult.error != null) {
				if (compilationResult.error instanceof HttpCommandException) {
					throw ((HttpCommandException)compilationResult.error);
				} else {
					throw new HttpCommandException(compilationResult.error);
				}
			} else {
				// Don't do anything; if compilation ok, just return as normal.
				return null;
			}
		}
	}

	/**
	 * Invokes the editor's compile function.
	 */
	private void invokeCompile(final CompilationListener compilationListener) throws HttpCommandException {
		final Editor editor = plugin.getEditor();
		
		StartEndListener compilationEndListener = new StartEndListener() {
			@Override
			public void start() {
				// We don't really care to intercept this event, so do nothing here. This is here just in case.
			}
			@Override
			public void end() {
				try {
					convertEditorStatusIntoCompilationResult(editor, compilationListener);
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e1) {
					log("Error while attempting to invoke compile.", e1);
					// Don't forward this exception; just mark the fact that we've got an error,
					// and rely on future processing to notify this error to the caller.
					compilationListener.compilationFailed(new ReflectionException("Reflection error.", e1));
				} catch (Throwable th) {
					log("Error while attempting to invoke compile.", th);
					compilationListener.compilationFailed(new HttpCommandException("General error.", th));
				}
			}
		};

		try {
			RunnableWithListener presentHandlerWrapper = new RunnableWithListener((Runnable) Util.getPrivateField(Editor.class, editor, "presentHandler"), compilationEndListener);
			RunnableWithListener runHandlerWrapper = new RunnableWithListener((Runnable) Util.getPrivateField(Editor.class, editor, "runHandler"), compilationEndListener);
			
			editor.handleRun(false, presentHandlerWrapper, runHandlerWrapper);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e1) {
			log("Reflection error while attempting to invoke compile.", e1);
			compilationListener.compilationFailed(new ReflectionException("Reflection error.", e1));
		} catch (Throwable th) {
			log("Compilation error", th);
			compilationListener.compilationFailed(new HttpCommandException("Compilation error : " + th.getMessage()));
		}
	}

	/**
	 * Pauses current thread until the compilation result becomes available.
	 */
 	private void waitForCompilationResult() {
		// Arduino IDE runs the compilation on a separate thread; we need to wait until
		// the compilation is done, until we can read the results.
		
		// Call wait() in intervals of waitIntervalMilliseconds milliseconds.
		long waitIntervalMilliseconds = 500;
		// Wait for the compilation to finish for a maximum of maxWaitMilliseconds milliseconds.
		long maxWaitMilliseconds = 120 * waitIntervalMilliseconds;
		
		while (maxWaitMilliseconds > 0) {
			try {
				synchronized (this) {
					wait(waitIntervalMilliseconds);
				}
			} catch (InterruptedException ignored) { }
			
			maxWaitMilliseconds -= waitIntervalMilliseconds;
			
			if (compilationResult.finished) {
				break;
			}
		}
	}
	
	/**
	 * Utility function; analyzes the internal field "status" of the Arduino IDE main Editor, and
	 * calls the appropiate {@link CompilationListener#compilationSuccessful()} or
	 * {@link CompilationListener#compilationFailed(HttpCommandException)()} method.
	 */
	private void convertEditorStatusIntoCompilationResult(Editor editor, CompilationListener compilationListener) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		Util.EditorStatus editorStatus = Util.readStatus(editor);
		
		if (editorStatus.status == editorStatus.ERR) {
			compilationListener.compilationFailed(new HttpCommandException(editorStatus.message));
		} else {
			compilationListener.compilationSuccessful();
		}
	}
	
	private static interface CompilationListener {
		void compilationSuccessful();
		void compilationFailed(HttpCommandException error);
	}
	
	private static class CompilationResult {
		public boolean finished;
		public Throwable error;
	}
	
}
