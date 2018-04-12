package database;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;



public class Database {
	
	protected Connection c;
	
	//Usati per la funzione getProdotto per sapere che tipo di prodotto recupeare
	public final static int MENU_CIBO = 1;
	public final static int MENU_BEVANDE = 2;
	public final static int MENU_CANNABIS = 3;
	
	//Ordine
	public final static int ORDINE_COD = 1;
	public final static int ORDINE_SCONTO = 2;
	public final static int ORDINE_TAVOLO = 3;
	public final static int ORDINE_CF = 4;
	public final static int ORDINE_COD_TURNO = 5;
	public final static int ORDINE_CONSEGNATO = 6;
	
	//Usati per sapere a quale indice della tabella si trovi cosa
	public final static int PRODOTTO_COD = 1;
	public final static int PRODOTTO_IS_CANNABIS = 2;
	public final static int PRODOTTO_IS_CIBO = 3;
	public final static int PRODOTTO_IS_BEVANDA = 4;
	public final static int PRODOTTO_QTA = 5;	
	public final static int PRODOTTO_NOME = 6;
	public final static int PRODOTTO_SCHEDA = 7;
	public final static int PRODOTTO_TIPO = 8;
	public final static int PRODOTTO_PREZZO = 9;
	
	
	//EVENTO
	public final static int EVENTO_COD = 1;
	public final static int EVENTO_NOME = 2;
	public final static int EVENTO_TIPO = 3;
	
