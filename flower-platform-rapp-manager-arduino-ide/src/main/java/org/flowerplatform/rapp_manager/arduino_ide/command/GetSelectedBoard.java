package org.flowerplatform.rapp_manager.arduino_ide.command;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.MenuListener;

import org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin;
import org.flowerplatform.rapp_manager.arduino_ide.IFlowerPlatformPluginAware;
import org.flowerplatform.rapp_manager.arduino_ide.model.Board;
import org.flowerplatform.tiny_http_server.IHttpCommand;

import processing.app.Editor;
import processing.app.I18n;

public class GetSelectedBoard implements IHttpCommand, IFlowerPlatformPluginAware {
	private FlowerPlatformPlugin plugin;
	
	public void setFlowerPlatformPlugin(FlowerPlatformPlugin plugin) {
		this.plugin = plugin;
	}
	
	public Object run() {
		Editor editor = plugin.getEditor();
		JMenuBar menuBar = editor.getJMenuBar();
		JMenu toolsMenu = menuBar.getMenu(3);
		toolsMenu.getListeners(MenuListener.class)[0].menuSelected(null);

		JMenu boardsMenu = null;
		for (Component c : toolsMenu.getMenuComponents()) {
			if (!(c instanceof JMenu) || !c.isVisible()) {
				continue;
			}
			JMenu menu = (JMenu) c;
			if (menu.getText().startsWith(I18n.tr(FlowerPlatformPlugin.BOARD_MENU_TEXT_KEY))) {
				boardsMenu = menu;
				break;
			}
		}
		
		// start from 1 (skip first entry - "Boards manager")
		for (int i = 1; i < boardsMenu.getItemCount(); i++) {
			JMenuItem item = boardsMenu.getItem(i);
			if (item != null && item.isEnabled() && item.isSelected()) {
				return new Board(item.getText(), item.isSelected());
			}
		}
		
		return null;
	}
}
