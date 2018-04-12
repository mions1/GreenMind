import java.util.Calendar;

import database.Database;
import gui.Login_window;
import gui.panel.LoginPanel;
import sun.font.CreatedFontTracker;

/*
 * 
 * Vedi qualche dato derivato e tabella sulla quale fare indice
 *
 */

public class Main {

	/**
	 * Usati per quando si cambia il pannello nella
	 * finestra principale
	 */
	public final int LOGIN = 1;
	public final int CLIENTE = 2;
	public final int CAMERIERE = 3;
	public final int GESTORE = 4;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Database db = new Database();
		
		System.out.println("Creazione tabelle...");
		if (db.createTable())
			System.out.println("OK!");
		
		//resetDb(db);
		
		Calendar turno = Database.getOggi();
		
		if (!db.setTurno(turno))
			System.out.println("Turno già esistente");
		/*
		System.out.println("Aggiunta valori esempio...");
		if (db.valoriEsempio())
			System.out.println("OK!");
		*/
		
		System.out.println("Apertura finestra login...");
		Login_window lw = new Login_window(db);
		lw.setPanel(new LoginPanel(lw));
		lw.setSize(500,500);
		lw.setVisible(true);
		System.out.println("OK!");
		}
	
	public static void resetDb(Database db) {
		db.deleteAll();
		db.createTable();
		db.valoriEsempio();
		db.creaTriggers();
	}

}
