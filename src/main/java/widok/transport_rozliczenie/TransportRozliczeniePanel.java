package widok.transport_rozliczenie;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

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

	private JPopupMenu tworzMenuPopup()
	{
		JPopupMenu result = new JPopupMenu();
		JMenuItem itemEdytuj = new JMenuItem("Edytuj", null);
		result.add(itemEdytuj);
		itemEdytuj.setHorizontalTextPosition(SwingConstants.RIGHT);
		edytujListener = tworzEdytujListener();
		itemEdytuj.addActionListener(edytujListener);

		return result;
	}

	public TransportRozliczeniePanel(boolean s) throws Exception {
		super(s);
		try
		{
			warunki = new ObiektWyszukanieWarunki(null);
			PanelOgolnyParametry params = new PanelOgolnyParametry(
					tworzModelFuktor(), tworzMenuPopup(),
					new TransportRozliczenieBaza(), null,
					new TransportPanelEdytujDodaj(), null/* panelDwukliku */,
					false, TransportRozliczenie.opisKolumn);

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
			MojeUtils.error(e1);
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
		case "Bieżący miesiąc":
		{
			daty = DataUtils.getCurrentMonth();
			sorter.setSelectedItem("Bieżący miesiąc");
			sort.sorterT = "Bieżący miesiąc";
			break;
		}
		case "Bieżący rok":
		{
			daty = DataUtils.getCurrentYear();
			sorter.setSelectedItem("Bieżący rok");
			sort.sorterT = "Bieżący rok";
			break;
		}
		}
		warunki = new ObiektWyszukanieWarunki(null);
		if (!wszystkie)
		{
			warunki.dodajWarunekZData(daty[0], daty[1], "data_ksiegowania");
		}
		SorterKontroler.edytuj(sort);
		return getObiektBazaManager().pobierzWierszeZBazy(warunki);

	}
}
