package gui.panel;

import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import database.Database;
import gui.Login_window;
import listeners.MenuListener;

public class ClientPanel extends JPanel{

	JLabel cibo;
	JLabel bevande;
	JLabel cannabis;
	
	String cf;
	
	JButton ordina;
	JButton indietro;
	
	ArrayList<JLabel> menu_cibo;
	ArrayList<JLabel> menu_bevande;
	ArrayList<JLabel> menu_cannabis;
	
	ArrayList<JComboBox<Integer>> qta;
	
	JPanel menu_panel;
	JPanel cibo_qta_panel;
	JPanel bevande_qta_panel;
	JPanel cannabis_qta_panel;
	JPanel bottoni;
	
	float totale;
	
	Login_window lw;
	
	int tavolo;
	
	public ClientPanel(Login_window lw, String cf) {
		this.lw = lw;
		this.cf = cf;
		
		Random rand = new Random();
		tavolo = rand.nextInt(10)+1;
		
		totale = 0;
		
		Font tipo_menu = new Font(null, Font.BOLD, 25);
		cibo = new JLabel("Cibo");
		bevande = new JLabel("Bevande");
		cannabis = new JLabel("Cannabis");
		
		ordina = new JButton("Ordina");
		indietro = new JButton("Indietro");
		
		cibo.setFont(tipo_menu);
		bevande.setFont(tipo_menu);
		cannabis.setFont(tipo_menu);
		
		menu_panel = new JPanel();
		cibo_qta_panel = new JPanel();
		bevande_qta_panel = new JPanel();
		cannabis_qta_panel = new JPanel();
		bottoni = new JPanel();
		
		menu_cibo = new ArrayList<JLabel>();
		menu_bevande = new ArrayList<JLabel>();
		menu_cannabis = new ArrayList<JLabel>();
		qta = new ArrayList<JComboBox<Integer>>();
		
		createMenu(lw);
		
		menu_panel.setLayout(new BoxLayout(menu_panel, BoxLayout.Y_AXIS));
		cibo_qta_panel.setLayout(new GridLayout(menu_cibo.size(), 2));
		bevande_qta_panel.setLayout(new GridLayout(menu_bevande.size(), 2));
		cannabis_qta_panel.setLayout(new GridLayout(menu_cannabis.size(), 2));
		bottoni.setLayout(new BoxLayout(bottoni, BoxLayout.Y_AXIS));
		
		bottoni.add(indietro);
		bottoni.add(ordina);
		
		menu_panel.add(new JLabel("Tavolo: "+Integer.toString(tavolo)));
		
		menu_panel.add(cibo);
		for (int i = 0; i < menu_cibo.size(); i++) {
			cibo_qta_panel.add(menu_cibo.get(i));
			cibo_qta_panel.add(qta.get(i));
		}
		menu_panel.add(cibo_qta_panel);
		
		menu_panel.add(bevande);
		for (int i = 0; i < menu_bevande.size(); i++) {
			bevande_qta_panel.add(menu_bevande.get(i));
			bevande_qta_panel.add(qta.get(i+menu_cibo.size()));
		}
		menu_panel.add(bevande_qta_panel);
		
		menu_panel.add(cannabis);
		for (int i = 0; i < menu_cannabis.size(); i++) {
			cannabis_qta_panel.add(menu_cannabis.get(i));
			cannabis_qta_panel.add(qta.get(i+menu_cibo.size()+menu_bevande.size()));
		}
		menu_panel.add(cannabis_qta_panel);
		
		menu_panel.add(bottoni);
		
		add(menu_panel);
		
		ordina.addActionListener(new MenuListener(this));
		indietro.addActionListener(new MenuListener(this));
		for (int i = 0; i < qta.size(); i++)
			qta.get(i).addActionListener(new MenuListener(this));		
	}
	
	public void showDialog(String testo, ArrayList<ArrayList<String>> ordine, ArrayList<Integer> qta) {
		int n = JOptionPane.showConfirmDialog(this, testo, "Confermare Ordine?", JOptionPane.YES_NO_OPTION);
		ArrayList<Integer> qta_nuova = new ArrayList<>();
		if (n == JOptionPane.YES_OPTION) {
			ArrayList<Integer> cod_prod = new ArrayList<>();
			
			for (int i=0; i < ordine.size(); i++) {
				if (qta.get(i) != 0) {
					cod_prod.add( Integer.parseInt(ordine.get(i).get(0)));
					qta_nuova.add(qta.get(i));
				}
			}
			lw.getDb().addOrdine(tavolo, cf, cod_prod, qta_nuova, lw.getDb().getCod_turnoFromDate(Database.getOggi()));
		}
			
	}
	
	private void createMenu(Login_window lw) {
		Database db = lw.getDb();
		
		ArrayList<ArrayList<String>> cibo = db.recuperaMenu(db.MENU_CIBO);
		ArrayList<ArrayList<String>> bevande = db.recuperaMenu(db.MENU_BEVANDE);
		ArrayList<ArrayList<String>> cannabis = db.recuperaMenu(db.MENU_CANNABIS);
		
		for (int i = 0; i < cibo.size(); i++) {
			menu_cibo.add(new JLabel(cibo.get(i).get(0)));
			qta.add(createQta( Integer.parseInt(cibo.get(i).get(1))));
		}
		for (int i = 0; i < bevande.size(); i++) {
			menu_bevande.add(new JLabel(bevande.get(i).get(0)));
			qta.add(createQta(Integer.parseInt(bevande.get(i).get(1))));
		}
		for (int i = 0; i < cannabis.size(); i++) {
			menu_cannabis.add(new JLabel(cannabis.get(i).get(0)));
			qta.add(createQta(Integer.parseInt(cannabis.get(i).get(1))));
		}
				
	}
	
	private JComboBox<Integer> createQta(int max) {
		JComboBox<Integer> qta = new JComboBox<Integer>();
		ArrayList<ArrayList<String>> prodotti = lw.getDb().getProdotti();
		
		for (int i=0; i <= max; i++)
			qta.addItem(i);
		return qta;
	}

	public ArrayList<JLabel> getCibo() {
		return menu_cibo;
	}

	public String getCf() {
		return cf;
	}
	
	public ArrayList<JLabel> getBevande() {
		return menu_bevande;
	}

	public ArrayList<JLabel> getCannabis() {
		return menu_cannabis;
	}

	public JButton getOrdina() {
		return ordina;
	}
	
	public JButton getIndietro() {
		return indietro;
	}

	public ArrayList<JComboBox<Integer>> getQta() {
		return qta;
	}

	public void setCibo(JLabel cibo) {
		this.cibo = cibo;
	}

	public void setBevande(JLabel bevande) {
		this.bevande = bevande;
	}

	public void setCannabis(JLabel cannabis) {
		this.cannabis = cannabis;
	}

	public void setOrdina(JButton ordina) {
		this.ordina = ordina;
	}

	public void setQta(ArrayList<JComboBox<Integer>> qta) {
		this.qta = qta;
	}
	
	
	public Login_window getLoginWindow() {
		return lw;
	}
	
}
