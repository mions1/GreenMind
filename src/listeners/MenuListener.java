package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import database.Database;
import gui.panel.ClientPanel;
import gui.panel.LoginPanel;

public class MenuListener implements ActionListener {

	ClientPanel source;
	
	public MenuListener(ClientPanel source) {
		// TODO Auto-generated constructor stub
		this.source = source;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		int cibo_menu_size = source.getCibo().size();
		int bevande_menu_size = source.getBevande().size();
		int cannabis_menu_size = source.getCannabis().size();
		int totale = 0;
		String testo = "Riepilogo:\n";
		ArrayList<ArrayList<String>> ordine = new ArrayList<ArrayList<String>>();
		ArrayList<Integer> qta = new ArrayList<Integer>();
		//Recupero l'ordine effettuato (prodotti e qta)
		if (e.getSource().equals(source.getOrdina())) {
			//i va da 0 alla totalità dei prodotti presenti nel menu
			for (int i = 0; i < cibo_menu_size+bevande_menu_size+cannabis_menu_size;i++) {
				//Ogni prodotto lo aggiungo in ordine e la relativa qta in qta
				ordine.add(getProdottoFromIndex(i));
				qta.add((int)source.getQta().get(i).getSelectedItem());
			}
			for (int i = 0; i < ordine.size(); i++) 
				if (qta.get(i) != 0) {
					testo = testo + 
						ordine.get(i).get(Database.PRODOTTO_NOME-1) +
						"    x" + qta.get(i) + ":    " +
						Float.parseFloat(ordine.get(i).get(Database.PRODOTTO_PREZZO-1))*qta.get(i) + " €\n";
					totale += Float.parseFloat(ordine.get(i).get(Database.PRODOTTO_PREZZO-1))*qta.get(i);
				}
			testo += "\nTotale: "+totale+" €";
			source.showDialog(testo,ordine,qta);
		}
		
		else if (e.getSource().equals(source.getIndietro())) {
			source.getLoginWindow().editPanel(new LoginPanel(source.getLoginWindow()));
		}
	}

	private ArrayList<String> getProdottoFromIndex(int indice) {
		String nome = "";
		//Cannbis
		if (indice >= source.getCibo().size()+source.getBevande().size())
			nome = source.getCannabis().get(indice-(source.getCibo().size()+source.getBevande().size())).getText();
		//Bevande
		else if (indice >= source.getCibo().size())
			nome = source.getBevande().get(indice-(source.getCibo().size())).getText();
		//Cibo
		else
			nome = source.getCibo().get(indice).getText();

		Database db = source.getLoginWindow().getDb();
		
		return db.getProdotto(nome);
	}
	
}
