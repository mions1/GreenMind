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
	
	JComboBox<String> eventi;
	
	JPanel elimina_panel;
	
	GestorePanel source;
	
	public DialogGestisciEvento(GestorePanel source) {
		
		this.source = source;
		
		nome = new JTextField();
		tipo = new JTextField();
		
		eventi = new JComboBox<String>();
		
		elimina_panel = new JPanel();
		
		inserisci = new JButton("Inserisci");
		elimina = new JButton("Elimina");
		
		PromptSupport.setPrompt("Nome", nome);
		PromptSupport.setPrompt("Tipo", tipo);
		
		creaEventiElimina();
		
		elimina_panel.add(eventi);
		elimina_panel.add(elimina);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(nome);
		add(tipo);
		add(inserisci);
		add(elimina_panel);
		
		inserisci.addActionListener(this);
		elimina.addActionListener(this);
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
	
	public void reset() {
		nome.setText("");
		tipo.setText("");
	}
		
}
