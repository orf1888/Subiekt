package widok.wysylka;

import java.sql.SQLException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import kontroler.ObiektWyszukanieWarunki;
import kontroler.WysylkaBaza;
import model.Wysylka;
import utils.MojeUtils;
import widok.WyswietlPDFPanel;
import widok.abstrakt.PanelEdytujDodajObiekt;
import widok.abstrakt.PanelOgolnyParametry;
import widok.abstrakt.PanelOgolnyPrzyciski;

public class WysylkaPanel extends PanelOgolnyPrzyciski
{

	private static final long serialVersionUID = -2449361381185126706L;

	private JPopupMenu tworzMenuPopup()
	{
		JPopupMenu result = new JPopupMenu();
		JMenuItem itemCosTam = new JMenuItem("Edytuj", null);
		result.add(itemCosTam);
		itemCosTam.setHorizontalTextPosition(SwingConstants.RIGHT);
		edytujListener = tworzEdytujListener();
		itemCosTam.addActionListener(edytujListener);

		return result;
	}

	private static JPopupMenu tworzPopupMagazynWysylce()
	{
		JPopupMenu result = new JPopupMenu();
		result.setEnabled(false);
		return result;
	}

	public WysylkaPanel() throws SQLException {
		try
		{
			JPopupMenu popupTabeliMagazynu = tworzPopupMagazynWysylce();
			warunki = new ObiektWyszukanieWarunki(new Wysylka());
			PanelEdytujDodajObiekt panelDwukliku = new WyswietlPDFPanel();

			PanelOgolnyParametry params = new PanelOgolnyParametry(
					tworzModelFuktor(), Wysylka.kolumnyWyswietlane.length - 1,
					tworzMenuPopup(), new WysylkaBaza(),
					new WysylkaPanelEdytujDodaj("Dodaj", true,
							popupTabeliMagazynu), new WysylkaPanelEdytujDodaj(
							"Edytuj", true, popupTabeliMagazynu),
					panelDwukliku, false, Wysylka.kolumnyWyswietlane);
			params.setBounds(1150, 550);
			init(params);
		} catch (Exception e)
		{
			MojeUtils.showPrintError(e);
		}
	}

	public InitModelFunktor tworzModelFuktor()
	{
		return new InitModelFunktor()
		{
			@Override
			public String[][] getBeginningData()
			{
				try
				{
					return obiektBazaManager.pobierzWierszeZBazy(warunki);
				} catch (Exception e)
				{
					MojeUtils.showPrintError(e);
					return null;
				}
			}
		};
	}
}