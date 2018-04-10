package gui.panel;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.Login_window;
import listeners.RegistrazioneListener;



public class RegisterPanel extends JPanel {

	private JTextField cf;
	private JTextField nome;
	private JTextField cognome;
	private JComboBox<Integer> giorno;
	private JComboBox<Integer> mese;
	private JComboBox<Integer> anno;
	private JTextField nazione;
	
	private JLabel cf_label;
	private JLabel nome_label;
	private JLabel cognome_label;
	private JLabel data_nascita_label;
	private JLabel nazione_label;
	
	private JButton registrazione;
	private JButton indietro;
	
	private JPanel form;
	private JPanel data;
	
	public RegisterPanel(Login_window lw) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
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
		nazione = new JTextField();
		
		indietro = new JButton("Indietro");
		registrazione = new JButton("Registrazione");
		
		cf_label = new JLabel("Codice Fiscale: ");
		nome_label = new JLabel("Nome: ");
		cognome_label = new JLabel("Cognome: ");
		data_nascita_label = new JLabel("Nato il: ");
		nazione_label = new JLabel("Nazione: ");
		
		form = new JPanel();
		form.setLayout(new GridLayout(6, 2));
		
		data = new JPanel();
		data.setLayout(new BoxLayout(data, BoxLayout.X_AXIS));
		
		data.add(giorno);
		data.add(mese);
		data.add(anno);
		
		form.add(cf_label);
		form.add(cf);
		form.add(nome_label);
		form.add(nome);
		form.add(cognome_label);
		form.add(cognome);
		form.add(data_nascita_label);
		form.add(data);
		form.add(nazione_label);
		form.add(nazione);
		form.add(indietro);
		form.add(registrazione);
		
		add(form);
		
		indietro.addActionListener(new RegistrazioneListener(lw));
		registrazione.addActionListener(new RegistrazioneListener(lw));
	}
	
	public JButton getIndietro() {
		return indietro;
	}
	
	public JButton getRegistrazione() {
		return registrazione;
	}

	public JTextField getCf() {
		return cf;
	}

	public JTextField getNome() {
		return nome;
	}

	public JTextField getCognome() {
		return cognome;
	}

	public JComboBox<Integer> getGiorno() {
		return giorno;
	}

	public JComboBox<Integer> getMese() {
		return mese;
	}

	public JComboBox<Integer> getAnno() {
		return anno;
	}

	public JTextField getNazione() {
		return nazione;
	}

	public JPanel getData() {
		return data;
	}
	
}
