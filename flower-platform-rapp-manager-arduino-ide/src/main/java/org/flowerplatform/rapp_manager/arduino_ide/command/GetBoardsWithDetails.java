package org.flowerplatform.rapp_manager.arduino_ide.command;

import java.util.ArrayList;
import java.util.List;

import org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin;
import org.flowerplatform.rapp_manager.arduino_ide.IFlowerPlatformPluginAware;
import org.flowerplatform.rapp_manager.arduino_ide.model.Board;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;

import processing.app.BaseNoGui;
import processing.app.debug.TargetBoard;
import processing.app.debug.TargetPackage;
import processing.app.debug.TargetPlatform;

/**
 * Returns the list of boards available within Arduino IDE, along with their associated
 * details/properties. 
 * 
 * @author Andrei Taras
 * 
 * TODO : This class should be removed I think; this functionality is already somehwere else.
 */
public class GetBoardsWithDetails implements IHttpCommand, IFlowerPlatformPluginAware {

	private FlowerPlatformPlugin plugin;

	@Override
	public Object run() throws HttpCommandException {
		List<Board> boards = new ArrayList<>();
		
	    for (TargetPackage targetPackage : BaseNoGui.packages.values()) {
			for (TargetPlatform targetPlatform : targetPackage.platforms()) {
				for (TargetBoard board : targetPlatform.getBoards().values()) {
					boards.add(
						new Board(board.getName(), false)
					);
				}
			}
	    }

		return boards;
	}

	@Override
	public void setFlowerPlatformPlugin(FlowerPlatformPlugin plugin) {
		this.plugin = plugin;
	}
}
