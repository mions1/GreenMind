package gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

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
