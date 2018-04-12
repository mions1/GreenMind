package gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.prompt.PromptSupport;

import database.Database;
import gui.panel.GestorePanel;

/**
 * Dialog per aggiungere ed eliminare eventi
 * Ci sono i campi per inserire gli attributi dell'evento da aggiungere
 * Accanto al pulsante elimina si trova una combobox degli eventi salvati nel db, selezionane uno per eliminarlo
 *
 */
public class DialogGestisciEvento extends JPanel implements ActionListener {

	JTextField nome;
	JTextField tipo;
	
	JButton inserisci;
	JButton elimina;
	JButton inserisci_in_turno;
	
	JComboBox<String> eventi;
	JComboBox<String> turni;
	JComboBox<String> eventi_turno;
	
	JPanel elimina_panel;
	JPanel inserisci_in_turno_panel;
	
	GestorePanel source;
	
	public DialogGestisciEvento(GestorePanel source) {
		
		this.source = source;
		
		nome = new JTextField();
		tipo = new JTextField();
		
		eventi = new JComboBox<String>();
		eventi_turno = new JComboBox<String>();
		turni = new JComboBox<String>();
		
		elimina_panel = new JPanel();
		inserisci_in_turno_panel = new JPanel();
		
		inserisci = new JButton("Inserisci");
		elimina = new JButton("Elimina");
		inserisci_in_turno = new JButton("Inserisci nel turno");
		
		PromptSupport.setPrompt("Nome", nome);
		PromptSupport.setPrompt("Tipo", tipo);
		
		creaEventiElimina();
		creaEventiInserisci();
		creaTurni();
		
		elimina_panel.add(eventi);
		elimina_panel.add(elimina);
		
		inserisci_in_turno_panel.add(eventi_turno);
		inserisci_in_turno_panel.add(turni);
		inserisci_in_turno_panel.add(inserisci_in_turno);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(nome);
		add(tipo);
		add(inserisci);
		add(elimina_panel);
		add(inserisci_in_turno_panel);
		
		inserisci.addActionListener(this);
		elimina.addActionListener(this);
		inserisci_in_turno.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(inserisci)) {
			if (source.getLoginWindow().getDb().nuovoEvento(
					nome.getText(), tipo.getText())) {
				JOptionPane.showMessageDialog(null, "Evento aggiunto", "Ok", JOptionPane.INFORMATION_MESSAGE);
				reset();
			}
			else
				JOptionPane.showMessageDialog(null, "Errore nell'aggiunta", "Nope", JOptionPane.ERROR_MESSAGE);
				
			creaEventiElimina();
		}
		
		else if (e.getSource().equals(elimina)) {
			String codice = eventi.getSelectedItem().toString();
			int cod = Integer.parseInt(codice.substring(codice.indexOf("(")+1,codice.length()-1));
			
			if (source.getLoginWindow().getDb().eliminaEvento(cod)) 
				JOptionPane.showMessageDialog(null, "Evento eliminato", "Ok", JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(null, "Errore nell'eliminazione", "Nope", JOptionPane.ERROR_MESSAGE);

			creaEventiElimina();
		}
		
		else if (e.getSource().equals(inserisci_in_turno)) {
			String codice = turni.getSelectedItem().toString();
			int cod_turno = Integer.parseInt(codice.substring(codice.indexOf("(")+1,codice.length()-1));
			codice = eventi_turno.getSelectedItem().toString();
			int cod_evento = Integer.parseInt(codice.substring(codice.indexOf("(")+1,codice.length()-1));
			
			if (source.getLoginWindow().getDb().aggiungiEventoAlTurno(cod_turno,cod_evento)) 
				JOptionPane.showMessageDialog(null, "Evento aggiunto al turno", "Ok", JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(null, "Errore nell'aggiunta", "Nope", JOptionPane.ERROR_MESSAGE);

			creaTurni();
		}
	}
	
	/**
	 * Crea la combobox degli eventi gia salvati nel db
	 */
	public void creaEventiElimina() {
		eventi.removeAllItems();
		ArrayList<ArrayList<String>> eventi = source.getLoginWindow().getDb().getEventi();
		for (ArrayList<String> evento: eventi) {
			this.eventi.addItem( evento.get(Database.EVENTO_NOME-1) + "(" + evento.get(Database.EVENTO_COD-1) + ")" );
		}
	}
	
	/**
	 * Crea la combobox degli eventi gia salvati nel db
	 */
	public void creaEventiInserisci() {
		eventi_turno.removeAllItems();
		ArrayList<ArrayList<String>> eventi = source.getLoginWindow().getDb().getEventi();
		for (ArrayList<String> evento: eventi) {
			this.eventi_turno.addItem( evento.get(Database.EVENTO_NOME-1) + "(" + evento.get(Database.EVENTO_COD-1) + ")" );
		}
	}
	
	public void creaTurni() {
		this.turni.removeAll();
		ArrayList<ArrayList<String>> turni = source.getLoginWindow().getDb().getTurni(Database.getOggi());
		for (ArrayList<String> turno: turni) 
			this.turni.addItem(turno.get(1) + " ("+turno.get(0)+")");
	}
	
	public void reset() {
		nome.setText("");
		tipo.setText("");
	}
		
}
