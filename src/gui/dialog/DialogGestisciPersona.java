package gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.prompt.PromptSupport;

import database.Database;
import gui.panel.GestorePanel;

/**
 * Dialog per aggiungere ed eliminare Persone
 * Ci sono i campi per inserire gli attributi della persona da aggiungere
 * Accanto al pulsante elimina si trova una combobox delle persone salvate nel db, selezionane una per eliminarla
 *
 * Se si seleziona il checkbox dipendente si abilitano i campi ruolo e stipendio
 *
 */
public class DialogGestisciPersona extends JPanel implements ActionListener {

	JTextField cf;
	JTextField nome;
	JTextField cognome;
	private JComboBox<Integer> giorno;
	private JComboBox<Integer> mese;
	private JComboBox<Integer> anno;
	JTextField nazionalita;
	JTextField stipendio;
	JTextField ruolo;
	JComboBox<String> cf_elimina;
	JCheckBox cliente;
	JCheckBox dipendente;
	
	JButton inserisci;
	JButton elimina;
	
	JPanel data_panel;
	JPanel elimina_panel;
	JPanel dipendente_panel;
	
	GestorePanel source;
	
	public DialogGestisciPersona(GestorePanel source) {
		
		this.source = source;
		
		cf = new JTextField();
		nome = new JTextField();
		cognome = new JTextField();
		giorno = new JComboBox<Integer>();
		for (int i = 1; i <= 31; i++)
			giorno.addItem(i);
		mese = new JComboBox<Integer>();
		for (int i = 1; i <= 12; i++)
			mese.addItem(i);
		anno = new JComboBox<Integer>();
		for (int i = 1930; i <= 2018; i++)
			anno.addItem(i);
		cf_elimina = new JComboBox<String>();
		nazionalita = new JTextField();
		stipendio = new JTextField("");
		ruolo = new JTextField("");
		cliente = new JCheckBox("Cliente");
		dipendente = new JCheckBox("Dipendente");
	
		data_panel = new JPanel();
		elimina_panel = new JPanel();
		dipendente_panel = new JPanel();
		
		inserisci = new JButton("Inserisci");
		elimina = new JButton("Elimina");
		
		PromptSupport.setPrompt("Codice Fiscale", cf);
		PromptSupport.setPrompt("Nome", nome);
		PromptSupport.setPrompt("Cognome", cognome);
		PromptSupport.setPrompt("Nazionalità", nazionalita);
		PromptSupport.setPrompt("Stipendio", stipendio);
		PromptSupport.setPrompt("Ruolo", ruolo);
		
		stipendio.setEnabled(false);
		ruolo.setEnabled(false);
		
		creaCfElimina();
		
		data_panel.add(new JLabel("Data di Nascita: "));
		data_panel.add(giorno);
		data_panel.add(mese);
		data_panel.add(anno);
		
		elimina_panel.add(cf_elimina);
		elimina_panel.add(elimina);
		
		dipendente_panel.add(dipendente);
		dipendente_panel.add(ruolo);
		dipendente_panel.add(stipendio);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(cf);
		add(nome);
		add(cognome);
		add(nazionalita);
		add(data_panel);
		add(cliente);
		add(dipendente_panel);
		add(inserisci);
		add(elimina_panel);
		
		inserisci.addActionListener(this);
		elimina.addActionListener(this);
		
		dipendente.addActionListener(this);
	}

	/**
	 * Se premo su "Inserisci" inserisce la persona
	 * Se premo su "Elimina" elimina la persona
	 * Se checko su "Dipendente" vengono abilitati i textfield del ruolo e dello stipendio
	 * In ogni caso mostra una finestra di ok o di errore e vengono ricaricate le combobox
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(inserisci)) {
			Calendar data = Database.creaData((int)anno.getSelectedItem(), 
					(int)mese.getSelectedItem(), 
					(int)giorno.getSelectedItem());
			float stipendio = !this.stipendio.getText().equals("") ? Float.parseFloat(this.stipendio.getText()) : 0;
			String ruolo = !this.ruolo.getText().equals("") ? this.ruolo.getText() : null;
			
			if (source.getLoginWindow().getDb().nuovaPersona(cf.getText(), 
					nome.getText(), cognome.getText(), 
					data, nazionalita.getText(), 
					stipendio, ruolo, dipendente.isSelected(), cliente.isSelected())) {
				JOptionPane.showMessageDialog(null, "Persona Aggiunta", "Ok", JOptionPane.INFORMATION_MESSAGE);
				reset();
			}
			else
				JOptionPane.showMessageDialog(null, "Errore nell'aggiunta", "Nope", JOptionPane.ERROR_MESSAGE);
			
			creaCfElimina();
		}
		
		else if (e.getSource().equals(elimina)) {
			String cf = cf_elimina.getSelectedItem() != null ? (String)cf_elimina.getSelectedItem() : "";
			if (source.getLoginWindow().getDb().eliminaPersona(cf))
				JOptionPane.showMessageDialog(null, "Persona eliminata", "Ok", JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(null, "Errore nell'eliminazione", "Nope", JOptionPane.ERROR_MESSAGE);

			creaCfElimina();
		}
		
		else if (e.getSource().equals(dipendente)) {
			stipendio.setEnabled(dipendente.isSelected());
			ruolo.setEnabled(dipendente.isSelected());
		}
	}
	
	/**
	 * Crea combobox delle persone gia salvate nel db
	 */
	public void creaCfElimina() {
		cf_elimina.removeAllItems();
		ArrayList<ArrayList<String>> persone = source.getLoginWindow().getDb().getPersone(true,true);
		for (ArrayList<String> persona: persone) {
			cf_elimina.addItem(persona.get(Database.PERSONA_CF-1));
		}
	}
	
	/**
	 * Reset dei campi dopo l'inserimento
	 */
	public void reset() {
		nome.setText("");
		cognome.setText("");
		cf.setText("");
		nazionalita.setText("");
		stipendio.setText("");
		ruolo.setText("");
		cliente.setSelected(false);
		dipendente.setSelected(false);
		stipendio.setEnabled(false);
		ruolo.setEnabled(false);
		giorno.setSelectedIndex(0);
		mese.setSelectedIndex(0);
		anno.setSelectedIndex(0);
	}
	
}
