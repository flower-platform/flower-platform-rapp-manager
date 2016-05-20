package org.flowerplatform.rapp_manager.arduino_ide.command;

import org.flowerplatform.rapp_manager.arduino_ide.library_manager.LibraryTransferDto;
import org.flowerplatform.tiny_http_server.HttpCommandException;
import org.flowerplatform.tiny_http_server.IHttpCommand;

public class ManageLibrariesCommand implements IHttpCommand{
	
	LibraryTransferDto input;
	//TODO executionFlag: acest flag spune daca este o operatie de analiza + populare sau una de executie.
	boolean executionFlag;
		
	@Override
	public Object run() throws HttpCommandException {
		//TODO ia structura primita (LibraryTransferDto) si analizeaza flagul executionFlag: 
		//TODO daca este setat pe executie, APELEAZA o metoda din libraryManager(applyActions) care itereaza prin fiecare entry si aplica operatia setata.
		//TODO daca nu este setat pe executie, inseamna ca se doreste un refresh. APELEAZA din libraryManager(analizeAndPopulate) care populeaza lista de entries din Dto
		
		//TODO return LibraryTransferDto primit initial si modificat conform actiunilor executate. De discutat daca e nevoie de loguri sau informatii suplimentare.
		return null;
	}

}
