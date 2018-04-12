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
 * Dialog per aggiungere ed eliminare prodotti
 * Ci sono i campi per inserire gli attributi del prodotto da aggiungere
 * Accanto al pulsante elimina si trova una combobox dei prodotti salvati nel db, selezionane uno per eliminarlo
 *
 */
public class DialogGestisciProdotto extends JPanel implements ActionListener {

	JTextField nome;
	JTextField scheda;
	JTextField tipo;
	JTextField qta;
	JTextField prezzo;
	
	JComboBox<String> prodotti_elimina;
	JComboBox<String> prodotti_aggiungi;
	
	ButtonGroup categoria;
	JRadioButton cannabis;
	JRadioButton cibo;
	JRadioButton bevande;
	
	JButton inserisci;
	JButton elimina;
	JTextField qta_aggiungi;
	JButton aggiungi_qta;
	
	JPanel elimina_panel;
	JPanel prodotto_panel;
	JPanel qta_panel;
	
	GestorePanel source;
	
	public DialogGestisciProdotto(GestorePanel source) {
		
		this.source = source;
		
		nome = new JTextField();
		scheda = new JTextField();
		tipo = new JTextField();
		qta = new JTextField();
		prezzo = new JTextField();
		
		prodotti_elimina = new JComboBox<String>();
		prodotti_aggiungi = new JComboBox<String>();
		
		categoria = new ButtonGroup();
		cannabis = new JRadioButton("Cannabis");
		cannabis.setSelected(true);
		cibo = new JRadioButton("Cibo");
		bevande = new JRadioButton("Bevande");
		
		elimina_panel = new JPanel();
		prodotto_panel = new JPanel();
		qta_panel = new JPanel();
		
		inserisci = new JButton("Inserisci");
		elimina = new JButton("Elimina");
		qta_aggiungi = new JTextField();
		aggiungi_qta = new JButton("Aggiungi");
		
		PromptSupport.setPrompt("Nome", nome);
		PromptSupport.setPrompt("Scheda", scheda);
		PromptSupport.setPrompt("Tipo", tipo);
		PromptSupport.setPrompt("Quantità", qta);
		PromptSupport.setPrompt("Prezzo", prezzo);
		PromptSupport.setPrompt("Quantità", qta_aggiungi);
		
		categoria.add(cannabis);
		categoria.add(cibo);
		categoria.add(bevande);
		
		creaProdottiElimina();
		creaProdottiAggiungi();
		
		elimina_panel.add(prodotti_elimina);
		elimina_panel.add(elimina);
		
		qta_panel.add(prodotti_aggiungi);
		qta_panel.add(qta_aggiungi);
		qta_panel.add(aggiungi_qta);
		
		prodotto_panel.add(cannabis);
		prodotto_panel.add(cibo);
		prodotto_panel.add(bevande);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(nome);
		add(scheda);
		add(tipo);
		add(qta);
		add(prezzo);
		add(prodotto_panel);
		add(inserisci);
		add(elimina_panel);
		add(qta_panel);
		
		inserisci.addActionListener(this);
		elimina.addActionListener(this);
		aggiungi_qta.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(inserisci)) {
			if (source.getLoginWindow().getDb().nuovoProdotto(
					nome.getText(), scheda.getText(), 
					tipo.getText(), Integer.parseInt(qta.getText()), 
					Integer.parseInt(prezzo.getText()), 
					cannabis.isSelected(), cibo.isSelected(), 
					bevande.isSelected())) {
				JOptionPane.showMessageDialog(null, "Prodotto Aggiunto", "Ok", JOptionPane.INFORMATION_MESSAGE);
				reset();
			}
			else
				JOptionPane.showMessageDialog(null, "Errore nell'aggiunta", "Nope", JOptionPane.ERROR_MESSAGE);
			
			creaProdottiElimina();
		}
		
		else if (e.getSource().equals(elimina)) {
			String codice = prodotti_elimina.getSelectedItem().toString();
			int cod = Integer.parseInt(codice.substring(codice.indexOf("(")+1,codice.length()-1));
			
			if (source.getLoginWindow().getDb().eliminaProdotto(cod))
				JOptionPane.showMessageDialog(null, "Prodotto eliminato", "Ok", JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(null, "Errore nell'eliminazione", "Nope", JOptionPane.ERROR_MESSAGE);
			
			creaProdottiElimina();
		}
		
		else if (e.getSource().equals(aggiungi_qta)) {
			String codice = prodotti_aggiungi.getSelectedItem().toString();
			int cod = Integer.parseInt(codice.substring(codice.indexOf("(")+1,codice.length()-1));
			
			if (source.getLoginWindow().getDb().aggiungiQtaProdotto(cod,Integer.parseInt(qta_aggiungi.getText())))
				JOptionPane.showMessageDialog(null, "Quantità aggiunta", "Ok", JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(null, "Errore nell'aggiornamento", "Nope", JOptionPane.ERROR_MESSAGE);
			
			creaProdottiAggiungi();
		}
	}
	
	public void creaProdottiElimina() {
		prodotti_elimina.removeAllItems();
		ArrayList<ArrayList<String>> prodotti = source.getLoginWindow().getDb().getProdotti();
		for (ArrayList<String> prodotto: prodotti) {
			this.prodotti_elimina.addItem( prodotto.get(Database.PRODOTTO_NOME-1) + "(" + prodotto.get(Database.PRODOTTO_COD-1) + ")" );
		}
	}
	
	public void creaProdottiAggiungi() {
		prodotti_aggiungi.removeAllItems();
		ArrayList<ArrayList<String>> prodotti = source.getLoginWindow().getDb().getProdotti();
		for (ArrayList<String> prodotto: prodotti) {
			this.prodotti_aggiungi.addItem( prodotto.get(Database.PRODOTTO_NOME-1) + "(" + prodotto.get(Database.PRODOTTO_COD-1) + ")" );
		}
	}
	
	public void reset() {
		nome.setText("");
		scheda.setText("");
		tipo.setText("");
		prezzo.setText("");
		cannabis.setSelected(true);
		cibo.setSelected(false);
		bevande.setSelected(false);
	}
	
}
