package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import gui.dialog.DialogGestisciEvento;
import gui.dialog.DialogGestisciPersona;
import gui.dialog.DialogGestisciProdotto;
import gui.panel.GestorePanel;
import gui.panel.LoginPanel;

public class GestoreListener implements ActionListener {

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
					null, new DialogGestisciPersona(source),"Gestisci Prodotto", 
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
					null, new DialogGestisciEvento(source),"Gestisci Prodotto", 
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
			sql = "SELECT persona.cf, sum((presenta.qta*prodotto.prezzo)) as tot "
					+ "from persona,prodotto, presenta, ordina "
					+ "where presenta.cod_prodotto=prodotto.cod_prodotto "
					+ "AND presenta.cod_ordine=ordina.cod_ordine "
					+ "AND ordina.cf=persona.cf "
					+ "GROUP BY persona.cf order by tot desc";
		
		else if (e.getActionCommand().equals(source.PROD_PIU_CHIESTI))
			sql = "SELECT prodotto.nome, presenta.qta "
					+ "FROM prodotto, ordina, presenta "
					+ "WHERE presenta.cod_prodotto=prodotto.cod_prodotto "
					+ "AND presenta.cod_ordine=ordina.cod_ordine "
					+ "ORDER BY presenta.qta desc";
		
		else if (e.getActionCommand().equals(source.PROD_PIU_LUCRO))
			sql = "SELECT prodotto.nome, (presenta.qta*prezzo) as tot "
					+ "FROM prodotto, ordina, presenta "
					+ "WHERE presenta.cod_prodotto=prodotto.cod_prodotto "
					+ "AND presenta.cod_ordine=ordina.cod_ordine "
					+ "ORDER BY tot desc";
		
		else if (e.getActionCommand().equals(source.CLIENTE_PIU_ORDINI))
			sql = "SELECT persona.cf, count(*) as num_ordini "
					+ "FROM persona, ordina "
					+ "WHERE ordina.cf = persona.cf "
					+ "GROUP BY persona.cf "
					+ "ORDER BY num_ordini DESC LIMIT 1";
		
		else if (e.getSource().equals(source.getEsegui()))
			sql = source.getCustomQuery().getText();
		
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
