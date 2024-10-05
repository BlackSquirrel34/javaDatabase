package de.uniba.wiai.dsg.ajp.assignment2.literature;


import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.MainMenu;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.impl.MainMenuImpl;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model.Database;

public class Main {

	public static Database globalDBscope;

	public static void main(String[] args) {
		// TODO startet eure Anwendung ueber diese main-Methode
		MainMenu main = (MainMenu) new MainMenuImpl();
		main.startReadEvalPrint();
	}

}
