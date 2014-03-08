package widok.kontrachent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import kontroler.KontrachentBaza;
import kontroler.ObiektWyszukanieWarunki;
import model.Kontrachent;
import utils.MojeUtils;
import widok.abstrakt.PanelOgolnyParametry;
import widok.abstrakt.PanelOgolnyPrzyciski;

public class KontrachentPanel extends PanelOgolnyPrzyciski
{
	private static final long serialVersionUID = 13019692222387680L;

	private JPopupMenu tworzMenuPopup()
	{
		JPopupMenu result = new JPopupMenu();
		JMenuItem itemEdytuj = new JMenuItem("Edytuj", null);
		result.add(itemEdytuj);
		itemEdytuj.setHorizontalTextPosition(SwingConstants.RIGHT);
		edytujListener = tworzEdytujListener();
		itemEdytuj.addActionListener(edytujListener);

		JMenuItem itemPokazDlugi = new JMenuItem("Pokaż długi", null);
		result.add(itemPokazDlugi);
		itemPokazDlugi.setHorizontalTextPosition(SwingConstants.RIGHT);
		pokazDlugiListener = tworzDlugiListener();
		itemPokazDlugi.addActionListener(pokazDlugiListener);

		JMenuItem itemUsun = new JMenuItem("Usuń", null);
		result.add(itemUsun);
		itemUsun.setHorizontalTextPosition(SwingConstants.RIGHT);
		usunListener = tworzUsunListener();
		itemUsun.addActionListener(usunListener);

		return result;
	}

	public KontrachentPanel(boolean s) {
		super(s);
		try
		{
			warunki = new ObiektWyszukanieWarunki(new Kontrachent());
			init(new PanelOgolnyParametry(initModelFunktor, tworzMenuPopup(),
					new KontrachentBaza(), new KontrachentPanelEdytujDodaj(
							"Dodaj", true), new KontrachentPanelEdytujDodaj(
							"Edytuj", true), new KontrachentPanelEdytujDodaj(
							"Dane", false), true, Kontrachent.opisKolumn),
					false);
		} catch (Exception e)
		{
			MojeUtils.showPrintError(e);
		}
	}

	private final InitModelFunktor initModelFunktor = new InitModelFunktor()
	{
		@Override
		public String[][] getBeginningData()
		{
			try
			{
				warunki.dodajWarunek(1, "widoczny");
				return getObiektBazaManager().pobierzWierszeZBazy(warunki);
			} catch (Exception e)
			{
				MojeUtils.showPrintError(e);
				return null;
			}
		}
	};
}