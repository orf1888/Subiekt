package widok.faktura;

import javax.swing.JPopupMenu;

import model.Faktura;

public class FakturyZakupuPanel extends FakturyGeneralPanel
{
	private static final long serialVersionUID = -1171279686468102750L;

	public FakturyZakupuPanel() throws Exception {
		super(tworzPopupMagazynWFakturyZakupu(), Faktura.ZAKUP, true);
	}

	/**
	 * funkcja odpowiedzialna za tworzenie popupu (z PPM na tabeli magazyn)
	 */
	private static JPopupMenu tworzPopupMagazynWFakturyZakupu()
	{
		JPopupMenu result = new JPopupMenu();
		result.setEnabled(false);
		return result;
	}
}