package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.Calendar;

import database.Database;
import gui.Login_window;
import gui.panel.LoginPanel;
import gui.panel.RegisterPanel;

public class RegistrazioneListener implements ActionListener {

	Login_window source;
	
	public RegistrazioneListener(Login_window lw) {
		// TODO Auto-generated constructor stub
		this.source = lw;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		RegisterPanel pannello = (RegisterPanel)(source.getPannello());
		if (e.getSource().equals(pannello.getIndietro())) {
			source.editPanel(new LoginPanel(source));
		}
		
		if (e.getSource().equals(pannello.getRegistrazione())) {
			System.out.println("Eccomi");
			String nome = pannello.getNome().getText();
			String cognome = pannello.getCognome().getText();
			String cf = pannello.getCf().getText();
			int giorno = (int) pannello.getGiorno().getSelectedItem();
			int mese = (int) pannello.getMese().getSelectedItem();
			int anno = (int) pannello.getAnno().getSelectedItem();
			String nazione = pannello.getNazione().getText();
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, anno);
			cal.set(Calendar.MONTH, mese-1);
			cal.set(Calendar.DATE, giorno);
			Database db = source.getDb();
			db.nuovaPersona(cf, nome, cognome, cal, nazione, 0, null, false, true);
		}
	}

}
