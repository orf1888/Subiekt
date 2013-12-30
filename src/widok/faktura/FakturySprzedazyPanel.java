package widok.faktura;

import java.sql.SQLException;

import javax.swing.JPopupMenu;

import model.Faktura;

public class FakturySprzedazyPanel extends FakturyGeneralPanel
{
	private static final long serialVersionUID = -527809025862409241L;

	public FakturySprzedazyPanel() throws SQLException {
		super(tworzPopupMagazynWFakturySprzedazy(), Faktura.SPRZEDAZ);
	}

	/**
	 * funkcja odpowiedzialna za tworzenie popupu (z PPM na tabeli magazyn)
	 * 
	 * jej zawartość to dokładnie how not to do... ale działa
	 */
	private static JPopupMenu tworzPopupMagazynWFakturySprzedazy()
	{
		JPopupMenu result = new JPopupMenu();
		result.setEnabled(false);
		return result;
	}
}
