import java.sql.SQLException;
import java.util.Calendar;

import database.Database;
import gui.MainWindow;
import gui.panel.LoginPanel;
import sun.font.CreatedFontTracker;

/**
 * @author Simone e Mattia
 * 
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

		Database db = null;
		
		String db_name = "GreenMind";
		String user_db_name = "postgres";
		String user_db_psw = "postgres";
		
		boolean reset = false;
		
		System.out.println("Argomenti passati: ");
		for (String s: args)
			System.out.println(s);
		System.out.println("\n\n");
		
		if (args.length == 3) {
			db_name = args[0];
			user_db_name = args[1];
			user_db_psw = args[2];
		}
		
		else if (args.length == 4) {
			if (args[0].equals("reset"))
				reset = true;
			db_name = args[1];
			user_db_name = args[2];
			user_db_psw = args[3];
		}
		
		else if (args.length == 1) {
			if (args[0].equals("reset"))
				reset = true;
		}
		
		else if (args.length > 0)
			System.out.println("Parametri non validi, leggere il file readme.md");
		
		try {
			db = new Database(db_name,user_db_name,user_db_psw);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("\n***************************");
			System.out.println("ERRORE IMPORTANTE: Database oppure credenziali errate. Di default "
					+ "il nome del db è 'GreenMind', l'utente è 'postgres' e la psw è 'postgres'. "
					+ "Se si vogliono cambiare, avviare con 'java -jar greenmind.jar <nome_db> <nome_utente> <psw>' "
					+ "senza omettere nessun campo");
			System.out.println("LEGGERE FILE README.MD");
			System.out.println("***************************");
		}
		
		Calendar turno = Database.getOggi();
		MainWindow lw = new MainWindow(db);
		
		if (reset) {
			System.out.println("Reset db...");
			if (resetDb(db))
				System.out.println("OK!");
		}
		
		System.out.println("Creazione tabelle...");
		if (db.createTable())
			System.out.println("OK!");
		else
			System.out.println("Errore");
		
		System.out.println("Creazione indici...");
		if (db.creaIndici())
			System.out.println("OK!");
		else
			System.out.println("Errore");
		
		System.out.println("Apertura finestra login...");
		lw.setPanel(new LoginPanel(lw));
		lw.setSize(700,500);
		lw.setVisible(true);
		System.out.println("OK!");
		
		}
	
	/**
	 * Resetta il database eliminado tutte le relazioni ed i trigger e ricreandoli
	 * aggiungendo anche dei valori di esempio
	 * @param db Database sul quale agire
	 */
	public static boolean resetDb(Database db) {
		System.out.println("Eliminazione schema...");
		if (db.deleteAll())
			System.out.println("OK!");
		else 
			System.out.println("Errore");
		
		System.out.println("Creazione tabelle...");
		if (db.createTable())
			System.out.println("OK!");
		else
			System.out.println("Errore");
		
		System.out.println("Aggiunta valori di esempio...");
		if (db.valoriEsempio())
			System.out.println("OK!");
		else
			System.out.println("Errore");
		
		System.out.println("Creazione triggers...");
		if (db.creaTriggers())
			System.out.println("OK!");
		else
			System.out.println("Errore");
		
		System.out.println("Creazione indici...");
		if (db.creaIndici())
			System.out.println("OK!");
		else
			System.out.println("Errore");
		
		System.out.println("Aggiunta turno di oggi...");
		if (db.nuovoTurno(Database.getOggi()))
			System.out.println("OK!");
		else
			System.out.println("Errore");
		
		System.out.println("Aggiunta dipendente al turno 1...");
		if (db.aggiungiDipendenteAlTurno("ABCDFG89T23G312T", 1))
			System.out.println("OK!");
		else
			System.out.println("Errore");
		
		System.out.println("Aggiunta evento 1 al turno 1...");
		if (db.aggiungiEventoAlTurno(1, 1))
			System.out.println("OK!");
		else
			System.out.println("Errore");
		
		return true;
	}

}
