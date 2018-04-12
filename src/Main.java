import java.util.Calendar;

import database.Database;
import gui.Login_window;
import gui.panel.LoginPanel;
import sun.font.CreatedFontTracker;

//Check, lista valori ammessi, etc

/**
 * Gestione del coffee shop GreenMind, interfaccia con il database.
 * 
 * >Nella prima schermata si sceglie se registrarsi (come cliente) oppure se accedere come cliente, cameriere oppure dirigente,
 * 	in ogni caso si richiede un codice fiscale per controllare se l'utente è registrato e se ha i privilegi di accedere con quel ruolo.
 * 
 * >Nella schermata di registrazione si inseriranno i dati che verrano poi salvati nella tabella Persona.
 * >Nella schermata del cliente si potrà effettuare un ordine (inserimento nelle tabelle ordina e presenta ed aggiornamento delle qta residue nella tabella Prodotto) 
 *  scegliendo i prodotti nel menu (quelli presenti nella tabella Prodotto) scegliendo anche la relativa qta.
 * >Nella schermata del cameriere c'è la lista dei tavoli, quelli in verde sono quelli con degli ordini ancora
 *  da consegnare. Una volta cliccato su di un tavolo ci sarà la lista degli ordini attivi e la possibilità di eliminarli
 *  oppure di settarli come "Consegnati" andando ad agire sull'omonimo attributo della tabella Ordina
 * >Nella schermata del gestore ci sono delle query di statistiche utili, un campo per una query personalizzata
 *  e la possibilità di creare ed eliminare persone, evento e prodotti
 *
 * Nel programma ci sono molti system.out di tracing
 */
public class Main {
	
	public static void main(String[] args) {

		Database db = new Database();
		Calendar turno = Database.getOggi();
		Login_window lw = new Login_window(db);
		
		
		//resetDb(db);
		System.out.println("Creazione tabelle...");
		if (db.createTable())
			System.out.println("OK!");
		
		//Aggiunta turno odierno se non c'è
		if (!db.nuovoTurno(turno))
			System.out.println("Turno già esistente");
		
		System.out.println("Apertura finestra login...");
		lw.setPanel(new LoginPanel(lw));
		lw.setSize(500,500);
		lw.setVisible(true);
		System.out.println("OK!");
		
		}
	
	/**
	 * Resetta il database eliminado tutte le relazioni ed i trigger e ricreandoli
	 * aggiungendo anche dei valori di esempio
	 * @param db Database sul quale agire
	 */
	public static void resetDb(Database db) {
		db.deleteAll();
		db.createTable();
		db.valoriEsempio();
		db.creaTriggers();
	}

}
