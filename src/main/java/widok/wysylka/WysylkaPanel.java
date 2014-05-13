package widok.wysylka;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import kontroler.ObiektWyszukanieWarunki;
import kontroler.SorterKontroler;
import kontroler.WysylkaBaza;
import model.Sorter;
import model.Wysylka;
import utils.DataUtils;
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

	public WysylkaPanel(boolean s) throws Exception {
		super(s);
		try
		{
			JPopupMenu popupTabeliMagazynu = tworzPopupMagazynWysylce();
			warunki = new ObiektWyszukanieWarunki(new Wysylka());
			PanelEdytujDodajObiekt panelDwukliku = new WyswietlPDFPanel();
			PanelOgolnyParametry params = new PanelOgolnyParametry(
					tworzModelFuktor(), tworzMenuPopup(), new WysylkaBaza(),
					new WysylkaPanelEdytujDodaj("Dodaj", true,
							popupTabeliMagazynu), new WysylkaPanelEdytujDodaj(
							"Edytuj", true, popupTabeliMagazynu),
					panelDwukliku, false, Wysylka.opisKolumn);
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
								.showError("Błąd ustawiania danych widoku faktury!");
					}
					try
					{
						przeladujTabele(dane, editableFunktor, false);
					} catch (Exception e)
					{
						MojeUtils
								.showError("Błąd przeładowania widoku faktur!");
					}
				}
			});
			params.setBounds(1150, 550);
			init(params, false);
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
					return ustawOkres(null);
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
			// TODO Auto-generated catch block
			MojeUtils.error(e1);
		}
		if (wejscieSortowania == null)
			wejscieSortowania = sort.sorterW;
		String[] daty = null;
		boolean wszystkie = false;
		switch (wejscieSortowania)
		{
		case "Wszystkie":
		{
			wszystkie = true;
			sort.sorterW = "Wszystkie";
			sorter.setSelectedItem("Wszystkie");
			break;
		}
		case "Bierzący miesiąc":
		{
			daty = DataUtils.getCurrentMonth();
			sorter.setSelectedItem("Bierzący miesiąc");
			sort.sorterW = "Bierzący miesiąc";
			break;
		}
		case "Bierzący rok":
		{
			daty = DataUtils.getCurrentYear();
			sorter.setSelectedItem("Bierzący rok");
			sort.sorterW = "Bierzący rok";
			break;
		}
		}
		warunki = new ObiektWyszukanieWarunki(new Wysylka());
		if (!wszystkie)
		{
			warunki.dodajWarunekZData(daty[0], daty[1], "data");
		}
		SorterKontroler.edytuj(sort);
		return getObiektBazaManager().pobierzWierszeZBazy(warunki);

	}

}