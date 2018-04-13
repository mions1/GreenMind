package gui.panel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.prompt.*;

import gui.MainWindow;
import listeners.LoginListener;


/**
 * Il primo pannello che viene mostrato. Si tratta della finestra
 * di login, si sceglierà quale figura tenta il login(cliente-cameriere-gestore)
 * e di conseguenza verrà scritto il nuovo pannello.
 * Oppure ci si puo registrare come cliente.
 *
 */
public class LoginPanel extends JPanel{

	//Dichiarazione componenti
		
	JTextField codice_fiscale;		//Campo cf per login cliente
		
	JComboBox<String> tipo_utente;	//Cliente/cameriere/gestore
		String cliente_string;
		String cameriere_string;
		String gestore_string;
		
	JButton nuovo_cliente;			//Registrazione nuovo cliente
	JButton login_button;			//Tasto per accedere
	
	JPanel credenziali_panel;		//Credenziali (textfields)
	JPanel selezione_panel;			//Selezione tipo utente e registrazione
	
	public LoginPanel (MainWindow lw) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//Definizione componenti
		cliente_string = "Cliente";
		cameriere_string = "Cameriere";
		gestore_string = "Gestore";
		
		tipo_utente = new JComboBox<String>();
			tipo_utente.addItem(cliente_string);
			tipo_utente.addItem(cameriere_string);
			tipo_utente.addItem(gestore_string);
			
		nuovo_cliente = new JButton("Sei nuovo? Registrati!");
		login_button = new JButton("Login");
		
		codice_fiscale = new JTextField();
		PromptSupport.setPrompt("Codice Fiscale", codice_fiscale);
		
		credenziali_panel = new JPanel();
		selezione_panel = new JPanel();
		
		credenziali_panel.setLayout (new BoxLayout(credenziali_panel, BoxLayout.Y_AXIS));
		selezione_panel.setLayout (new BoxLayout(selezione_panel, BoxLayout.Y_AXIS));
		
		credenziali_panel.add(codice_fiscale);
		
		selezione_panel.add(tipo_utente);
		selezione_panel.add(login_button);
		selezione_panel.add(nuovo_cliente);
		
		add(credenziali_panel);
		add(selezione_panel);
	
		tipo_utente.addActionListener(new LoginListener(lw));
		nuovo_cliente.addActionListener(new LoginListener(lw));
		login_button.addActionListener(new LoginListener(lw));
		codice_fiscale.addKeyListener(new LoginListener(lw));
		codice_fiscale.addMouseListener(new LoginListener(lw));
	}
	
	public JComboBox<String> getTipo() {
		return tipo_utente;
	}
	
	public JTextField getCodiceFiscale() {
		return codice_fiscale;
	}
	
	public JButton getNuovoCliente() {
		return nuovo_cliente;
	}
	
	public JButton getLogin() {
		return login_button;
	}
}
