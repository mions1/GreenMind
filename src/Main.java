import java.util.Calendar;

import database.Database;
import gui.Login_window;
import gui.panel.LoginPanel;

/*
 * Si passa al gestore?
 * Fai le dialog per eliminare ed aggiungere record da gestore
 * 
 */

public class Main {

	public final int LOGIN = 1;
	public final int CLIENTE = 2;
	public final int CAMERIERE = 3;
	public final int GESTORE = 4;
	
	int next_window;
	
	public Main (int next_window) {
		this.next_window = next_window;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Database db = new Database();
		System.out.println("Creazione tabelle...");
		if (db.createTable())
			System.out.println("OK!");
		
		Calendar turno = Database.getOggi();
		
		if (!db.setTurno(turno))
			System.out.println("Turno già esistente");
		//System.out.println("Aggiunta valori esempio...");
		//if (db.valoriEsempio())
		//	System.out.println("OK!");
		System.out.println("Apertura finestra login...");
		Login_window lw = new Login_window(db);
		lw.setPanel(new LoginPanel(lw));
		lw.setSize(500,500);
		lw.setVisible(true);
		System.out.println("OK!");
		}

}
