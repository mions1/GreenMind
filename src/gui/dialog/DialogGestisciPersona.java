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
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.prompt.PromptSupport;

import database.Database;
import gui.panel.GestorePanel;

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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(inserisci)) {
			Calendar data = Database.creaData((int)anno.getSelectedItem(), 
					(int)mese.getSelectedItem(), 
					(int)giorno.getSelectedItem());
			float stipendio = !this.stipendio.getText().equals("") ? Float.parseFloat(this.stipendio.getText()) : 0;
			String ruolo = !this.ruolo.getText().equals("") ? this.ruolo.getText() : null;
			
			source.getLoginWindow().getDb().nuovaPersona(cf.getText(), 
					nome.getText(), cognome.getText(), 
					data, nazionalita.getText(), 
					stipendio, ruolo, dipendente.isSelected(), cliente.isSelected());
			creaCfElimina();
		}
		
		else if (e.getSource().equals(elimina)) {
			String cf = cf_elimina.getSelectedItem() != null ? (String)cf_elimina.getSelectedItem() : "";
			source.getLoginWindow().getDb().eliminaPersona(cf);
			creaCfElimina();
		}
		
		else if (e.getSource().equals(dipendente)) {
			stipendio.setEnabled(dipendente.isSelected());
			ruolo.setEnabled(dipendente.isSelected());
		}
	}
	
	public void creaCfElimina() {
		cf_elimina.removeAllItems();
		ArrayList<String> cfs = source.getLoginWindow().getDb().getPersone(true,true);
		for (String cf: cfs) {
			cf_elimina.addItem(cf);
		}
	}
	
}
