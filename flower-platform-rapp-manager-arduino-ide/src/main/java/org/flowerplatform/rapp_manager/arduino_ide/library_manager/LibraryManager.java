package org.flowerplatform.rapp_manager.arduino_ide.library_manager;

import java.util.List;

public class LibraryManager {

	
	
	public List<LibraryManagerEntry> analizeAndPopulate(LibraryTransferDto dto) {
		//TODO metoda analizeAndPopulate . Aici se populeaza Dto->entries pe baza dependintelor din Dto-> libraryDependencies. implementare similiara cu refreshTable din flowerino
		
		return null;
	}
	
	protected void applyActions(List<LibraryManagerEntry> entries) {
		//TODO metoda applyActions itereaza prin fiecare entry si ruleaza fiecare actiune setata. implementare similara cu metoda din flowerino
	}
	
	
}
