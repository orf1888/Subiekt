package widok.faktura;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import kontroler.FakturaBaza;
import kontroler.ObiektWyszukanieWarunki;
import kontroler.ProduktBaza;
import kontroler.SorterKontroler;
import model.Faktura;
import model.ObiektWiersz;
import model.Sorter;
import utils.DataUtils;
import utils.MojeUtils;
import utils.UserShowException;
import widok.WyswietlPDFPanel;
import widok.abstrakt.DialogEdytujDodaj;
import widok.abstrakt.PanelEdytujDodajObiekt;
import widok.abstrakt.PanelOgolnyParametry;
import widok.abstrakt.PanelOgolnyPrzyciski;

public class FakturyGeneralPanel extends PanelOgolnyPrzyciski
{
	private static final long serialVersionUID = -1171279686468102750L;

	protected ActionListener korygujListener;

	private PanelEdytujDodajObiekt panelDodajKorekta;

	private PanelEdytujDodajObiekt panelEdytujKorekta;

	protected ActionListener tworzEdytujFakturaLubKorektaListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					/* zapamietaj ktory wiersz edytujemy */
					numerEdytowanegoWiersza = pobierzNumerZaznaczonegoWiersza();
					if (numerEdytowanegoWiersza < 0)
						return;
					ObiektWiersz wiersz = new ObiektWiersz(
							pobierzZaznaczonyWiersz());
					if (wiersz.wiersz == null)
						return;

					staryObiekt = getObiektBazaManager().pobierzObiektZBazy(
							wiersz);

					if (((Faktura) staryObiekt).isKorekta)
					{
						/* edycja korekty */
						oknoModalne = new DialogEdytujDodaj(
								panelEdytujOkButtonListener, anulujListener,
								panelEdytujKorekta, sizeX, sizeY);
					} else
					{
						/* edycja faktury */
						oknoModalne = new DialogEdytujDodaj(
								panelEdytujOkButtonListener, anulujListener,
								panelEdytuj, sizeX, sizeY);
					}
					/* wstaw dane do formatki */
					oknoModalne.uzupelnijFormatke(staryObiekt);

					oknoModalne
							.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					oknoModalne.setVisible(true);

