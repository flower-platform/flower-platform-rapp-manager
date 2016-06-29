package org.flowerplatform.rapp_manager.arduino_ide.util;

import java.awt.Component;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.flowerplatform.rapp_manager.arduino_ide.FlowerPlatformPlugin;
import org.flowerplatform.rapp_manager.arduino_ide.model.BoardOption;
import org.flowerplatform.rapp_manager.arduino_ide.model.BoardProperty;

import processing.app.Editor;
import processing.app.I18n;

/**
 * Utility class.
 * 
 * @author Andrei Taras
 */
public class Util {
	/**
	 * Hack-ish method that retrieves a private field from the given class instance.
	 * This is used to access stuff from within the main editor.
	 * Please note that all exceptions are forwarded.
	 */
	public static <T>T getPrivateField(Class<?> clazz, Object instance, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		Field field = clazz.getDeclaredField(fieldName); 
		field.setAccessible(true);
		
		@SuppressWarnings("unchecked")
		T privateField = (T)field.get(instance);
		
		return privateField;
	}
	
	public static <T>void setPrivateField(Class<?> clazz, Object instance, String fieldName, T newValue) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		
		field.set(instance, newValue);
	}
	
	/**
	 * Reads the status field from the given editor instance.
	 * @param editor
	 * @return
	 */
	public static EditorStatus readStatus(Editor editor) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		processing.app.EditorStatus editorStatus = Util.getPrivateField(Editor.class, editor, "status");

		EditorStatus result = new EditorStatus();
		
		result.ERR = Util.<Integer>getPrivateField(processing.app.EditorStatus.class, processing.app.EditorStatus.class, "ERR");
		result.status = Util.getPrivateField(processing.app.EditorStatus.class, editorStatus, "mode");
		result.message = Util.getPrivateField(processing.app.EditorStatus.class, editorStatus, "message");

		return result;
	}
	
	/**
	 * Retrieves the list of extra options associated with the currently selected board.
	 * These options are present in the Tools menu, and they are different from board to board.
	 */
	public static List<BoardProperty> getToolsMenuExtraBoardOptions(JMenu toolsMenu) {
		List<BoardProperty> properties = new ArrayList<>();
		
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
				List<BoardOption> values = new ArrayList<>();
				for (int i = 0; i <  menu.getItemCount(); i++) {
					JMenuItem item = menu.getItem(i);
					if (item != null && item.isEnabled() && item.isVisible()) {
						values.add(new BoardOption(item.getText(), item.isSelected()));
					}
				}
				
				properties.add(new BoardProperty(option, values));
			}
		}		
		
		return properties;
	}
	
	public static class EditorStatus {
		/**
		 * The actual value that represents an error (i.e. when status == ERR, then we've got an error).
		 */
		public int ERR;
		
		public int status;
		public String message;
	}
}
