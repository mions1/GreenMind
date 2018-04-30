package gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

import org.jdesktop.swingx.prompt.PromptSupport;

import database.Database;
import gui.panel.GestorePanel;

/**
 * Dialog per aggiungere ed eliminare turni
 * Si può:
 * -Aggiungere un turno
 * -Eliminare un turno
 * -Aggiungere un dipendente al turno
 * -Eliminare un dipendente dal turno
 * 
 */
public class DialogGestisciTurno extends JPanel implements ActionListener {

	private JComboBox<Integer> giorno;
	private JComboBox<Integer> mese;
	private JComboBox<Integer> anno;
	private JButton aggiungi_turno;
	
	private JComboBox<String> cf_camerieri;
	private JComboBox<String> turni;
	private JButton aggiungi_in_turno;
	
	private JComboBox<String> cf_camerieri_in_turno;
	private JComboBox<String> turni_cameriere;
	private JButton elimina_cameriere;
	
	JComboBox<String> turni_elimina;
	JButton elimina;
	
	JPanel aggiungi_panel;
	JPanel aggiungi_in_turno_panel;
	JPanel elimina_cameriere_panel;
	JPanel elimina_panel;
	
	GestorePanel source;
	
	public DialogGestisciTurno(GestorePanel source) {
		
		this.source = source;
		
		giorno = new JComboBox<Integer>();
		mese = new JComboBox<Integer>();
		anno = new JComboBox<Integer>();
		aggiungi_turno = new JButton("Aggiungi turno");
		
		cf_camerieri = new JComboBox<String>();
		turni = new JComboBox<String>();
		aggiungi_in_turno = new JButton("Aggiungi turno al cameriere");
		
		cf_camerieri_in_turno = new JComboBox<String>();
		turni_cameriere = new JComboBox<String>();
		elimina_cameriere = new JButton("Elimina cameriere dal turno");
		
		turni_elimina = new JComboBox<String>();
		elimina = new JButton("Elimina turno");
		
		aggiungi_panel = new JPanel();
		aggiungi_in_turno_panel = new JPanel();
		elimina_cameriere_panel = new JPanel();
		elimina_panel = new JPanel();
		
		for (int i = 1; i <= 31; i++)
			giorno.addItem(i);
		for (int i = 1; i <= 12; i++)
			mese.addItem(i);
		for (int i = 2000; i <= 2020; i++)
			anno.addItem(i);
		
		reset();
		
		aggiungi_panel.add(new JLabel("Data: "));
		aggiungi_panel.add(giorno);
		aggiungi_panel.add(mese);
		aggiungi_panel.add(anno);
		aggiungi_panel.add(aggiungi_turno);
		
		aggiungi_in_turno_panel.add(cf_camerieri);
		aggiungi_in_turno_panel.add(turni);
		aggiungi_in_turno_panel.add(aggiungi_in_turno);
		
		elimina_cameriere_panel.add(cf_camerieri_in_turno);
		elimina_cameriere_panel.add(turni_cameriere);
		elimina_cameriere_panel.add(elimina_cameriere);
		
		elimina_panel.add(turni_elimina);
		elimina_panel.add(elimina);
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(aggiungi_panel);
		add(aggiungi_in_turno_panel);
		add(elimina_cameriere_panel);
		add(elimina_panel);
		
		elimina.addActionListener(this);
		elimina_cameriere.addActionListener(this);
		aggiungi_in_turno.addActionListener(this);
		aggiungi_turno.addActionListener(this);
	}

