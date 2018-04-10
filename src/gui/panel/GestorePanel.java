package gui.panel;

import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import gui.Login_window;
import gui.ModelloTabella;
import gui.TabellaRisultati;
import listeners.GestoreListener;

/**
 * Finestra di gestione, attività:
 * 	-Campo di testo per query al volo
 * 	-Aggiunta/Elimina/Modifica di ogni record nelle tabelle
 * 	-Diverse query per finalità statistiche come:
 * 		v Nazionalità dei clienti
 * 		v Età media dei clienti
 * 		v Spesa totale per cliente
 * 		v Prodotti ordinati in base ai piu chiesti
 * 		v Prodotti ordinati in base ai guadagni piu alti
 * 		v Cliente piu ordinante
 * 		- I 3 eventi presenti nel turno con maggior guadagni
 * 		-...
 * 		-inserisci evento
 * 		-elimina evento
 * 		-
 * 	
 * 
 * @author simone
 *
 */
public class GestorePanel extends JPanel {

	public final String NAZ_PIU_FREQ = "1";
	public final String ETA_MEDIA = "2";
	public final String TOT_PER_CF = "3";
	public final String PROD_PIU_CHIESTI = "4";
	public final String PROD_PIU_LUCRO = "5";
	public final String CLIENTE_PIU_ORDINI = "6";
	
	ArrayList<JButton> query;
	JTextField custom_query;
	
	TabellaRisultati table;
	
	JPanel custom_query_panel;
	JScrollPane scroll_pane;
	
	JButton esegui;
	JButton indietro;
	
	JButton gestisciPersona;
	JButton gestisciProdotto;
	JButton gestisciEvento;
	
	JPanel gestione;
	
	JDialog dialog;
	
	Login_window lw;
	
	public GestorePanel (Login_window lw) {
		query = new ArrayList<JButton>();
		custom_query = new JTextField();
		esegui = new JButton("Esegui");
		indietro = new JButton("Indietro");
		
		gestisciPersona = new JButton("Gestisci Persona");
		gestisciProdotto = new JButton("Gestisci Prodotto");
		gestisciEvento = new JButton("Gestisci Evento");
		
		dialog = new JDialog(new Login_window(lw.getDb()));
		
		this.lw = lw;
		
		custom_query_panel = new JPanel();
		gestione = new JPanel();
		scroll_pane = new JScrollPane();
		
		JButton btn = new JButton("Nazionalità più frequente");
		btn.setToolTipText("Nazionalità dei clienti ordinati in base al loro numero");
		btn.setActionCommand(NAZ_PIU_FREQ);
		query.add(btn);
		
		btn = new JButton("Età media clientela");
		btn.setToolTipText("Età media dei clienti");
		btn.setActionCommand(ETA_MEDIA);
		query.add(btn);
		
		btn = new JButton("Spesa totale per clienti");
		btn.setToolTipText("Spesa totale effettuata da ogni cliente");
		btn.setActionCommand(TOT_PER_CF);
		query.add(btn);
		
		btn = new JButton("Prodotti più richiesti");
		btn.setToolTipText("Prodotti in ordine di quantità ordinata");
		btn.setActionCommand(PROD_PIU_CHIESTI);
		query.add(btn);
		
		btn = new JButton("Prodotti con più guadagno");
		btn.setToolTipText("Prodotti in ordine di quanto si è guadagnato con la loro vendita");
		btn.setActionCommand(PROD_PIU_LUCRO);
		query.add(btn);
		
		btn = new JButton("Cliente con più ordini");
		btn.setToolTipText("Cliente che ha effettutato piu ordini");
		btn.setActionCommand(CLIENTE_PIU_ORDINI);
		query.add(btn);
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		custom_query_panel.setLayout(new BoxLayout(custom_query_panel, BoxLayout.Y_AXIS));
		
		custom_query_panel.add(custom_query);
		custom_query_panel.add(esegui);
		
		gestione.add(gestisciPersona);
		gestione.add(gestisciProdotto);
		gestione.add(gestisciEvento);
		
		this.add(gestione);
		this.add(indietro);
		
		for (JButton bottone: query) {
			this.add(bottone);
			bottone.addActionListener(new GestoreListener(this));
		}
		
		gestisciPersona.addActionListener(new GestoreListener(this));
		gestisciProdotto.addActionListener(new GestoreListener(this));
		gestisciEvento.addActionListener(new GestoreListener(this));
		
		esegui.addActionListener(new GestoreListener(this));
		indietro.addActionListener(new GestoreListener(this));
		table = new TabellaRisultati(new ModelloTabella());
		scroll_pane.setViewportView(table);
		this.add(custom_query_panel);
		this.add(scroll_pane);
	}
	
	public JButton getEsegui() {
		return esegui;
	}
	
	public JButton getGestisciPersona() {
		return gestisciPersona;
	}
	
	public JButton getGestisciProdotto() {
		return gestisciProdotto;
	}
	
	public JButton getGestisciEvento() {
		return gestisciEvento;
	}
	
	public JButton getIndietro() {
		return indietro;
	}
	
	public JTextField getCustomQuery() {
		return custom_query;
	}
	
	public Login_window getLoginWindow() {
		return lw;
	}
	
	public void setTable(ArrayList<String> nomi_colonne, ArrayList<ArrayList<Object>> risultati) {
		table = new TabellaRisultati(new ModelloTabella(nomi_colonne, risultati));
		scroll_pane.getViewport().removeAll();
		scroll_pane.setViewportView(table);
		this.revalidate();
	}
	
}
