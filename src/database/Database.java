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

import javax.swing.JOptionPane;


/**
 * La classe si occupa di gesire il database, gestisce la creazione
 * delle tabelle, aggiunta dei record ed eliminazione ed altre utili
 * query per la gestione del locale.
 *
 */
public class Database {
	
	protected Connection c;
	
	//QUANDO SI USANO QUESTE COSTANTI NEGLI ARRAYLIST VANNO USATI CON -1
	
	//Per getProdotto
	public final static int MENU_CIBO = 1;
	public final static int MENU_BEVANDE = 2;
	public final static int MENU_CANNABIS = 3;
	
	//Per getOrdine
	public final static int ORDINE_COD = 1;
	public final static int ORDINE_NOME_PRODOTTO = 2;
	public final static int ORDINE_QTA = 3;
	public final static int ORDINE_SCONTO = 7;
	public final static int ORDINE_TAVOLO = 4;
	public final static int ORDINE_PREZZO_PRODOTTO = 5;
	public final static int ORDINE_CF = 6;
	public final static int ORDINE_TOTALE = 8;
	
	//Per getTurno
	public final static int TURNO_COD = 1;
	public final static int TURNO_DATA = 2;
	public final static int TURNO_EVENTO = 3;
	
	//Per getPersone
	public final static int PERSONA_CF = 1;
	public final static int PERSONA_NOME = 2;
	public final static int PERSONA_COGNOME = 3;
	public final static int PERSONA_DATA = 4;
	public final static int PERSONA_NAZIONALITA = 5;
	public final static int PERSONA_STIPENDIO = 6;
	public final static int PERSONA_RUOLO = 7;
	public final static int PERSONA_DIPENDENTE = 8;
	public final static int PERSONA_CLIENTE = 9;
	
	//Tabella Prodotto
	public final static int PRODOTTO_COD = 1;
	public final static int PRODOTTO_IS_CANNABIS = 2;
	public final static int PRODOTTO_IS_CIBO = 3;
	public final static int PRODOTTO_IS_BEVANDA = 4;
	public final static int PRODOTTO_QTA = 5;	
	public final static int PRODOTTO_NOME = 6;
	public final static int PRODOTTO_SCHEDA = 7;
	public final static int PRODOTTO_TIPO = 8;
	public final static int PRODOTTO_PREZZO = 9;
	
	
	//Tabella Prodotto
	public final static int EVENTO_COD = 1;
	public final static int EVENTO_NOME = 2;
	public final static int EVENTO_TIPO = 3;
	