	/**
	 * Se premo su "Inserisci" inserisce il turno (se non esiste)
	 * Se premo su "Elimina" elimina il turno
	 * Se premo su "Aggiungi turno al cameriere" metto quel cameriere nel turno idicato
	 * Se premo su "Elimina cameriere dal turno" elimino quel cameriere da quel turno
	 * 
	 * In ogni caso mostra una finestra di ok o di errore e vengono ricaricate le combobox
	 * e si resettano i campi
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if (e.getSource().equals(aggiungi_turno)) {
			Calendar data = Database.creaData((int)anno.getSelectedItem(), (int)mese.getSelectedItem(), (int)giorno.getSelectedItem());
			if (source.getLoginWindow().getDb().nuovoTurno(data))
				JOptionPane.showMessageDialog(null, "Turno Aggiunto", "Ok", JOptionPane.INFORMATION_MESSAGE);
			else {
				JOptionPane.showMessageDialog(null, "Errore nell'aggiunta\nProbabilmente turno gia esistente", "Nope", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		else if (e.getSource().equals(elimina)) {
			String codice = turni_elimina.getSelectedItem().toString();
			int cod = Integer.parseInt(codice.substring(codice.indexOf("(")+1,codice.length()-1));
			
			if (source.getLoginWindow().getDb().eliminaTurno(cod))
				JOptionPane.showMessageDialog(null, "Turno eliminato", "Ok", JOptionPane.INFORMATION_MESSAGE);
			else {
				JOptionPane.showMessageDialog(null, "Errore nell'eliminazione", "Nope", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		else if (e.getSource().equals(aggiungi_in_turno)) {
			String codice = turni.getSelectedItem().toString();
			int cod_turno = Integer.parseInt(codice.substring(codice.indexOf("(")+1,codice.length()-1));
			String codice_fiscale = cf_camerieri.getSelectedItem().toString();
			
			if (source.getLoginWindow().getDb().aggiungiDipendenteAlTurno(codice_fiscale,cod_turno))
				JOptionPane.showMessageDialog(null, "Cameriere aggiunto nel turno", "Ok", JOptionPane.INFORMATION_MESSAGE);
			else {
				JOptionPane.showMessageDialog(null, "Errore nell'aggiunta nel turno", "Nope", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		else if (e.getSource().equals(elimina_cameriere)) {
			String codice = turni_cameriere.getSelectedItem().toString();
			int cod_turno = Integer.parseInt(codice.substring(codice.indexOf("(")+1,codice.length()-1));
			String codice_fiscale = cf_camerieri_in_turno.getSelectedItem().toString();
			
			if (source.getLoginWindow().getDb().eliminaDipendenteDalTurno(codice_fiscale,cod_turno))
				JOptionPane.showMessageDialog(null, "Cameriere eliminato dal turno", "Ok", JOptionPane.INFORMATION_MESSAGE);
			else {
				JOptionPane.showMessageDialog(null, "Errore nell'eliminazione dal turno", "Nope", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		reset();
	}
	
	/**
	 * Crea la combobox dei camerieri
	 * @param cf combobox da riempire
	 */
	public void creaCf(JComboBox<String> cf) {
		cf.removeAllItems();
		ArrayList<ArrayList<String>> persone = source.getLoginWindow().getDb().getPersone(true, false);
		for (ArrayList<String> persona: persone) {
			if (persona.get(Database.PERSONA_RUOLO-1).equalsIgnoreCase("cameriere"))
				cf.addItem(persona.get(Database.PERSONA_CF-1));
		}
	}
	
	/**
	 * Crea la combobox dei turni
	 * @param cf codice fiscale della persona dei turni (vedi param isPresente)...
	 * @param isPresente se true vogliamo la lista dei turni in cui il dipendente lavora (per elimina_cameriere)
	 * se false vogliamo la lista dei turni dove non lavora (per aggiungi_in_turno)
	 */
	public void creaTurni(String cf, boolean isPresente, JComboBox<String> turni_combo) {
		turni_combo.removeAllItems();
		ArrayList<ArrayList<String>> turni = source.getLoginWindow().getDb().getTurni(cf, isPresente);
		
		for (ArrayList<String> turno: turni)
			turni_combo.addItem(turno.get(Database.TURNO_DATA-1)+" ("+turno.get(Database.TURNO_COD-1)+")");
	}
	
	/**
	 * Riempie la combobox con i prodotti gia inseriti da eliminare
	 */
	public void creaTurniElimina() {
		turni_elimina.removeAllItems();
		ArrayList<ArrayList<String>> turni = source.getLoginWindow().getDb().getTurni();
		for (ArrayList<String> turno: turni) {
			turni_elimina.addItem( turno.get(Database.TURNO_DATA-1) + "(" + turno.get(Database.TURNO_COD-1) + ")" );
		}
	}
	
	/**
	 * Resetta i campi dopo l'inserimento
	 */
	public void reset() {
		creaCf(cf_camerieri);
		creaCf(cf_camerieri_in_turno);
		
		creaTurni(cf_camerieri.getSelectedItem().toString(), false, turni);
		creaTurni(cf_camerieri_in_turno.getSelectedItem().toString(),true,turni_cameriere);

		creaTurniElimina();
	}
	
}
