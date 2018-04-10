package gui.panel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import gui.Login_window;

/**
 * Finestra del cameriere, dove ci sono i bottoni dei tavoli.
 * I tavoli verdi sono quelli con ordini non ancora consegnati
 *
 */
public class CamerierePanel extends JPanel implements ActionListener {

	ArrayList<JButton> tavoli;	//Bottoni dei tavoli (10 di def)
	JPanel tavoli_panel;		//Pannello dei bottoni dei tavoli
	JButton indietro;
	Login_window lw;
	
	public CamerierePanel(Login_window lw) {
		this.lw = lw;
		
		tavoli = createTavoli();
		indietro = new JButton("Indietro");
		
		tavoli_panel = new JPanel();
		tavoli_panel.setLayout(new BoxLayout(tavoli_panel,BoxLayout.Y_AXIS));
		
		//Aggiunta bottoni tavoli al pannello e settaggio actioncommand inserendo il numero del tavolo
		for (int i = 0; i < tavoli.size(); i++) {
			tavoli_panel.add(tavoli.get(i));
			tavoli.get(i).setActionCommand(Integer.toString(i+1));
			tavoli.get(i).addActionListener(this);
		}
		
		add(tavoli_panel);
		add(indietro);
		indietro.addActionListener(this);
	}
	
	/**
	 * Crea l'arraylist dei bottoni dei tavoli
	 * @return
	 */
	private ArrayList<JButton> createTavoli() {
		ArrayList<JButton> tavoli = new ArrayList<>();
		ArrayList<Integer> tavoliAttivi = lw.getDb().getTavoliAttivi();
		
		for (int i = 0; i < 10; i++) {
			JButton tavolo = new JButton("Tavolo "+(i+1));
			tavoli.add(tavolo);
			if ( tavoliAttivi.contains(i+1) )
				tavolo.setBackground(Color.GREEN);
		}
	return tavoli;
	}

	//Se premo indietro torna al login altrimenti avrò premuto su un tavolo e vado nel pannello tavolo
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(indietro)) {
			lw.editPanel(new LoginPanel(lw));
			return;
		}
		
		int tavolo = Integer.parseInt(e.getActionCommand());
		lw.editPanel(new TavoloPanel(tavolo,lw));
	}
	
}