	/**
	 * Stabilisce la connessione con il database GreenMind in localhost
	 */
	public Database (String nome_db, String nome_account, String psw) {
		c = null;
		
		try {
        Class.forName("org.postgresql.Driver");
        c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+nome_db, 
        		nome_account, psw);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//-----------------GESTIONE DB, SCHEMA, TABELLE, TRIGGERS e VALORI DI ESEMPIO--------------
	
	/**
	 * Elimina lo schema e lo ricrea
	 */
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
	 * @return true se tutto è andato bene, false altrimenti
	 */
	public boolean createTable() {
		
		try {
			Statement stmt = c.createStatement();
			
			String sql;
			
			//Creazione tabella bonus
			sql = "CREATE TABLE IF NOT EXISTS bonus ("
					+ "nome varchar primary key,"
					+ "soglia integer check (soglia >= 0) not null unique,"
					+ "sconto integer check (sconto >= 0) not null)";
			stmt.executeUpdate(sql);
			
			//Creazione tabella Persona
			sql = "CREATE TABLE IF NOT EXISTS persona ("
					+ "cf varchar primary key,"
					+ "nome varchar not null,"
					+ "cognome varchar not null,"
					+ "data_nascita date not null,"
					+ "nazionalita varchar not null,"
					+ "stipendio real check (stipendio >= 0),"
					+ "ruolo varchar check (ruolo in ('cameriere','dirigente', 'barista',null)),"
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
					+ "data date not null unique,"
					+ "cod_evento integer,"
					+ "foreign key(cod_evento) references evento)";
			stmt.executeUpdate(sql);
			//Creazione tabella Lavora
			sql = "CREATE TABLE IF NOT EXISTS lavora ("
					+ "cf varchar not null,"
					+ "cod_turno integer not null, "
					+ "primary key(cod_turno,cf), "
					+ "foreign key(cf) references persona ON DELETE CASCADE,"
					+ "foreign key(cod_turno) references turno ON DELETE CASCADE)";
			stmt.executeUpdate(sql);
			//Creazione tabella ordine
			sql = "CREATE TABLE IF NOT EXISTS ordine ("
					+ "cod_ordine SERIAL primary key,"
					+ "sconto integer not null,"
					+ "tavolo integer not null,"
					+ "cf varchar(16) not null,"
					+ "cod_turno integer,"
					+ "consegnato boolean not null,"
					+ "totale real not null,"
					+ "foreign key(cf) references persona,"
					+ "foreign key(cod_turno) references turno ON DELETE SET NULL)";
			stmt.executeUpdate(sql);
			//Creazione tabella Prodotto
			sql = "CREATE TABLE IF NOT EXISTS prodotto ("
					+ "cod_prodotto SERIAL primary key,"
					+ "s1 boolean,"
					+ "s2 boolean,"
					+ "s3 boolean,"
					+ "qta integer check (qta >= 0),"
					+ "nome varchar not null unique,"
					+ "scheda varchar not null,"
					+ "tipo varchar check (tipo in (null,'vegetariano','vegano')),"
					+ "prezzo real not null check (prezzo > 0))";
			stmt.executeUpdate(sql);
			//Creazione tabella Presenta
			sql = "CREATE TABLE IF NOT EXISTS presenta ("
					+ "cod_prodotto integer not null,"
					+ "cod_ordine integer not null,"
					+ "qta integer not null,"
					+ "primary key(cod_prodotto,cod_ordine),"
					+ "foreign key(cod_prodotto) references prodotto ON DELETE CASCADE,"
					+ "foreign key(cod_ordine) references ordine ON DELETE CASCADE)";
			stmt.executeUpdate(sql);
			
			//CREAZIONI INDICI
			sql = "CREATE INDEX ON persona (cf)";
			stmt.executeUpdate(sql);
			sql = "CREATE INDEX ON prodotto (cod_prodotto)";
			stmt.executeUpdate(sql);
			sql = "CREATE INDEX ON ordine (cod_ordine)";
			stmt.executeUpdate(sql);
		}
		
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Crea Triggers sul db
	 */
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
					+ "SELECT cf into cf_per FROM ordine,presenta where ordine.cod_ordine=presenta.cod_ordine;\n" 
					+ "SELECT sum((presenta.qta*prodotto.prezzo)) into spesa from persona,prodotto,presenta,ordine "  
					+ "WHERE presenta.cod_prodotto=prodotto.cod_prodotto "
					+ "AND presenta.cod_ordine=ordine.cod_ordine " 
					+ "AND ordine.cf=persona.cf AND persona.cf=cf_per;\n"
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
			
			sql = "CREATE OR REPLACE FUNCTION aggiungiDipendenteAlTurno(character varying, integer) RETURNS BOOLEAN AS\n" 
					+ "$$\n "
					+ "DECLARE\n "
					+ "	dip character varying := (select s1 from persona where cf = $1);\n " 
					+ "BEGIN\n " 
					+ "	IF (dip) THEN\n "
					+ "	INSERT INTO lavora values ($1,$2);\n "
					+ " return true;\n " 
					+ "	ELSE\n " 
					+ " return false;\n " 
					+ "	END IF;\n " 
					+ " END;\n " 
					+ "$$\n " 
					+ "LANGUAGE plpgsql;";
			stmt.executeUpdate(sql);
			
			sql = "CREATE OR REPLACE FUNCTION aggiungiPresenta(character varying, integer, integer, integer) "
					+ "RETURNS boolean AS\n" + 
					"$$\n" + 
					"DECLARE\n" + 
					"eta integer := (select date_part('year',age(data_nascita)) from persona where cf=$1);\n" + 
					"iscannabis boolean := (select s1 from prodotto where cod_prodotto=$2);\n" + 
					"\n" + 
					"BEGIN\n" + 
					"IF (eta < 18 AND iscannabis) THEN\n" + 
					"delete from ordine where cod_ordine=$3;\n" + 
					"return false;\n" + 
					"ELSE\n" + 
					"INSERT INTO presenta VALUES ($2,$3,$4);\n" + 
					"UPDATE prodotto SET qta=(SELECT qta FROM prodotto WHERE cod_prodotto=$2)-$4 WHERE cod_prodotto=$2;\n" + 
					"RETURN true;" +
					"END IF;\n" + 
					"END;\n" + 
					"$$\n" + 
					"LANGUAGE plpgsql;";
			stmt.executeUpdate(sql);
			
			sql = "CREATE OR REPLACE FUNCTION ripristinoQta() RETURNS trigger AS\n" + 
					"$$\n" + 
					"	BEGIN\n" + 
					"		UPDATE prodotto SET qta = qta+(SELECT qta from presenta where presenta.cod_ordine = OLD.cod_ordine AND presenta.cod_prodotto = prodotto.cod_prodotto) WHERE prodotto.cod_prodotto in (SELECT presenta.cod_prodotto from presenta where presenta.cod_ordine = OLD.cod_ordine);\n" + 
					"	RETURN OLD;\n" + 
					"	END\n" + 
					"$$\n" + 
					"LANGUAGE plpgsql VOLATILE;";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TRIGGER ripristinaQta BEFORE DELETE ON ordine\n" + 
				  "FOR EACH ROW EXECUTE PROCEDURE ripristinoQta();";
			stmt.executeUpdate(sql);
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Aggiunta valori di esempio
	 * @return true se è andato tutto bene, false altrimenti
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
			
			cal.set(Calendar.YEAR, 2017);
			cal.set(Calendar.MONTH, 5);
			cal.set(Calendar.DATE, 12);
			nuovaPersona("minorenne", "Aldo", "Maldo", 
					cal, "Italia", 0, null, false, true);
			
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

	//-------------------------------------------------------------------------------------------
	
	//----------------------------------AGGIUNTA RECORD NELLE TABELLE----------------------------
	
	/**
	 * Aggiunta di una persona
	 * @param cf codice fiscale
	 * @param nome nome
	 * @param cognome cognome
	 * @param data data di nascità
	 * @param nazione nazionalità
	 * @param stipendio stipendio nel caso sia un dipendente
	 * @param ruolo ruole del dipendente
	 * @param dip true se è un dipendente
	 * @param cli true se è un cliente
	 * @return true se è andato bene, false altrimenti
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
	 * Aggiunta di un ordine.
	 * Ovviamente aggiungera anche i record delle relazioni n-n (presenta)
	 * ed aggiorna la qta dei prodotti ordinati
	 * @param tavolo tavolo dell'ordine 
	 * @param cf codice fiscale dell'ordinante
	 * @param cod_prod prodotti ordinati
	 * @param qta qta dei prodotti ordinati
	 * @param turno turno nel quale si è effettuato l'ordine
	 * @return -1 in caso di errori, 0 se è andato tutto bene, 1 se si è cercato di acquistare cannabis e si è minorenni
	 */
	public int nuovoOrdine(int tavolo,String cf, ArrayList<Integer> cod_prod, ArrayList<Integer> qta, int turno, float totale) {
		
		//Se il turno non esiste
		if (turno == -1) 
			return -1;
		
		int cod_ordine = 0;
		
		//Aggiunta ordine
		try {
			System.out.println("Aggiunta ordine");
			Statement stmt = c.createStatement();
			String sql = "";
			int sconto = getSconto(cf);
			sql = "INSERT INTO ordine (tavolo,cf,cod_turno,consegnato,sconto,totale) VALUES ("
					+ tavolo + ",'"+cf+"', "+turno+", false, "+sconto+", "+totale+") RETURNING cod_ordine";
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
			ResultSet res;
			String sql = "";
			for (int i = 0; i < cod_prod.size(); i++) {
				sql = "SELECT aggiungiPresenta('"+cf+"', "+cod_prod.get(i)+", "
						+ cod_ordine+", "+qta.get(i)+")";
				res = stmt.executeQuery(sql);
				res.next();
				if (!res.getBoolean(1))
					return 1;
			}
				/*
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
				*/
		} catch (SQLException e) {
			// TODO: handle exception
			System.err.println(e.getMessage());
			return -1;
		}
		
		return 0;
	}
	
	/**
	 * Aggiunta di un turno
	 * @param turno data del turno da inserire
	 * @return true se tutto bene, false altrimenti
	 */
	public boolean nuovoTurno(Calendar turno) {
		
		try {
			PreparedStatement prep;
//			ArrayList<ArrayList<String>> turni = getTurni();
//			String data = "";
//			data = Integer.toString(turno.get(Calendar.YEAR));
//			if (turno.get(Calendar.MONTH)+1 < 10)
//				data += "-0"+Integer.toString(turno.get(Calendar.MONTH)+1);
//			else
//				data += "-0"+Integer.toString(turno.get(Calendar.MONTH)+1);
//			data += "-"+Integer.toString(turno.get(Calendar.DAY_OF_MONTH));
//			
//			//Se la data già c'è non aggiunge il turno
//			for (int i=0; i<turni.size(); i++) {
//				if (turni.get(i).get(1).toString().equals(data)) {
//					return false;
//				}
//			}
			
			String sql = "INSERT INTO turno (data, cod_evento) VALUES ("
					+ "?, null)";
			prep = c.prepareStatement(sql);
			prep.setDate(1, new Date(turno.getTime().getTime()));
			prep.executeUpdate();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Aggiunta di un prodotto
	 * @param nome nome prodotto
	 * @param scheda scheda (informazioni di cosa è)
	 * @param tipo tipo (vegetariano etc)
	 * @param qta qta in magazzino del prodotto
	 * @param prezzo prezzo del prodotto
	 * @param cannabis true se il prodotto è cannabis
	 * @param cibo true se il prodotto è cibo
	 * @param bevanda true se il prodotto è bevanda
	 * @return true se tutto è andato bene, false altrimenti
	 */
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
	
	/**
	 * Aggiunta di un evento
	 * @param nome nome dell'evento
	 * @param tipo tipo dell'evento
	 * @return true se è andato bene, false altrimenti
	 */
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
	
	/**
	 * Aggiunta del bonus
	 * @param nome nome del bonus (argento, oro etc)
	 * @param soglia spesa che bisogna effettuare per raggiungere il bonus
	 * @param sconto quanto sconto si ha diritto (10 = 10%)
	 * @return
	 */
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
	
	//------------------------------------RECUPERO RECORDS----------------------------------------

	/**
	 * Seleziona tutte le persone registrate nel db
	 * @return lista delle persone che soddisfano i suddetti predicati
	 */
	public ArrayList<ArrayList<String>> getPersone() {
		ArrayList<String> persona = new ArrayList<String>();
		ArrayList<ArrayList<String>> persone = new ArrayList<ArrayList<String>>();
		try {
			Statement stmt = c.createStatement();
			String sql = "SELECT cf, nome, cognome, data_nascita, nazionalita, stipendio, ruolo, s1, s2 "
					+ "FROM persona";
			ResultSet res = stmt.executeQuery(sql);
			
			while (res.next()) {
				persona.add(res.getString(1));
				persona.add(res.getString(2));
				persona.add(res.getString(3));
				persona.add(res.getDate(4).toString());
				persona.add(res.getString(5));
				persona.add(Float.toString(res.getFloat(6)));
				persona.add(res.getString(7));
				persona.add(Boolean.toString(res.getBoolean(8)));
				persona.add(Boolean.toString(res.getBoolean(9)));
				
				persone.add(persona);
				persona = new ArrayList<String>();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return persone;
	}
	
	/**
	 * Seleziona tutte le persone registrate nel db con la possibilità di scegliere se dipendenti o clienti
	 * @param dipendente true se si vogliono anche i dipendenti
	 * @param cliente true se si vogliono anche i clienti
	 * @return lista delle persone che soddisfano i suddetti predicati
	 */
	public ArrayList<ArrayList<String>> getPersone(boolean dipendente, boolean cliente) {
		ArrayList<String> persona = new ArrayList<String>();
		ArrayList<ArrayList<String>> persone = new ArrayList<ArrayList<String>>();
		try {
			Statement stmt = c.createStatement();
			String sql = "SELECT cf, nome, cognome, data_nascita, nazionalita, stipendio, ruolo, s1, s2 "
					+ "FROM persona WHERE s2="+cliente+" "
					+ "OR s1="+dipendente;
			ResultSet res = stmt.executeQuery(sql);
			
			while (res.next()) {
				persona.add(res.getString(1));
				persona.add(res.getString(2));
				persona.add(res.getString(3));
				persona.add(res.getDate(4).toString());
				persona.add(res.getString(5));
				persona.add(Float.toString(res.getFloat(6)));
				persona.add(res.getString(7));
				persona.add(Boolean.toString(res.getBoolean(8)));
				persona.add(Boolean.toString(res.getBoolean(9)));
				
				persone.add(persona);
				persona = new ArrayList<String>();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return persone;
	}
	
	/**
	 * Seleziona i dipendenti con un certo ruolo
	 * @param ruolo ruolo dei dipendenti da recuperare
	 * @return Lista dei dipendenti col dato ruolo
	 */
	public ArrayList<ArrayList<String>> getPersone(String ruolo) {
		ArrayList<String> persona = new ArrayList<String>();
		ArrayList<ArrayList<String>> persone = new ArrayList<ArrayList<String>>();
		try {
			Statement stmt = c.createStatement();
			String sql = "SELECT cf, nome, cognome, data_nascita, nazionalita, stipendio, ruolo, s1, s2 "
					+ "FROM persona WHERE ruolo='"+ruolo+"'";
			ResultSet res = stmt.executeQuery(sql);
			
			while (res.next()) {
				persona.add(res.getString(1));
				persona.add(res.getString(2));
				persona.add(res.getString(3));
				persona.add(res.getDate(4).toString());
				persona.add(res.getString(5));
				persona.add(Float.toString(res.getFloat(6)));
				persona.add(res.getString(7));
				persona.add(Boolean.toString(res.getBoolean(8)));
				persona.add(Boolean.toString(res.getBoolean(9)));
				
				persone.add(persona);
				persona = new ArrayList<String>();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return persone;
	}
	
	/**
	 * Recupero dei prodotti di un dato tipo
	 * @param tipo tipo dei prodotti da recuperare (MENU_CANNABIS, etc)
	 * @return lista dei prodotti del tipo scelto
	 */
	public ArrayList<ArrayList<String>> getMenu(int tipo) {
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

	/**
	 * Recupero di un prodotto dato il nome
	 * @param nome prodotto da recuperare
	 * @return lista dei campi del prodotto scelto
	 */
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
	
	/**
	 * Recupero dei prodotti nel db
	 * @return lista dei prodotti
	 */
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
	 * Restituisce i turni inseriti
	 * @return lista dei turni nel db
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
	 * Restituisce i turni nel quale un dipendete lavora oppure no
	 * @param lavora true se vogliamo i turni nel quale lavora, false altrimenti
	 * @param cf codice fiscale del dipendente
	 * @return lista dei turni nel db
	 */
	public ArrayList<ArrayList<String>> getTurni(String cf, Boolean lavora) {
		ArrayList<ArrayList<String>> turni = new ArrayList<ArrayList<String>>();
		ArrayList<String> turno = new ArrayList<String>();
		try {
			Statement stmt = c.createStatement();
			String sql = "";
			if (lavora)
				sql = "SELECT turno.cod_turno, turno.data, turno.cod_evento "
						+ "FROM turno, persona, lavora "
						+ "WHERE persona.cf = lavora.cf "
						+ "AND turno.cod_turno = lavora.cod_turno "
						+ "AND persona.cf = '"+cf+"'";
			else
				sql = "SELECT turno.cod_turno, turno.data, turno.cod_evento "
						+ "FROM turno, persona "
						+ "WHERE persona.cf not in "
							+ "(SELECT persona.cf "
							+ "FROM persona, lavora "
							+ "WHERE persona.cf = lavora.cf "
							+ "AND turno.cod_turno = lavora.cod_turno) "
							+ "AND persona.cf='"+cf+"'";
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
	 * @param data data dalla quale partire
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
	 * @param data data richiesta
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
	 * Restituisce gli eventi inseriti nel db
	 * @return lista degli eventi
	 */
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
	
	/**
	 * Restitusce lo sconto di cui ha diritto una persona
	 * @param cf cf della persona di cui prendere lo sconto
	 * @return lo sconto da applicare
	 */
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
	
	/**
	 * Restituisce tutti gli ordini ed i relativi prodotti di un dato tavolo, consegnati oppure no
	 * @param tavolo tavolo
	 * @param consegnato false per gli ordini non consegnati, true per tutti
	 * @return lista degli ordini del tavolo
	 */
	public ArrayList<ArrayList<String>> getOrdine(int tavolo, boolean consegnato) {
		
		ArrayList<ArrayList<String>> ordini = new ArrayList<>();
		ArrayList<String> ordine;
		
		try {
			Statement stmt = c.createStatement();
			
			/*
			System.out.println("Creazione view1...");
			String ordini_tavolo = "CREATE OR REPLACE TEMP VIEW ordini_tavolo AS "
					+ "(SELECT * FROM ordine WHERE "
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
			
			*/
			
			String sql = "SELECT ordine.cod_ordine, prodotto.nome, presenta.qta, tavolo, prezzo, cf, sconto, totale "
					+ "FROM ordine, presenta, prodotto "
					+ "WHERE ordine.cod_ordine = presenta.cod_ordine "
					+ "AND prodotto.cod_prodotto = presenta.cod_prodotto "
					+ "AND ordine.consegnato = false "
					+ "AND tavolo = " + tavolo +" "
					+ "GROUP BY ordine.cod_ordine, tavolo, cf, presenta.qta, nome, prezzo, sconto, totale";
					
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
				ordine.add(Float.toString(res.getFloat(8)));
				ordini.add(ordine);
			}
			System.out.println(ordini);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		
		return ordini;
	}
	
	//------------------------------------ELIMINA RECORD-----------------------------
	
	/**
	 * Elimina un ordine col tale codice
	 * @param cod_ordine cod dell'ordine da eliminare
	 * @return true se è stato eliminato
	 */
	public boolean eliminaOrdine(int cod_ordine) {
		System.out.println("Elimina ordine...");
		try {
			Statement stmt = c.createStatement();
			String sql = "DELETE FROM ordine where cod_ordine="+cod_ordine;
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("OK!");
		return true;
	}
	
	/**
	 * Elimina una persona col tale cf
	 * @param cf cf della persona da eliminare
	 * @return true se è andato tutto bene, false altrimenti
	 */
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
	
	/**
	 * Elimina un prodotto col tale codice
	 * @param cod codice prodotto da eliminare
	 * @return true se è andato tutto bene false altrimenti
	 */
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
	
	/**
	 * Elimina un turno col tale codice
	 * @param cod codice del turno
	 * @return
	 */
	public boolean eliminaTurno(int cod) {
		try {
			Statement stmt = c.createStatement();
			String sql = "DELETE FROM turno WHERE cod_turno="+cod;
			stmt.executeUpdate(sql);
			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Elimina evento col tale codice
	 * @param cod codice evento da eliminare
	 * @return true se è andato tutto bene false altrimenti
	 */
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
	
	//-----------------------------------------ALTRO------------------------------------
	
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
			int cod_turno_oggi = getCod_turnoFromDate(getOggi());
			
			String sql = "SELECT * "
					+ "FROM persona, lavora, turno "
					+ "WHERE s1=true "
					+ "AND persona.cf='"+cf+"' "
					+ "AND ruolo='cameriere' "
					+ "AND persona.cf = lavora.cf "
					+ "AND turno.cod_turno = lavora.cod_turno "
					+ "AND turno.cod_turno = "+cod_turno_oggi;
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
	 * Restituisce il giorno attuale del turno in corso
	 * quindi se sono prima delle 9 a.m. restituisce la data scorsa
	 * (usata per inserire il turno odierno)
	 * @return
	 */
	public static Calendar getOggi() {
		Calendar turno = Calendar.getInstance();
		if (!(turno.get(Calendar.HOUR_OF_DAY) >= 9))
			turno.add(Calendar.DATE, -1);
		return turno;
			 
	}
	
	/**
	 * Crea la data nel formato calendar
	 * @param anno 
	 * @param mese
	 * @param giorno
	 * @return
	 */
	public static Calendar creaData(int anno, int mese, int giorno) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, anno);
		cal.set(Calendar.MONTH, mese-1);
		cal.set(Calendar.DATE, giorno);
		return cal;
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
			String sql = "UPDATE ordine set consegnato="+consegnato+" where cod_ordine="+cod_ordine;
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("OK!");
		return true;
	}
	
	/**
	 * Esegue la query passata
	 * @param query query da eseguire
	 * @return il risultato della query
	 */
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
	
	/**
	 * Restituisce i tavoli che hanno ordini non consegnati
	 * @return tavoli con ordini non consegnati
	 */
	public ArrayList<Integer> getTavoliAttivi() {
		ArrayList<Integer> tavoli = new ArrayList<Integer>();
		
		try {
			ResultSet res;
			Statement stmt = c.createStatement();
			String sql = "SELECT tavolo FROM ordine WHERE consegnato=false";
			res = stmt.executeQuery(sql);
			while (res.next()) 
				tavoli.add(res.getInt(1));
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return tavoli;
	}
	
	/**
	 * Modifica un prodotto impostandone la qta passata
	 * @param cod prodotto da modificare
	 * @param qta quantità da impostare al prodotto
	 * @return 
	 */
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
	
	/**
	 * Aggiunge un evento al turno
	 * @param cod_turno turno al quale aggiungere l'evento
	 * @param cod_evento evento da aggiungere
	 * @return
	 */
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
	
	/**
	 * Inserisce il dipendente nel turno. Sfrutta la funzione definita
	 * nel DB che controlla se il cf passato è di un dipendente
	 * @param cf codice fiscale dipendente
	 * @param cod_turno codice del turno in cui aggiungerlo
	 * @return
	 */
	public boolean aggiungiDipendenteAlTurno(String cf, int cod_turno) {
		try {
			Statement stmt = c.createStatement();
			String sql = "SELECT aggiungiDipendenteAlTurno('"+cf+"', "+cod_turno+")";
			ResultSet res;
			
			res = stmt.executeQuery(sql);
			res.next();
			return res.getBoolean(1);
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
	
	public boolean eliminaDipendenteDalTurno(String cf, int cod_turno) {
		try {
			Statement stmt = c.createStatement();
			String sql = "DELETE FROM lavora "
					+ "WHERE cf='"+cf+"' AND cod_turno="+cod_turno;
			stmt.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
}