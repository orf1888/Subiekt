package widok;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import kontroler.ObiektWyszukanieWarunki;
import kontroler.SorterKontroler;
import kontroler.TransportRozliczenieBaza;
import model.Sorter;
import model.TransportRozliczenie;
import utils.DataUtils;
import utils.MojeUtils;
import widok.abstrakt.PanelOgolnyParametry;
import widok.abstrakt.PanelOgolnyPrzyciski;

public class TransportRozliczeniePanel extends PanelOgolnyPrzyciski
{

	private static final long serialVersionUID = 2132580214548407183L;

	public TransportRozliczeniePanel(boolean s) throws Exception {
		super(s);
		try
		{
			// JPopupMenu popupTabeliMagazynu = null;//
			// tworzPopupMagazynWysylce();
			warunki = new ObiektWyszukanieWarunki(null);
			// PanelEdytujDodajObiekt panelDwukliku = new WyswietlPDFPanel();
			PanelOgolnyParametry params = new PanelOgolnyParametry(
					tworzModelFuktor(),
					TransportRozliczenie.kolumnyWyswietlane.length, null/*
																		 * tworzMenuPopup
																		 * ()
																		 */,
					new TransportRozliczenieBaza(), null, null,
					null/* panelDwukliku */, false,
					TransportRozliczenie.kolumnyWyswietlane);

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
						e1.printStackTrace();
					}
					String[][] dane = null;
					try
					{
						dane = obiektBazaManager.pobierzWierszeZBazy(warunki);
					} catch (Exception e)
					{
						MojeUtils
								.showError("Błąd ustawiania danych widoku rozliczenia transportu!");
					}
					try
					{
						przeladujTabele(dane, editableFunktor, false);
					} catch (Exception e)
					{
						MojeUtils
								.showError("Błąd przeładowania widoku rozliczenia transportu!");
					}
				}
			});
			params.setBounds(1150, 550);
			init(params, true);
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
					/* return ustawOkres(null); */
				} catch (Exception e)
				{
					MojeUtils.showPrintError(e);
					return null;
				}
			}
		};
	}

	private String[][] ustawOkres(String wejscieSortowania) throws Exception
	{
		Sorter sort = null;
		try
		{
			sort = SorterKontroler.getSorter();
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
		if (wejscieSortowania == null)
			wejscieSortowania = sort.sorterT;
		String[] daty = null;
		boolean wszystkie = false;
		switch (wejscieSortowania)
		{
		case "Wszystkie":
		{
			wszystkie = true;
			sort.sorterT = "Wszystkie";
			sorter.setSelectedItem("Wszystkie");
			break;
		}
		case "Bierzący miesiąc":
		{
			daty = DataUtils.getCurrentMonth();
			sorter.setSelectedItem("Bierzący miesiąc");
			sort.sorterT = "Bierzący miesiąc";
			break;
		}
		case "Bierzący rok":
		{
			daty = DataUtils.getCurrentYear();
			sorter.setSelectedItem("Bierzący rok");
			sort.sorterT = "Bierzący rok";
			break;
		}
		}
		warunki = new ObiektWyszukanieWarunki(null);
		if (!wszystkie)
		{
			warunki.dodajWarunekZData(daty[0], daty[1], "data_ksiegowania");
		}
		SorterKontroler.edytuj(sort);
		return obiektBazaManager.pobierzWierszeZBazy(warunki);

	}
}
