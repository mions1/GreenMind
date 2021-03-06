package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import gui.dialog.DialogGestisciEvento;
import gui.dialog.DialogGestisciPersona;
import gui.dialog.DialogGestisciProdotto;
import gui.dialog.DialogGestisciTurno;
import gui.panel.GestorePanel;
import gui.panel.LoginPanel;

/**
 * Listeners della finestra di gestione.
 * A seconda del bottone premuto si esegue l'azione.
 * Se il bottone è una delle query di statistica si esegue la query, si prelevano i risultati e si mostra la tabella.
 * Se il bottone è uno di gestione dei record si apre una dialog per la gestione e gli ascoltatori sono nella classe della dialog corrispondente
 *
 */
public class GestoreListener implements ActionListener, KeyListener {

	GestorePanel source;
	
	public GestoreListener (GestorePanel source) {
		this.source = source;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		String sql = "";
		
		if (e.getSource().equals(source.getGestisciPersona())) {
			JOptionPane.showOptionDialog(
					null, new DialogGestisciPersona(source),"Gestisci Persona", 
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, 
					null, new Object[]{}, null);
		}
		
		else if (e.getSource().equals(source.getGestisciProdotto())) {
			JOptionPane.showOptionDialog(
					null, new DialogGestisciProdotto(source),"Gestisci Prodotto", 
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, 
					null, new Object[]{}, null);
		}
		
		else if (e.getSource().equals(source.getGestisciEvento())) {
			JOptionPane.showOptionDialog(
					null, new DialogGestisciEvento(source),"Gestisci Evento", 
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, 
					null, new Object[]{}, null);
		}
		
		else if (e.getSource().equals(source.getGestisciTurno())) {
			JOptionPane.showOptionDialog(
					null, new DialogGestisciTurno(source),"Gestisci Turno", 
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, 
					null, new Object[]{}, null);
		}
		
		else if (e.getSource().equals(source.getIndietro())) {
			source.getLoginWindow().editPanel(new LoginPanel(source.getLoginWindow()));
			return;
		}

		else if (e.getActionCommand().equals(source.NAZ_PIU_FREQ))
			sql = "SELECT nazionalita, count(*) as num "
					+ "FROM persona GROUP BY nazionalita "
					+ "ORDER BY num DESC";
		
		else if (e.getActionCommand().equals(source.ETA_MEDIA))
			sql = "SELECT AVG(date_part('year', age(data_nascita))) as eta_media "
					+ "FROM persona WHERE s2=true";
		
		else if (e.getActionCommand().equals(source.TOT_PER_CF))
			sql = "SELECT persona.cf, sum(totale) as tot "
					+ "from persona, ordine "
					+ "WHERE ordine.cf=persona.cf "
					+ "GROUP BY persona.cf order by tot desc";
		
		else if (e.getActionCommand().equals(source.PROD_PIU_CHIESTI))
			sql = "SELECT prodotto.nome, presenta.qta "
					+ "FROM prodotto, ordine, presenta "
					+ "WHERE presenta.cod_prodotto=prodotto.cod_prodotto "
					+ "AND presenta.cod_ordine=ordine.cod_ordine "
					+ "ORDER BY presenta.qta desc";
		
		else if (e.getActionCommand().equals(source.PROD_PIU_LUCRO))
			sql = "SELECT prodotto.nome, (presenta.qta*prezzo) as tot "
					+ "FROM prodotto, ordine, presenta "
					+ "WHERE presenta.cod_prodotto=prodotto.cod_prodotto "
					+ "AND presenta.cod_ordine=ordine.cod_ordine "
					+ "ORDER BY tot desc";
		
		else if (e.getActionCommand().equals(source.CLIENTE_PIU_ORDINI))
			sql = "SELECT persona.cf, count(*) as num_ordini "
					+ "FROM persona, ordine "
					+ "WHERE ordine.cf = persona.cf "
					+ "GROUP BY persona.cf "
					+ "ORDER BY num_ordini DESC LIMIT 1";
		
		else if (e.getSource().equals(source.getEsegui()))
			sql = source.getCustomQuery().getText();
		
		eseguiQuery(sql);
		}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode() == e.VK_ENTER) {
			if (source.getCustomQuery().getText().length() > 0)
				eseguiQuery(source.getCustomQuery().getText());
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void eseguiQuery(String sql) {
		if (sql != "") {
			ArrayList<ArrayList<Object>> risultati = new ArrayList<ArrayList<Object>>();
			ArrayList<String> nomi_colonne = new ArrayList<String>();
			ResultSet res = null;
			res = source.getLoginWindow().getDb().eseguiQuery(sql);
			int i = 1;
			try {
				for (int j = 1; j <= res.getMetaData().getColumnCount(); j++)
					nomi_colonne.add(res.getMetaData().getColumnName(j));
					
				while (res.next()) {
					ArrayList<Object> record = new ArrayList<Object>();
					for (int j = 0; j < res.getMetaData().getColumnCount(); j++)
						record.add(res.getObject(j+1));
						
					risultati.add(record);
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			source.setTable(nomi_colonne, risultati);
		}
	}
	
}
