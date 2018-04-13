package gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * Estende AbstractTableModel, il modello della tabella per
 * mostrare i risultati delle query eseguite dal gestore.
 * 
 * Gli header saranno i nomi dei campi dei risultati della query
 * ed i campi saranno i risultati.
 * 
 * @author simone
 *
 */
public class ModelloTabella extends AbstractTableModel {

	int righe;
	int colonne;
	ArrayList<String> nomi_colonne;
	ArrayList<ArrayList<Object>> valori;
	
	public ModelloTabella() {
		righe = 0;
		colonne = 0;
		nomi_colonne = new ArrayList<String>();
		valori = new ArrayList<ArrayList<Object>>();
	}
	
	/**
	 * Costruttore modello tabella
	 * @param nomi_colonne nomi delle colonne dei risultati da mostrare. Con questa list si trova quindi anche il numero di colonne
	 * @param valori I valori da mostrare. Da questo si definisce il numero di righe.
	 */
	public ModelloTabella(ArrayList<String> nomi_colonne, ArrayList<ArrayList<Object>> valori) {
		// TODO Auto-generated constructor stub
		this.righe = valori.size();
		this.colonne = nomi_colonne.size();
		this.nomi_colonne = nomi_colonne;
		this.valori = valori;
	}
	
	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String getColumnName(int arg0) {
		// TODO Auto-generated method stub
		return nomi_colonne.get(arg0);
	}
	
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return colonne;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return righe;
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return valori.get(arg0).get(arg1);
	}

}
