package listeners;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import gui.Login_window;
import gui.panel.CamerierePanel;
import gui.panel.ClientPanel;
import gui.panel.GestorePanel;
import gui.panel.LoginPanel;
import gui.panel.RegisterPanel;

public class LoginListener implements ActionListener, KeyListener, MouseListener {

	Login_window source;

	public LoginListener(Login_window source) {
		// TODO Auto-generated constructor stub
		this.source = source;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		LoginPanel pannello =(LoginPanel)(source.getPannello());
		
		//Click su "registrazione", viene aperto il form
		if (pannello.getNuovoCliente().equals(e.getSource())) {
			source.editPanel(new RegisterPanel(source));
		}
		
		//Viene effettuato il login. Vedi login per info
		else if (pannello.getLogin().equals(e.getSource())) {
			login(pannello);
		}
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	//Se premo "Invio" clicca su login
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode() == e.VK_ENTER) {
			LoginPanel pannello =(LoginPanel)(source.getPannello());
			login(pannello);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Controllo se la persona che vuole fare il login esiste
	 * nel relativo ruolo scelto nella selectbox (cliente/cameriere/gestore)
	 * e nel caso faccio partire la relativa finestra
	 * 
	 * @param pannello
	 */
	private void login(LoginPanel pannello) {
		String tipo = pannello.getTipo().getSelectedItem().toString();
		String cf = pannello.getCodiceFiscale().getText();
		
		switch (tipo) {
			case "Cliente":
				if (cf.equals("io")) //Per far prima
					cf = "MNISMN95R05A341B";
				if (source.getDb().loginCliente(cf)) {
					source.editPanel(new ClientPanel(source,cf));
				}
				else
					System.out.println("NOPE");
				return;
			case "Cameriere": 
				if (cf.equals("cam")) //Per far prima
					cf = "ABCDFG89T23G312T";
				if (source.getDb().loginCameriere(cf)) {
					source.editPanel(new CamerierePanel(source));
				}
				else
					System.out.println("NOPE");
				return;
				
			case "Gestore":
				if (cf.equals("di")) //Per far prima
					cf = "FRRLRD93N48A341E";
				if (source.getDb().loginGestore(cf)) {
					source.editPanel(new GestorePanel(source));
				}
				else
					System.out.println("NOPE");
				return;
		}			
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	
}