	/**
	 * Stabilisce la connessione con il database
	 */
	public Database () {
		c = null;
		try {
        Class.forName("org.postgresql.Driver");
        c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/GreenMind", 
        		"postgres", "mions95");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);
		}
		System.out.println("Opened database successfully");
	}
	
	//Elimina lo schema e lo ricrea
	public void deleteAll() {
		try {
			Statement stmt = c.createStatement();
			String sql = "DROP SCHEMA public CASCADE;";
			stmt.executeUpdate(sql);
			sql = "CREATE SCHEMA public;";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Crea le tabelle sul db
	 * @return
	 */
	public boolean createTable() {
		
		try {
			Statement stmt = c.createStatement();
			
			String sql;
			
			//Creazione tabella bonus
			sql = "CREATE TABLE IF NOT EXISTS bonus ("
					+ "nome varchar primary key,"
					+ "soglia integer not null,"
					+ "sconto integer not null)";
			stmt.executeUpdate(sql);
			
			//Creazione tabella Persona
			sql = "CREATE TABLE IF NOT EXISTS persona ("
					+ "cf varchar primary key,"
					+ "nome varchar not null,"
					+ "cognome varchar not null,"
					+ "data_nascita date not null,"
					+ "nazionalita varchar not null,"
					+ "stipendio real,"
					+ "ruolo varchar,"
					+ "s1 boolean,"
					+ "s2 boolean,"
					+ "cod_bonus varchar not null,"
					+ "foreign key (cod_bonus) "
					+ "references bonus(nome) )";
			stmt.executeUpdate(sql);
			
			//Creazione tabella Evento
			sql = "CREATE TABLE IF NOT EXISTS evento ("
					+ "cod_evento SERIAL primary key,"
					+ "nome varchar not null,"
					+ "tipo varchar not null)";
			stmt.executeUpdate(sql);
			//Creazione tabella Turno
			sql = "CREATE TABLE IF NOT EXISTS turno ("
					+ "cod_turno SERIAL primary key,"
					+ "data date not null,"
					+ "cod_evento integer,"
					+ "foreign key(cod_evento) references evento)";
			stmt.executeUpdate(sql);
			//Creazione tabella Lavora
			sql = "CREATE TABLE IF NOT EXISTS lavora ("
					+ "cf varchar not null,"
					+ "cod_turno integer not null, "
					+ "primary key(cod_turno,cf), "
					+ "foreign key(cf) references persona,"
					+ "foreign key(cod_turno) references turno)";
			stmt.executeUpdate(sql);
			//Creazione tabella Ordina
			sql = "CREATE TABLE IF NOT EXISTS ordina ("
					+ "cod_ordine SERIAL primary key,"
					+ "sconto integer not null,"
					+ "tavolo integer not null,"
					+ "cf varchar(16) not null,"
					+ "cod_turno integer not null,"
					+ "consegnato boolean not null,"
					+ "foreign key(cf) references persona,"
					+ "foreign key(cod_turno) references turno)";
			stmt.executeUpdate(sql);
			//Creazione tabella Prodotto
			sql = "CREATE TABLE IF NOT EXISTS prodotto ("
					+ "cod_prodotto SERIAL primary key,"
					+ "s1 boolean,"
					+ "s2 boolean,"
					+ "s3 boolean,"
					+ "qta integer,"
					+ "nome varchar not null,"
					+ "scheda varchar not null,"
					+ "tipo varchar,"
					+ "prezzo real not null)";
			stmt.executeUpdate(sql);
			//Creazione tabella Presenta
			sql = "CREATE TABLE IF NOT EXISTS presenta ("
					+ "cod_prodotto integer not null,"
					+ "cod_ordine integer not null,"
					+ "qta integer not null,"
					+ "primary key(cod_prodotto,cod_ordine),"
					+ "foreign key(cod_prodotto) references prodotto,"
					+ "foreign key(cod_ordine) references ordina)";
			stmt.executeUpdate(sql);
			
		}
		
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	//Crea triggers
	public void creaTriggers() {
		try {
			Statement stmt = c.createStatement();
			//setBonus
			String sql = "CREATE OR REPLACE FUNCTION add_bonus() " 
					+ "RETURNS trigger AS\n"
					+ "$BODY$\n"
					+ "DECLARE\n"
					+ "    nome_bonus varchar;\n"
					+ "    spesa real;\n"
					+ "	   cf_per varchar;\n"
					+ "BEGIN\n"
					+ "SELECT cf into cf_per FROM ordina,presenta where ordina.cod_ordine=presenta.cod_ordine;\n" 
					+ "SELECT sum((presenta.qta*prodotto.prezzo)) into spesa from persona,prodotto,presenta,ordina "  
					+ "WHERE presenta.cod_prodotto=prodotto.cod_prodotto "
					+ "AND presenta.cod_ordine=ordina.cod_ordine " 
					+ "AND ordina.cf=persona.cf AND persona.cf=cf_per;\n"
					+ "SELECT nome INTO nome_bonus FROM bonus "
					+ "WHERE soglia<spesa order by soglia desc limit 1;\n"
					+ "UPDATE persona SET cod_bonus=nome_bonus "
					+ "WHERE persona.cf=cf_per;\n" 
					+ "RETURN null;\n" 
					+ "END;\n"
					+ "$BODY$\n"
					+ "LANGUAGE plpgsql VOLATILE\n";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TRIGGER setBonus\n"
					+ "  AFTER INSERT\n"
					+ "  ON presenta\n" 
					+ "  FOR EACH ROW\n" 
					+ "  EXECUTE PROCEDURE add_bonus();"; 
			stmt.executeUpdate(sql);
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Aggiunge una nuova persona nel db
	 * @param cf
	 * @param nome
	 * @param cognome
	 * @param data
	 * @param nazione
	 * @param stipendio
	 * @param ruolo
	 * @param dip
	 * @param cli
	 * @return
	 */
	public boolean nuovaPersona(String cf, String nome, String cognome, Calendar data, String nazione, float stipendio, String ruolo, boolean dip, boolean cli) {
		try {
			PreparedStatement prep;
			stipendio = !dip ? 0 : stipendio;
			ruolo = !dip ? null : ruolo;
			String sql = "INSERT INTO persona VALUES ("
					+ "'" + cf +"'" + ", "
					+ "'" + nome + "'" + ", "
					+ "'" + cognome + "'" + ", "
					+ "?" + ", "
					+ "'" + nazione + "'" + ", "
					+ stipendio + ", '"
					+ ruolo + "', "
					+ dip +", "
					+ cli + ", 'base')";
			prep = c.prepareStatement(sql);
			prep.setDate(1, new Date(data.getTime().getTime()));
			prep.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	/**
	 * Seleziona tutte le persone registrate nel db
	 * @param dipendente true se si vogliono anche i dipendenti
	 * @param cliente true se si vogliono anche i clienti
	 * @return
	 */
	public ArrayList<String> getPersone(boolean dipendente, boolean cliente) {
		ArrayList<String> cfs = new ArrayList<String>();
		try {
			Statement stmt = c.createStatement();
			String sql = "SELECT cf FROM persona WHERE s2="+cliente+" OR s1="+dipendente;
			ResultSet res = stmt.executeQuery(sql);
			
			while (res.next()) {
				cfs.add(res.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cfs;
	}
	
	/**
	 * Verifica se esiste un cliente con il dato cf
	 * @param cf
	 * @return
	 */
	public boolean loginCliente(String cf) {
		Statement stmt;
		try {
			stmt = c.createStatement();
			String sql = "SELECT * FROM persona WHERE s2=true AND cf='"+cf+"'";
			ResultSet res = stmt.executeQuery(sql);
			if (res.next())
				return true;
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Verifica se esiste un cameriere col dato cf
	 * @param cf
	 * @return
	 */
	public boolean loginCameriere(String cf) {
		Statement stmt;
		try {
			stmt = c.createStatement();
			String sql = "SELECT * FROM persona WHERE s1=true AND cf='"+cf+"' AND ruolo='cameriere'";
			ResultSet res = stmt.executeQuery(sql);
			if (res.next())
				return true;
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Verifica se esiste un gestore col dato cf
	 * @param cf
	 * @return
	 */
	public boolean loginGestore(String cf) {
		Statement stmt;
		try {
			stmt = c.createStatement();
			String sql = "SELECT * FROM persona WHERE s2=true AND cf='"+cf+"'";
			ResultSet res = stmt.executeQuery(sql);
			if (res.next())
				return true;
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Aggiunta valori di esempio
	 * @return
	 */
	public boolean valoriEsempio () {
			Calendar cal = Calendar.getInstance();
			
			//Inserimento bonus
			nuovoBonus("base", 0, 0);
			nuovoBonus("bronzo", 50, 5);
			nuovoBonus("argento", 150, 10);
			nuovoBonus("oro", 250, 15);
			
			//Inserimento 3 clienti
			cal.set(Calendar.YEAR, 1995);
			cal.set(Calendar.MONTH, 10-1);
			cal.set(Calendar.DATE, 5);
			nuovaPersona("MNISMN95R05A341B", "Simone", "Mione", 
					cal, "Italia", 0, null, false, true);

			cal.set(Calendar.YEAR, 1995);
			cal.set(Calendar.MONTH, 9-1);
			cal.set(Calendar.DATE, 5);
			nuovaPersona("MLGMTT92P05F205V", "Mattia", "Malgarini", 
					cal, "Italia", 0, null, false, true);
			
			cal.set(Calendar.YEAR, 1995);
			cal.set(Calendar.MONTH, 5);
			cal.set(Calendar.DATE, 12);
			nuovaPersona("0152637283", "Jihane", "Sarhane", 
					cal, "Marocco", 0, null, false, true);
			
			//Inserimento 3 lavoratori
			cal.set(Calendar.YEAR, 1993);
			cal.set(Calendar.MONTH, 1-1);
			cal.set(Calendar.DATE, 8);
			nuovaPersona("FRRLRD93N48A341E", "Loredana", 
					"Ferrante", cal, 
					"Italia", 3000, "dirigente", 
					true, true);

			cal.set(Calendar.YEAR, 1995);
			cal.set(Calendar.MONTH, 9-1);
			cal.set(Calendar.DATE, 19);
			nuovaPersona("MDCMNL95P59E741T", "Emanuela", "Madeccia",
					cal, "Italia", 1500, "barista", 
					true, true);
			
			cal.set(Calendar.YEAR, 1989);
			cal.set(Calendar.MONTH, 12-1);
			cal.set(Calendar.DATE, 22);
			nuovaPersona("ABCDFG89T23G312T", "Adalberto", 
					"Furlan", cal, "Italia", 2000, 
					"cameriere", true, false);
			
			//Inserimento 3 prodotti (Cannabis)
			nuovoProdotto("Kush", "10 sat 20 ind", null, 20, 7, true, false, false);
			nuovoProdotto("Silver", "20 sat 10 ind", null, 20, 7, true, false, false);
			nuovoProdotto("Bubble Gum", "10 sat 10 ind", null, 3, 11, true, false, false);
			
			//Inserimento 3 prodotti (cibo)
			nuovoProdotto("Hamburger", "Pane, Hamburger, Insalata", null, 7, (float)5.90, false, true, false);
			nuovoProdotto("Margherita", "Pomodoro, Mozzarella", "Vegetariano", 6, (float)6, false, true, false);
			nuovoProdotto("Crepes", "Nutella, panna", "Vegetariano", 3, (float)4, false, true, false);

			//Inserimento 3 prodotti (bevande)
			nuovoProdotto("Acqua", "50cl", null, 12, (float)1.50, false, false, true);
			nuovoProdotto("Birra", "33cl", null, 4, (float)3, false, false, true);
			nuovoProdotto("Coca-Cola", "50cl", null, 18, (float)3, false, false, true);

			//Inserimento 2 eventi
			nuovoEvento("Karaoke con Vanessa", "karaoke");
			nuovoEvento("Gioco a premi", "quiz");
			
		return true;
	}

	public ArrayList<ArrayList<String>> recuperaMenu(int tipo) {
		ArrayList<ArrayList<String>> menu = new ArrayList<ArrayList<String>>();
		try {
			Statement stmt = c.createStatement();
			String sql = "";
			if (tipo == MENU_BEVANDE)
				sql = "SELECT * FROM prodotto WHERE S3=true";
			else if (tipo == MENU_CIBO)
				sql = "SELECT * FROM prodotto WHERE S2=true";
			else if (tipo == MENU_CANNABIS)
				sql = "SELECT * FROM prodotto WHERE S1=true";
			
			ResultSet res = stmt.executeQuery(sql);
			ArrayList<String> prodotto;
			while (res.next()) {
				prodotto = new ArrayList<String>();
				prodotto.add(res.getString(PRODOTTO_NOME));
				prodotto.add( Integer.toString(res.getInt(PRODOTTO_QTA)));
				menu.add(prodotto);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return menu;
	}

	public ArrayList<String>  getProdotto (String nome) {
		ArrayList<String> prodotto = new ArrayList<String>();
		try {
			Statement stmt = c.createStatement();
			String sql = "SELECT * FROM prodotto WHERE nome='"+nome+"'";
			ResultSet res = stmt.executeQuery(sql);
			
			while (res.next()) {
				prodotto.add(Integer.toString(res.getInt(1))); //Codice
				prodotto.add(Boolean.toString(res.getBoolean(2))); //S1 (cannabis)
				prodotto.add(Boolean.toString(res.getBoolean(3))); //S2 (cibo)
				prodotto.add(Boolean.toString(res.getBoolean(4))); //S3 (Bevande)
				prodotto.add(Integer.toString(res.getInt(5))); //qta
				prodotto.add(res.getString(6)); //nome
				prodotto.add(res.getString(7)); //scheda
				prodotto.add(res.getString(8)); //tipo
				prodotto.add(Float.toString(res.getFloat(9))); //prezzo		
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prodotto;
	}
	
	public ArrayList<ArrayList<String>> getProdotti() {
		ArrayList<ArrayList<String>> prodotti = new ArrayList<ArrayList<String>>();
		try {
			Statement stmt = c.createStatement();
			String sql = "SELECT * FROM prodotto";
			ResultSet res = stmt.executeQuery(sql);
			ArrayList<String> prodotto;
			while (res.next()) {
				prodotto = new ArrayList<String>();
				prodotto.add(Integer.toString(res.getInt(1))); //Codice
				prodotto.add(Boolean.toString(res.getBoolean(2))); //S1 (cannabis)
				prodotto.add(Boolean.toString(res.getBoolean(3))); //S2 (cibo)
				prodotto.add(Boolean.toString(res.getBoolean(4))); //S3 (Bevande)
				prodotto.add(Integer.toString(res.getInt(5))); //qta
				prodotto.add(res.getString(6)); //nome
				prodotto.add(res.getString(7)); //scheda
				prodotto.add(res.getString(8)); //tipo
				prodotto.add(Float.toString(res.getFloat(9))); //prezzo
				prodotti.add(prodotto);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prodotti;
	}
	
	/**
	 * Aggiunta di un ordine.
	 * Ovviamente aggiungera anche i record delle relazioni n-n
	 * e rimuove i prodotti dalla qta totale
	 * @param tavolo
	 * @param cf
	 * @param cod_prod
	 * @param qta
	 * @param turno
	 * @return
	 */
	public boolean addOrdine(int tavolo,String cf, ArrayList<Integer> cod_prod, ArrayList<Integer> qta, int turno) {
		
		if (turno == -1) 
			return false;
		
		int cod_ordine = 0;
		
		//Aggiunta ordine
		try {
			System.out.println("Aggiunta ordine");
			Statement stmt = c.createStatement();
			String sql = "";
			int sconto = getSconto(cf);
			sql = "INSERT INTO ordina (tavolo,cf,cod_turno,consegnato,sconto) VALUES ("
					+ tavolo + ",'"+cf+"', "+turno+", false, "+sconto+") RETURNING cod_ordine";
			ResultSet res = stmt.executeQuery(sql);
			res.next();
			cod_ordine = res.getInt(1);
			stmt.close();
		} catch (SQLException e) {
			// TODO: handle exception
			System.err.println(e.getMessage());
		}
		
		//Aggiunta presenta
		try {
			System.out.println("Aggiunta presenta");
			Statement stmt = c.createStatement();
			String sql = "";
			for (int i = 0; i < cod_prod.size(); i++) {
				sql = "INSERT INTO presenta VALUES ("
						+cod_prod.get(i)+", "
								+cod_ordine+", "+qta.get(i)+")";
				stmt.executeUpdate(sql);
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO: handle exception
			System.err.println(e.getMessage());
			return false;
		}
		
		//Rimozione prodotti portati
		try {
			System.out.println("Rimozione prodotti");
			Statement stmt = c.createStatement();
			String sql = "";
			for (int i=0; i<cod_prod.size(); i++) {
				sql = "UPDATE prodotto "
						+ "SET qta=(SELECT qta FROM prodotto WHERE nome="
						+ "(SELECT nome FROM prodotto WHERE cod_prodotto="
						+ cod_prod.get(i)+"))-"+qta.get(i)
						+ " WHERE nome=(SELECT nome FROM prodotto WHERE cod_prodotto="
						+ cod_prod.get(i)+")";
				stmt.executeUpdate(sql);
			}
		} catch (SQLException e) {
			// TODO: handle exception
			System.err.println(e.getMessage());
		}
		
		return true;
	}
	
	/**
	 * Restituisce i turni inseriti
	 * @return
	 */
	public ArrayList<ArrayList<String>> getTurni() {
		ArrayList<ArrayList<String>> turni = new ArrayList<ArrayList<String>>();
		ArrayList<String> turno = new ArrayList<String>();
		try {
			Statement stmt = c.createStatement();
			String sql = "SELECT * FROM turno";
			ResultSet res = stmt.executeQuery(sql);
			while (res.next()) {
				turno = new ArrayList<>();
				turno.add(Integer.toString(res.getInt(1))); //Codice
				turno.add((res.getDate(2).toString())); //Data
				turno.add(Integer.toString(res.getInt(3))); //cod_evento
				turni.add(turno);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return turni;
	}
	
	/**
	 * Restituisce i turni da una certa data in poi
	 * @param data
	 * @return
	 */
	public ArrayList<ArrayList<String>> getTurni(Calendar data) {
		ArrayList<ArrayList<String>> turni = new ArrayList<ArrayList<String>>();
		ArrayList<String> turno = new ArrayList<String>();
		try {
			PreparedStatement prep;
			String sql = "SELECT cod_turno, data FROM turno where data>=?";
			prep = c.prepareStatement(sql);
			prep.setDate(1, new Date(data.getTime().getTime()));
			ResultSet res = prep.executeQuery();
			while (res.next()) {
				turno = new ArrayList<>();
				turno.add(Integer.toString(res.getInt(1))); //Codice
				turno.add((res.getDate(2).toString())); //Data
				turni.add(turno);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return turni;
	}
	
	/**
	 * Restituisce il codice di un turno nella data richiesta
	 * @param data
	 * @return
	 */
	public int getCod_turnoFromDate(Calendar data) {
		try {
			PreparedStatement prep;
			String sql = "SELECT cod_turno FROM turno where data=?";
			prep = c.prepareStatement(sql);
			prep.setDate(1, new Date(data.getTime().getTime()));
			ResultSet res = prep.executeQuery();
			res.next();
			return res.getInt(1);
		} catch (SQLException e) {
			// TODO: handle exception
			System.err.println(e.getMessage());
		}
		return -1;
	}
	
	/**
	 * Aggiunta di un nuovo turno che abbia come data quella richiesta
	 * @param turno
	 * @return
	 */
	public boolean setTurno(Calendar turno) {
		
		try {
			PreparedStatement prep;
			ArrayList<ArrayList<String>> turni = getTurni();
			String data = "";
			data = Integer.toString(turno.get(Calendar.YEAR));
			if (turno.get(Calendar.MONTH)+1 < 10)
				data += "-0"+Integer.toString(turno.get(Calendar.MONTH)+1);
			else
				data += "-0"+Integer.toString(turno.get(Calendar.MONTH)+1);
			data += "-"+Integer.toString(turno.get(Calendar.DAY_OF_MONTH));
			
			for (int i=0; i<turni.size(); i++) {
				if (turni.get(i).get(1).toString().equals(data)) {
					return false;
				}
			}
			
			String sql = "INSERT INTO turno (data, cod_evento) VALUES ("
					+ "?, null)";
			prep = c.prepareStatement(sql);
			prep.setDate(1, new Date(turno.getTime().getTime()));
			prep.executeUpdate();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return true;
		
	}

	/**
	 * Restituisce il giorno attuale (usata per inserire il turno odierno)
	 * @return
	 */
	public static Calendar getOggi() {
		Calendar turno = Calendar.getInstance();
		turno.set(Calendar.YEAR, turno.get(Calendar.YEAR));
		turno.set(Calendar.MONTH, turno.get(Calendar.MONTH));
		turno.set(Calendar.DATE, turno.get(Calendar.DAY_OF_MONTH));
		return turno;
	}
	
	public static Calendar creaData(int anno, int mese, int giorno) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, anno);
		cal.set(Calendar.MONTH, mese-1);
		cal.set(Calendar.DATE, giorno);
		return cal;
	}
	
	/**
	 * Restituisce tutti gli ordini di un dato tavolo, consegnati oppure no
	 * @param tavolo tavolo
	 * @param consegnato false per gli ordini non consegnati, true per tutti
	 */
	public ArrayList<ArrayList<String>> getOrdine(int tavolo, boolean consegnato) {
		
		ArrayList<ArrayList<String>> ordini = new ArrayList<>();
		ArrayList<String> ordine;
		
		try {
			Statement stmt = c.createStatement();
			
			System.out.println("Creazione view1...");
			String ordini_tavolo = "CREATE OR REPLACE TEMP VIEW ordini_tavolo AS "
					+ "(SELECT * FROM ordina WHERE "
					+ "tavolo="+tavolo;
			ordini_tavolo += consegnato ? ")" : " AND consegnato=false)" ;
			stmt.executeUpdate(ordini_tavolo);
			System.out.println("OK!");
			
			System.out.println("Creazione view2");
			String ordini_presenta = "CREATE OR REPLACE TEMP VIEW ordini_presenta AS "
					+ "SELECT presenta.cod_ordine, presenta.cod_prodotto, qta, tavolo, cf, sconto "
					+ "FROM presenta,ordini_tavolo "
					+ "where presenta.cod_ordine in "
					+ "(SELECT cod_ordine from ordini_tavolo group by cod_ordine) "
					+ "AND presenta.cod_ordine = ordini_tavolo.cod_ordine";
			stmt.executeUpdate(ordini_presenta);
			System.out.println("OK!");
			
			System.out.println("Select ordini...");
			String sql = "SELECT cod_ordine, nome, ordini_presenta.qta, tavolo, prezzo, cf, sconto "
					+ "FROM prodotto, ordini_presenta "
					+ "WHERE prodotto.cod_prodotto in "
					+ "(SELECT cod_prodotto FROM ordini_presenta "
					+ "group by cod_prodotto) "
					+ "AND ordini_presenta.cod_prodotto=prodotto.cod_prodotto "
					+ "group by cod_ordine, tavolo, cf, ordini_presenta.qta, nome, prezzo, sconto";
			ResultSet res = stmt.executeQuery(sql);
			System.out.println("OK!");
			while (res.next()) {
				ordine = new ArrayList<>();
				ordine.add(Integer.toString(res.getInt(1)));	//cod_ordine
				ordine.add(res.getString(2));	//nome
				ordine.add(Integer.toString(res.getInt(3)));	//qta
				ordine.add(Integer.toString(res.getInt(4)));	//tavolo
				ordine.add(Float.toString(res.getFloat(5)));	//prezzo
				ordine.add(res.getString(6));	//cf
				ordine.add( Integer.toString(res.getInt(7)) );      //Sconto
				ordini.add(ordine);
			}
			System.out.println(ordini);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		
		return ordini;
	}
	
	/**
	 * Modifica un ordine per impostarlo come consegnato
	 * @param consegnato
	 * @param cod_ordine
	 * @return
	 */
	public boolean setConsegnato(boolean consegnato, int cod_ordine) {
		System.out.println("Set consegnato...");
		try {
			Statement stmt = c.createStatement();
			String sql = "UPDATE ordina set consegnato="+consegnato+" where cod_ordine="+cod_ordine;
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("OK!");
		return true;
	}
	
	public boolean eliminaOrdine(int cod_ordine) {
		System.out.println("Elimina ordine...");
		try {
			Statement stmt = c.createStatement();
			String sql = "DELETE FROM ordina where cod_ordine="+cod_ordine;
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("OK!");
		return true;
	}
	
	public ResultSet eseguiQuery(String query) {
		ResultSet res = null;
		System.out.println("Eseguo query...");
		try {
			Statement stmt = c.createStatement();
			res = stmt.executeQuery(query);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		System.out.println("OK!");
		return res;
	}
	
	public boolean eliminaPersona(String cf) {
		try {
			Statement stmt = c.createStatement();
			String sql = "DELETE FROM PERSONA WHERE cf='"+cf+"'";
			stmt.executeUpdate(sql);
			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean eliminaProdotto(int cod) {
		try {
			Statement stmt = c.createStatement();
			String sql = "DELETE FROM prodotto WHERE cod_prodotto="+cod;
			stmt.executeUpdate(sql);
			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean nuovoProdotto(String nome, String scheda, String tipo, int qta, float prezzo, boolean cannabis, boolean cibo, boolean bevanda) {
		try {
			Statement stmt = c.createStatement();
			String sql = "INSERT INTO prodotto(S1,S2,S3,nome,scheda,tipo,qta,prezzo) "
					+ "VALUES("+cannabis+", "+cibo+", "
							+ bevanda+", '"+nome+"', '"
							+scheda+"', '"+tipo+"', "
							+qta+", "+prezzo+")";
			stmt.executeUpdate(sql);
			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public ArrayList<ArrayList<String>> getEventi() {
		ArrayList<ArrayList<String>> eventi = new ArrayList<ArrayList<String>>();
		ArrayList<String> evento = new ArrayList<String>();
		try {
			Statement stmt = c.createStatement();
			String sql = "SELECT * FROM evento";
			ResultSet res = stmt.executeQuery(sql);
			while (res.next()) {
				evento = new ArrayList<>();
				evento.add(Integer.toString(res.getInt(EVENTO_COD))); //Codice
				evento.add(res.getString(EVENTO_NOME)); //Nome
				evento.add(res.getString(EVENTO_TIPO)); //TIPO
				eventi.add(evento);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return eventi;
	}
	
	public boolean nuovoEvento(String nome, String tipo) {
		try {
			Statement stmt = c.createStatement();
			String sql = "INSERT INTO evento(nome,tipo) "
					+ "VALUES('"+nome+"', '"+tipo+"')";
			stmt.executeUpdate(sql);
			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean eliminaEvento(int cod) {
		try {
			Statement stmt = c.createStatement();
			String sql = "DELETE FROM evento WHERE cod_evento="+cod;
			stmt.executeUpdate(sql);
			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Restituisce i tavoli che hanno ordini non consegnati
	 * @return
	 */
	public ArrayList<Integer> getTavoliAttivi() {
		ArrayList<Integer> tavoli = new ArrayList<Integer>();
		
		try {
			ResultSet res;
			Statement stmt = c.createStatement();
			String sql = "SELECT tavolo FROM ordina WHERE consegnato=false";
			res = stmt.executeQuery(sql);
			while (res.next()) 
				tavoli.add(res.getInt(1));
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return tavoli;
	}
	
	public boolean aggiungiQtaProdotto(int cod, int qta) {
		try {
			Statement stmt = c.createStatement();
			String sql = "UPDATE prodotto set qta="+qta+" WHERE cod_prodotto="+cod;
			stmt.executeUpdate(sql);
			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean nuovoBonus(String nome, int soglia, int sconto) {
		try {
			Statement stmt = c.createStatement();
			String sql = "INSERT INTO bonus values('"+nome+"', "+soglia+", "+sconto+")";
			stmt.executeUpdate(sql);
			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public int getSconto(String cf) {
		int sconto = 0;
		try {
			Statement stmt = c.createStatement();
			String sql = "SELECT sconto FROM bonus,persona WHERE cod_bonus=bonus.nome AND persona.cf='"+cf+"'";
			ResultSet res = stmt.executeQuery(sql);
			res.next();
			sconto = res.getInt(1);
			System.out.println(sconto);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sconto;
	}
	
	public boolean aggiungiEventoAlTurno(int cod_turno, int cod_evento) {
		try {
			Statement stmt = c.createStatement();
			String sql = "UPDATE turno SET cod_evento="+cod_evento+" WHERE cod_turno="+cod_turno;
			
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}