package gui.panel;

import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.MainWindow;
import listeners.TavoloListener;

/**
 * Finestra dove vedere gli ordini di quel tavolo.
 * Si possono eliminare e segnare come consegnati i vari ordini.
 *
 */
public class TavoloPanel extends JPanel {

	int tavolo;
	MainWindow lw;
	
	ArrayList<JLabel> ordini;
	JPanel ordini_panel;
	
	JButton indietro;
	JButton elimina;
	JButton consegnato;
	
	ArrayList<JCheckBox> check;
	
	public TavoloPanel(int tavolo, MainWindow lw) {
		this.tavolo = tavolo;
		this.lw = lw;

		indietro = new JButton("Indietro");
		elimina = new JButton("Elimina Ordine");
		consegnato = new JButton("Consegnato");
		
		check = new ArrayList<>();
		
		ordini = new ArrayList<>();
		ordini = createOrdini();
				
		ordini_panel = new JPanel();
		ordini_panel.setLayout(new BoxLayout(ordini_panel, BoxLayout.Y_AXIS));
		
		ordini_panel.add(new JLabel("Tavolo: "+Integer.toString(tavolo)));

		int i = 0;
		for (JLabel lab: ordini) {
			if (lab.getText().contains("Ordine #")) {
				ordini_panel.add(check.get(i++));
			}
			ordini_panel.add(lab);
		}
		
		ordini_panel.add(elimina);
		ordini_panel.add(indietro);
		ordini_panel.add(consegnato);
		
		indietro.addActionListener(new TavoloListener(this));
		consegnato.addActionListener(new TavoloListener(this));

		add(ordini_panel);		
	}
	
	/**
	 * Crea la lista degli ordini del tavolo da mostrare nella finestra
	 * @return ritorna una lista di JLabel in cui ogni oggetto è un ordine
	 */
	private ArrayList<JLabel> createOrdini() {
		ArrayList<ArrayList<String>> ordini;
		ArrayList<JLabel> ordini_label = new ArrayList<>();
		String ordine;
		int num_ordine = -1;
		
		ordini = lw.getDb().getOrdine(tavolo, false);
		
		ordine = "Nessun Ordine";
		int j = 1;
		for (int i = 0; i < ordini.size(); i++) {
			if (num_ordine != Integer.parseInt(ordini.get(i).get(0))) {
				num_ordine = Integer.parseInt(ordini.get(i).get(0));
				JCheckBox checkBox = new JCheckBox("");
				checkBox.setActionCommand(Integer.toString(num_ordine));
				check.add(checkBox);
				ordine = "------------------";
				ordini_label.add(new JLabel(ordine));
				ordine = "Ordine #"+j+":";
				ordini_label.add(new JLabel(ordine));
				j++;
			}
			ordine = ordini.get(i).get(1);	//Nome
			ordine += " x"+ordini.get(i).get(2);//qta
			float prezzo = Float.parseFloat(ordini.get(i).get(4))*Float.parseFloat(ordini.get(i).get(2));
			ordine += " "+((prezzo*(100- Integer.parseInt(ordini.get(i).get(6)) ))/100)+"€";
			ordine += "\n";
			ordini_label.add(new JLabel(ordine));
			System.out.println(ordine);
			ordine = "";
		}
		
		if (ordine.equals("Nessun Ordine")) {
			this.getElimina().setEnabled(false);
			this.getConsegnato().setEnabled(false);
		}
		
		return ordini_label;
	}


	public JButton getIndietro() {
		return indietro;
	}


	public void setIndietro(JButton indietro) {
		this.indietro = indietro;
	}


	public JButton getElimina() {
		return elimina;
	}


	public void setElimina(JButton elimina) {
		this.elimina = elimina;
	}


	public JButton getConsegnato() {
		return consegnato;
	}

	public int getTavolo() {
		return tavolo;
	}
	
	public void setConsegnato(JButton consegnato) {
		this.consegnato = consegnato;
	}
	
	public MainWindow getLoginWindow() {
		return lw;
	}
	
	public ArrayList<JCheckBox> getCheck() {
		return check;
	}
}
