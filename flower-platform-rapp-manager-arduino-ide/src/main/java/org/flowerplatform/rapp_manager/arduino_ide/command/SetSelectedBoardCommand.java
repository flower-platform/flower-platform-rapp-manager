package org.flowerplatform.rapp_manager.arduino_ide.command;

import static org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin.log;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.MenuListener;

import org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin;
import org.flowerplatform.rapp_manager.arduino_ide.IFlowerPlatformPluginAware;
import org.flowerplatform.rapp_manager.arduino_ide.model.Board;
import org.flowerplatform.rapp_manager.arduino_ide.model.BoardOption;
import org.flowerplatform.rapp_manager.arduino_ide.model.BoardProperty;
import org.flowerplatform.tiny_http_server.IHttpCommand;

import processing.app.Editor;
import processing.app.I18n;

/**
 * @author Claudiu Matei
 * @author Andrei Taras
 */
public class SetSelectedBoardCommand extends Board implements IHttpCommand, IFlowerPlatformPluginAware {

	private FlowerPlatformPlugin plugin;
	
	public Object run() {
		Editor editor = plugin.getEditor();
		JMenuBar menuBar = editor.getJMenuBar();
		JMenu toolsMenu = menuBar.getMenu(3);
		toolsMenu.getListeners(MenuListener.class)[0].menuSelected(null);
		toolsMenu.getListeners(MenuListener.class)[1].menuSelected(null);

		// find "Board" menu
		JMenu boardMenu = null;
		for (Component c : toolsMenu.getMenuComponents()) {
			if (!(c instanceof JMenu) || !c.isVisible()) {
				continue;
			}
			JMenu menu = (JMenu) c;
			if (menu.getText().startsWith(I18n.tr(FlowerPlatformPlugin.BOARD_MENU_TEXT_KEY))) {
				boardMenu = menu;
				break;
			}
		}
		
		// select board
		boolean boardFound = false;
		for (int i = 0; i < boardMenu.getItemCount(); i++) {
			JMenuItem item = boardMenu.getItem(i);
			if (item != null && item.isEnabled()) {
				if (item.getText().equals(this.getName())) {
					boardFound = true;
					item.setSelected(true);
					item.getActionListeners()[0].actionPerformed(null);
					log("Board selected: " + this.getName());
				}
			}
		}
		if (!boardFound) {
			throw new RuntimeException("Invalid board name or board package not installed.");
		}
		
		for (Component c : toolsMenu.getMenuComponents()) {
			if (!(c instanceof JMenu) || !c.isVisible()) {
				continue;
			}
			JMenu menu = (JMenu) c;
			if (menu.getText().startsWith(I18n.tr(FlowerPlatformPlugin.BOARD_MENU_TEXT_KEY))) {
				boardMenu = menu;
				break;
			}
		}

		// get menu options
		Map<String, List<String>> options = new HashMap<>(); 
		for (Component c : toolsMenu.getMenuComponents()) {
			if ((c instanceof JMenu) && c.isVisible()) {
				JMenu menu = (JMenu) c;
				String option = menu.getText();
				if (option == null) {
					continue;
				}
				int index = option.indexOf(':');
				if (index > 0) {
					option = option.substring(0, index);
				}
				if (option.equals(I18n.tr(FlowerPlatformPlugin.BOARD_MENU_TEXT_KEY)) || option.equals(I18n.tr(FlowerPlatformPlugin.PROGRAMMER_MENU_TEXT_KEY))) {
					continue;
				}
				List<String> values = new ArrayList<>();
				for (int i = 0; i <  menu.getItemCount(); i++) {
					JMenuItem item = menu.getItem(i);
					if (item != null && item.isVisible()) {
						values.add(item.getText());
					}
					if (isOptionSelected(option, item.getText())) {
						// Also set the selected flag if necessary
						item.setSelected(true);
						item.getActionListeners()[0].actionPerformed(null);
					}
				}
				options.put(option, values);
			}
		}

		
		return options;
	}

	@Override
	public void setFlowerPlatformPlugin(FlowerPlatformPlugin plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Searches in the param board, among the list of properties and returns true if the 
	 * one given as param is selected.
	 */
	private boolean isOptionSelected(String property, String optionValue) {
		List<BoardProperty> properties = getProperties();
		if (properties != null) {
			for (BoardProperty boardProperty : properties) {
				if (property.equals(boardProperty.getName())) {
					List<BoardOption> options = boardProperty.getOptions();
					if (options != null) {
						for (BoardOption boardOption : options) {
							if (optionValue.equals(boardOption.getName())) {
								return boardOption.isSelected();
							}
						}
					}
				}
			}
		}
		
		return false;
	}
}
