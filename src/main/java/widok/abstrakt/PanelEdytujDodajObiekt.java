package widok.abstrakt;

import javax.swing.JDialog;
import javax.swing.JPanel;

import model.ObiektZId;

public abstract class PanelEdytujDodajObiekt extends JPanel
{
	private static final long serialVersionUID = 2635368817417751702L;

	public abstract void uzupelnijFormatke(Object obiekt, JDialog parent);

	public abstract ObiektZId pobierzZFormatki(int id) throws Exception;

	public abstract void czysc();

}
