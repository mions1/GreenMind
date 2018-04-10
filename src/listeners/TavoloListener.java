package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;

import gui.panel.CamerierePanel;
import gui.panel.TavoloPanel;

public class TavoloListener implements ActionListener {

	TavoloPanel source;
	
	public TavoloListener(TavoloPanel source) {
		// TODO Auto-generated constructor stub
		this.source = source;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(source.getIndietro())) {
			source.getLoginWindow().editPanel(new CamerierePanel(source.getLoginWindow()));
		}
		
		else if (e.getSource().equals(source.getConsegnato())) {
			for (JCheckBox check: source.getCheck()) {
				if (check.isSelected())
					source.getLoginWindow().getDb().setConsegnato(true, Integer.parseInt(check.getActionCommand()));
			}
		}
		
		else if (e.getSource().equals(source.getElimina())) {
			for (JCheckBox check: source.getCheck()) {
				if (check.isSelected())
					source.getLoginWindow().getDb().eliminaOrdine(Integer.parseInt(check.getActionCommand()));
			}
		}
	}

}
