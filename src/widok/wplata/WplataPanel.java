package widok.wplata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import kontroler.ObiektWyszukanieWarunki;
import kontroler.SorterKontroler;
import kontroler.WplataBaza;
import model.Sorter;
import model.Wplata;
import utils.DataUtils;
import utils.MojeUtils;
import widok.abstrakt.PanelOgolnyParametry;
import widok.abstrakt.PanelOgolnyPrzyciski;

public class WplataPanel extends PanelOgolnyPrzyciski
{
	private static final long serialVersionUID = -273267642187625639L;

	public WplataPanel(boolean s) {
		super(s);
		try
		{
			warunki = new ObiektWyszukanieWarunki(new Wplata());
			init(new PanelOgolnyParametry(initModelFunktor, tworzMenuPopup(),
					new WplataBaza(),
					new WplataPanelEdytujDodaj("Dodaj", true),
					new WplataPanelEdytujDodaj("Edytuj", true), null/*
																	 * dwulkik
																	 * na
																	 * wierszu
																	 */, false,
					Wplata.opisKolumn), false);
			sorter.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					String okres_sortowania = (String) sorter.getSelectedItem();
					try
					{
						ustawOkres(okres_sortowania);
					} catch (Exception e1)
					{
						MojeUtils
								.showError("Błąd ustawiania okresu wyświetlania!");
						MojeUtils.error(e1);
					}
					String[][] dane = null;
					try
					{
						dane = getObiektBazaManager().pobierzWierszeZBazy(
								warunki);
					} catch (Exception e)
					{
						MojeUtils
								.showError("Błąd ustawiania danych widoku wpłat!");
					}
					try
					{
						przeladujTabele(dane, editableFunktor, false);
					} catch (Exception e)
					{
						MojeUtils.showError("Błąd przeładowania widoku wpłat!");
					}
				}
			});
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
				return ustawOkres(null);
			} catch (Exception e)
			{
				MojeUtils.showPrintError(e);
				return null;
			}
		}
	};

	private JPopupMenu tworzMenuPopup()
	{
		JPopupMenu result = new JPopupMenu();
		JMenuItem itemEdytuj = new JMenuItem("Edytuj", null);
		result.add(itemEdytuj);
		itemEdytuj.setHorizontalTextPosition(SwingConstants.RIGHT);
		edytujListener = tworzEdytujListener();
		itemEdytuj.addActionListener(edytujListener);

		JMenuItem itemUsun = new JMenuItem("Usuń", null);
		result.add(itemUsun);
		itemUsun.setHorizontalTextPosition(SwingConstants.RIGHT);
		usunListener = tworzUsunListener();
		itemUsun.addActionListener(usunListener);
		return result;
	}

	private String[][] ustawOkres(String wejscieSortowania) throws Exception
	{
		Sorter sort = null;
		try
		{
			sort = SorterKontroler.getSorter();
		} catch (Exception e1)
		{
			MojeUtils.error(e1);
		}
		if (wejscieSortowania == null)
			wejscieSortowania = sort.sorterWp;
		String[] daty = null;
		boolean wszystkie = false;
		switch (wejscieSortowania)
		{
		case "Wszystkie":
		{
			wszystkie = true;
			sort.sorterWp = "Wszystkie";
			sorter.setSelectedItem("Wszystkie");
			break;
		}
		case "Bierzący miesiąc":
		{
			daty = DataUtils.getCurrentMonth();
			sorter.setSelectedItem("Bierzący miesiąc");
			sort.sorterWp = "Bierzący miesiąc";
			break;
		}
		case "Bierzący rok":
		{
			daty = DataUtils.getCurrentYear();
			sorter.setSelectedItem("Bierzący rok");
			sort.sorterWp = "Bierzący rok";
			break;
		}
		}
		warunki = new ObiektWyszukanieWarunki(new Wplata());
		if (!wszystkie)
		{
			warunki.dodajWarunekZData(daty[0], daty[1], "data_wplaty");
		}
		SorterKontroler.edytuj(sort);
		return getObiektBazaManager().pobierzWierszeZBazy(warunki);

	}
}
