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

/**
 * Frame principale, dentro questa finistra andremo a disegnare
 * le diverse schermate. 
 * Ogni schermata avrà sempre in alto il logo del locale.
 * Si utilizzerà sempre un riferimento a questa classe per poter
 * recuperare il database e per poter cambiare il pannello con il metodo
 * editPanel()
 *
 */
public class MainWindow extends JFrame{

	//Dichiarazione componenti
	JLabel logo;					//Logo del locale
		String logo_string;			//Scritta logo
	
	public JPanel window;
	JPanel logo_panel;				//Logo
	JPanel pannello;				//Pannello da visualizzare
	
	Database db;
	
	public MainWindow (Database db) {
		
		this.db = db;
		window = new JPanel();
		logo_panel = new JPanel();
		
		logo_string = "Green Mind - Coffee & Food Shop";

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
		System.out.println("Settaggio pannello...");
		logo_panel.add(logo);
		window.add(logo_panel);
		this.pannello = pannello;
		window.add(pannello);
		add(window);
		revalidate();
		System.out.println("OK!");
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