					/* reszta dzieje sie w buttonie OK */
				} catch (Exception e)
				{
					MojeUtils.showPrintError(e);
				}
			}
		};
	}

	protected ActionListener tworzZaplaconaListener()
	{
		return new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					ObiektWiersz wiersz = new ObiektWiersz(
							pobierzZaznaczonyWiersz());
					if (wiersz.wiersz == null)
						return;
					Faktura faktura = (Faktura) getObiektBazaManager()
							.pobierzObiektZBazy(wiersz);
					Object[] polskiePrzyciski =
					{ "Zapłacona", "Nie zapłacona" };
					int wybor = JOptionPane.showOptionDialog(null, "Faktura "
							+ faktura.numer + " jest "
							+ (faktura.zaplacona ? "" : "nie ") + "zapłacona",
							"Płatności", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null,
							polskiePrzyciski, polskiePrzyciski[1]);
					if (wybor == 0)
					{

						zmienZaplacona(true);
					}

					if (wybor == 1)
						zmienZaplacona(false);

				} catch (Exception e)
				{
					MojeUtils.showPrintError(e);
				}
			}

			void zmienZaplacona(boolean zaplacona) throws Exception
			{
				ObiektWiersz wiersz = new ObiektWiersz(
						pobierzZaznaczonyWiersz());
				if (wiersz.wiersz == null)
					return;

				FakturaBaza.ustawZaplacona(wiersz, zaplacona);
			}
		};
	}

	private JPopupMenu tworzMenuPopup()
	{
		JPopupMenu result = new JPopupMenu();
		JMenuItem itemEdytuj = new JMenuItem("Edytuj", null);
		result.add(itemEdytuj);
		itemEdytuj.setHorizontalTextPosition(SwingConstants.RIGHT);
		edytujListener = tworzEdytujFakturaLubKorektaListener();
		itemEdytuj.addActionListener(edytujListener);

		JMenuItem itemKorekta = new JMenuItem("Dodaj korektę", null);
		result.add(itemKorekta);
		itemKorekta.setHorizontalTextPosition(SwingConstants.RIGHT);
		korygujListener = tworzKorektaListener();
		itemKorekta.addActionListener(korygujListener);

		JMenuItem itemZaplacona = new JMenuItem("Płatność", null);
		result.add(itemZaplacona);
		itemZaplacona.setHorizontalTextPosition(SwingConstants.RIGHT);
		ActionListener zaplaconaListener = tworzZaplaconaListener();
		itemZaplacona.addActionListener(zaplaconaListener);
		return result;
	}

	ActionListener penelKorektaOkButtonListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			try
			{
				/* pobierz dane z formatki */
				Faktura nowa = (Faktura) oknoModalne
						.pobierzZFormatki(staryObiekt.getId());
				Faktura stara = (Faktura) staryObiekt;
				FakturaBaza baza = ((FakturaBaza) getObiektBazaManager());

				if (stara.isKorekta)
				{
					throw new UserShowException("hahaha\neastereggvol3");
					/*
					 * powinno wykonac sie normalne Edytuj-Ok-Button z
					 * PanelPrzyciski tu wykonuje sie tylko
					 * Dodaj-Korekte-Ok-Button
					 */
				} else
				{
					// TODO tranzakcja
					stara.aktualna = false;
					baza.ustawNieaktualna(stara);

					/* nowa */
					nowa.isKorekta = true;
					baza.dodaj_encje_faktura(nowa);

					/* edytuj produkty (jak przy edycji) */

					ProduktBaza.dodajListeProduktow(nowa.produkty,
							nowa.id_faktura);
					ProduktBaza.dodajListeProduktow(nowa.produktyKorekta,
							nowa.id_faktura);
					boolean dodac = (nowa.rodzaj == Faktura.SPRZEDAZ) ? false
							: true;
					ProduktBaza.instance().zmienIloscProduktyZMagazynu(
							nowa.produktyKorekta, dodac);

					int wiersz = pobierzNumerZaznaczonegoWiersza();
					tabelaUsunWiersz(wiersz);
					tabelaDodajWiersz(nowa.piszWierszTabeli());
					/* tabela Dodaj wiersz + tabela Usun wiersz */
				}

				ukryjModalneOkno();
			} catch (UserShowException e)
			{
				MojeUtils.showError(e);
			} catch (Exception e)
			{
				MojeUtils.showPrintError(e);
			}
		}
	};

	protected ActionListener tworzKorektaListener()
	{
		return new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					/* zapamietaj ktory wiersz edytujemy */
					numerEdytowanegoWiersza = pobierzNumerZaznaczonegoWiersza();
					if (numerEdytowanegoWiersza < 0)
						return;
					ObiektWiersz wiersz = new ObiektWiersz(
							pobierzZaznaczonyWiersz());
					if (wiersz.wiersz == null)
						return;

					staryObiekt = getObiektBazaManager().pobierzObiektZBazy(
							wiersz);
					if (((Faktura) staryObiekt).isKorekta)
						throw new UserShowException(
								"Nie można dodać korekty do korekty!");
					oknoModalne = new DialogEdytujDodaj(
							penelKorektaOkButtonListener, anulujListener,
							panelDodajKorekta, sizeX, sizeY);

					/* wstaw dane do formatki */
					oknoModalne.uzupelnijFormatke(staryObiekt);

					oknoModalne
							.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					oknoModalne.setVisible(true);

					/* reszta dzieje sie w buttonie OK */
				} catch (UserShowException e)
				{
					MojeUtils.showError(e);
				} catch (Exception e)
				{
					MojeUtils.showPrintError(e);
				}
			}
		};
	}

	public FakturyGeneralPanel(JPopupMenu popupTabeliMagazynu,
			final int fakturaRodzaj, boolean s) throws Exception {
		super(s);
		try
		{
			panelDodajKorekta = new FakturyPanelKorekta("Dodaj korektę",
					fakturaRodzaj, true, popupTabeliMagazynu);
			panelEdytujKorekta = new FakturyPanelKorekta("Edytuj korektę",
					fakturaRodzaj, true, popupTabeliMagazynu);

			warunki = ObiektWyszukanieWarunki.TworzWarunekFaktura();
			warunki.removeWarunek("aktualna");

			PanelEdytujDodajObiekt panelDwukliku = fakturaRodzaj == Faktura.SPRZEDAZ ? new WyswietlPDFPanel()
					: null;
			PanelOgolnyParametry params = new PanelOgolnyParametry(
					tworzModelFuktor(fakturaRodzaj), tworzMenuPopup(),
					new FakturaBaza(), new FakturyPanelEdytujDodaj(
							"Dodaj fakturę", fakturaRodzaj, true,
							popupTabeliMagazynu, false),
					new FakturyPanelEdytujDodaj("Edytuj fakturę",
							fakturaRodzaj, true, popupTabeliMagazynu, false),
					panelDwukliku, false, Faktura.opisKolumn);
			sorter.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					String okres_sortowania = (String) sorter.getSelectedItem();
					try
					{
						ustawOkres(fakturaRodzaj, okres_sortowania);
					} catch (Exception e1)
					{
						MojeUtils
								.showError("Błąd ustawiania okresu wyświetlania!");
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

	public InitModelFunktor tworzModelFuktor(final int fakturaRodzaj)
	{
		return new InitModelFunktor()
		{
			@Override
			public String[][] getBeginningData()
			{
				try
				{
					if (fakturaRodzaj == Faktura.SPRZEDAZ)
						return ustawOkres(fakturaRodzaj, null);
					else
						return ustawOkres(fakturaRodzaj, null);
				} catch (Exception e)
				{
					MojeUtils.showPrintError(e);
					return null;
				}
			}
		};
	}

	private String[][] ustawOkres(int fakturaRodzaj, String wejscieSortowania)
			throws Exception
	{
		Sorter sort = null;
		try
		{
			sort = SorterKontroler.getSorter();
		} catch (Exception e1)
		{
			MojeUtils.error(e1);
		}
		if (wejscieSortowania == null && fakturaRodzaj == Faktura.SPRZEDAZ)
			wejscieSortowania = sort.sorterFS;
		if (wejscieSortowania == null && fakturaRodzaj == Faktura.ZAKUP)
			wejscieSortowania = sort.sorterFZ;
		String[] daty = null;
		boolean wszystkie = false;
		switch (wejscieSortowania)
		{
		case "Wszystkie":
		{
			wszystkie = true;
			if (fakturaRodzaj == Faktura.SPRZEDAZ)
				sort.sorterFS = "Wszystkie";
			else
				sort.sorterFZ = "Wszystkie";
			sorter.setSelectedItem("Wszystkie");
			break;
		}
		case "Bierzący miesiąc":
		{
			daty = DataUtils.getCurrentMonth();
			sorter.setSelectedItem("Bierzący miesiąc");
			if (fakturaRodzaj == Faktura.SPRZEDAZ)
				sort.sorterFS = "Bierzący miesiąc";
			else
				sort.sorterFZ = "Bierzący miesiąc";
			break;
		}
		case "Bierzący rok":
		{
			daty = DataUtils.getCurrentYear();
			sorter.setSelectedItem("Bierzący rok");
			if (fakturaRodzaj == Faktura.SPRZEDAZ)
				sort.sorterFS = "Bierzący rok";
			else
				sort.sorterFZ = "Bierzący rok";
			break;
		}
		}
		warunki = ObiektWyszukanieWarunki.TworzWarunekFaktura();
		warunki.dodajWarunek(fakturaRodzaj, "rodzaj");
		if (!wszystkie)
		{
			warunki.dodajWarunekZData(daty[0], daty[1], "data_wystawienia");
		}
		SorterKontroler.edytuj(sort);
		return getObiektBazaManager().pobierzWierszeZBazy(warunki);

	}

}