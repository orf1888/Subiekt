package widok.faktura;

import java.sql.SQLException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import model.Faktura;

public class FakturyZakupuPanel
	extends FakturyGeneralPanel
{
	private static final long serialVersionUID = -1171279686468102750L;

	public FakturyZakupuPanel()
		throws SQLException
	{
		super( tworzPopupMagazynWFakturyZakupu(), Faktura.ZAKUP );
	}

	/**
	 * funkcja odpowiedzialna za tworzenie popupu (z PPM na tabeli magazyn)
	 */
	private static JPopupMenu tworzPopupMagazynWFakturyZakupu()
	{
		JPopupMenu result = new JPopupMenu();
		// Guzik dodaj
		JMenuItem itemDodaj = new JMenuItem( "Dodaj", null );
		result.add( itemDodaj );
		itemDodaj.setHorizontalTextPosition( SwingConstants.RIGHT );
		// TODO
		//itemDodaj.addActionListener( edytujListener );
		return result;
	}
}
