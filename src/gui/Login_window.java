package gui;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import database.Database;

public class Login_window extends JFrame{

	//Dichiarazione componenti
	JLabel logo;					//Logo del locale
		String logo_string;			//Scritta logo
	
	public JPanel window;
	JPanel logo_panel;				//Logo
	JPanel pannello;				//Pannello da visualizzare
	
	Database db;
	
	public Login_window (Database db) {
		
		this.db = db;
		window = new JPanel();
		logo_panel = new JPanel();
		
		logo_string = "Big Boy - Coffee & Food Shop";

		logo = new JLabel(logo_string);
			logo.setFont(new Font(null, Font.PLAIN, 20));

		window.setLayout(new BoxLayout(window, BoxLayout.Y_AXIS));
	}
	
	public JPanel getPannello() {
		return pannello;
	}
	
	/**
	 * Imposta quale pannello mostrare nella finestra.
	 * Qualunque pannello ha come header il logo
	 * @param pannello
	 */
	public void setPanel(JPanel pannello) {
		logo_panel.add(logo);
		window.add(logo_panel);
		this.pannello = pannello;
		window.add(pannello);
		add(window);
		revalidate();
	}
	
	/**
	 * Cambio pannello da mostrare
	 * @param pannello
	 */
	public void editPanel(JPanel pannello) {
		window.removeAll();
		setPanel(pannello);
	}
	
	public Database getDb() {
		return db;
	}
}
