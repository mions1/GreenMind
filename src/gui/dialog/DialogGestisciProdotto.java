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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

import org.jdesktop.swingx.prompt.PromptSupport;

import database.Database;
import gui.panel.GestorePanel;

public class DialogGestisciProdotto extends JPanel implements ActionListener {

	JTextField nome;
	JTextField scheda;
	JTextField tipo;
	JTextField prezzo;
	
	JComboBox<String> prodotti;
	
	ButtonGroup categoria;
	JRadioButton cannabis;
	JRadioButton cibo;
	JRadioButton bevande;
	
	JButton inserisci;
	JButton elimina;
	
	JPanel elimina_panel;
	JPanel prodotto_panel;
	
	GestorePanel source;
	
	public DialogGestisciProdotto(GestorePanel source) {
		
		this.source = source;
		
		nome = new JTextField();
		scheda = new JTextField();
		tipo = new JTextField();
		prezzo = new JTextField();
		
		prodotti = new JComboBox<String>();
		
		categoria = new ButtonGroup();
		cannabis = new JRadioButton("Cannabis");
		cannabis.setSelected(true);
		cibo = new JRadioButton("Cibo");
		bevande = new JRadioButton("Bevande");
		
		elimina_panel = new JPanel();
		prodotto_panel = new JPanel();
		
		inserisci = new JButton("Inserisci");
		elimina = new JButton("Elimina");
		
		PromptSupport.setPrompt("Nome", nome);
		PromptSupport.setPrompt("Scheda", scheda);
		PromptSupport.setPrompt("Tipo", tipo);
		PromptSupport.setPrompt("Prezzo", prezzo);
		
		categoria.add(cannabis);
		categoria.add(cibo);
		categoria.add(bevande);
		
		creaProdottiElimina();
		
		elimina_panel.add(prodotti);
		elimina_panel.add(elimina);
		
		prodotto_panel.add(cannabis);
		prodotto_panel.add(cibo);
		prodotto_panel.add(bevande);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(nome);
		add(scheda);
		add(tipo);
		add(prezzo);
		add(prodotto_panel);
		add(inserisci);
		add(elimina_panel);
		
		inserisci.addActionListener(this);
		elimina.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(inserisci)) {
			source.getLoginWindow().getDb().nuovoProdotto(
					nome.getText(), scheda.getText(), 
					tipo.getText(), 0, 
					Integer.parseInt(prezzo.getText()), 
					cannabis.isSelected(), cibo.isSelected(), 
					bevande.isSelected());
			creaProdottiElimina();
		}
		
		else if (e.getSource().equals(elimina)) {
			String codice = prodotti.getSelectedItem().toString();
			int cod = Integer.parseInt(codice.substring(codice.indexOf("(")+1,codice.length()-1));
			
			source.getLoginWindow().getDb().eliminaProdotto(cod);
			creaProdottiElimina();
		}
	}
	
	public void creaProdottiElimina() {
		prodotti.removeAllItems();
		ArrayList<ArrayList<String>> prodotti = source.getLoginWindow().getDb().getProdotti();
		for (ArrayList<String> prodotto: prodotti) {
			this.prodotti.addItem( prodotto.get(Database.PRODOTTO_NOME-1) + "(" + prodotto.get(Database.PRODOTTO_COD-1) + ")" );
		}
	}
	
}
