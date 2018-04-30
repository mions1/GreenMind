package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import database.Database;
import gui.panel.ClientePanel;
import gui.panel.LoginPanel;

/**
 * Ascoltatore per la finestra del cliente.
 *
 */
public class ClienteListener implements ActionListener {

	ClientePanel source;
	
	public ClienteListener(ClientePanel source) {
		// TODO Auto-generated constructor stub
		this.source = source;
	}
	
	/**
	 * Prelevo l'ordine effettuato e creo il testo di riepilogo
	 * considerando anche lo sconto (che prelevo sempre qui).
	 * Una volta creato il testo chiamo showDialog() della classe ClientePanel
	 * per mostrare questo riepilogo e chiedere se confermare o annullare l'ordine (cosa gestita dal metodo showDialog())
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		int cibo_menu_size = source.getCibo().size();
		int bevande_menu_size = source.getBevande().size();
		int cannabis_menu_size = source.getCannabis().size();
		float totale = 0;
		String testo = "Riepilogo:\n";
		ArrayList<ArrayList<String>> ordine = new ArrayList<ArrayList<String>>();
		ArrayList<Integer> qta = new ArrayList<Integer>();
		//Recupero l'ordine effettuato (prodotti e qta)
		if (e.getSource().equals(source.getOrdina())) {
			
			int sconto = source.getLoginWindow().getDb().getSconto(source.getCf());
			
			//i va da 0 alla totalità dei prodotti presenti nel menu
			for (int i = 0; i < cibo_menu_size+bevande_menu_size+cannabis_menu_size;i++) {
				//Ogni prodotto lo aggiungo in ordine e la relativa qta in qta
				ordine.add(getProdottoFromIndex(i));
				qta.add((int)source.getQta().get(i).getSelectedItem());
			}
			for (int i = 0; i < ordine.size(); i++) 
				if (qta.get(i) != 0) {
					float prezzo = Float.parseFloat(ordine.get(i).get(Database.PRODOTTO_PREZZO-1))*qta.get(i);
					String testo_sconto = sconto > 0 ? "(-"+sconto+"%)" : "";
					testo = testo + 
						ordine.get(i).get(Database.PRODOTTO_NOME-1) +
						"    x" + qta.get(i) + ":    " +
						((prezzo*(100-sconto))/100) + " "+testo_sconto+" €\n";
					totale += Float.parseFloat(ordine.get(i).get(Database.PRODOTTO_PREZZO-1))*qta.get(i);

				}
			totale = ((totale*(100-sconto))/100);
			testo += "\nTotale: "+totale+" €";
			source.showDialog(testo,ordine,qta,totale);
		}
		
		else if (e.getSource().equals(source.getIndietro())) {
			source.getLoginWindow().editPanel(new LoginPanel(source.getLoginWindow()));
		}
	}

	/**
	 * Prelevo il prodotto corrispondente all'indice passato che si trova
	 * nella lista dei prodotti del menu creata in clientepanel
	 * @param indice
	 * @return
	 */
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
