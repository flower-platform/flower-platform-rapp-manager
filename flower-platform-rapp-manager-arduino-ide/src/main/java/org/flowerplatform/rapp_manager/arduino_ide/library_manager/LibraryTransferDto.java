package org.flowerplatform.rapp_manager.arduino_ide.library_manager;

import java.util.List;
import java.util.Map;

/**
 * Used for data transfer from gwt client(or other web client) and rap-manager (through tinny-http-server). 
 * All communications between this two entities is made with this DTO.
 * 
 * @author Silviu Negoita
 */
public class LibraryTransferDto {
	//TODO List<LibraryManagerEntry> entries este populat la return din LibraryManager.analizeAndPopulate. la primul apel(cand se trimite doar libraryDependencies) este setat pe null
	//TODO de discutat daca folosesc aceasta structura(LibraryManagerEntry) eventual modificata sau creeaz alta noua.
	List<LibraryManagerEntry> entries;
	
	//TODO List<Map<String, List<Object>>> libraryDependencies; trimise ca parametru pentru metoda (LibraryManager.analizeAndPopulate) de populare ui + analiza librarii de pe disk.
	List<Map<String, List<Object>>> libraryDependencies;
	
}
