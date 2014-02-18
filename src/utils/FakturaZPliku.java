package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import kontroler.FakturaBaza;
import kontroler.ProduktBaza;
import kontroler.TransportRozliczenieBaza;
import model.Faktura;
import model.TransportRozliczenie;
import model.produkt.Produkt;
import model.produkt.ProduktWFakturze;
import widok.WidokGlowny;

public class FakturaZPliku
{
	private static TransportRozliczenie rachunek;
	private static boolean isRachunek = true;

	public static boolean wczytajTowaryZFS() throws Exception
	{
		String niewczytane_rekordy = new String();
		/* towary z pliku o większej ilości niż w bazie */
		String zle = new String();
		/* niewczytanie_rekordy + zle */
		String komunikat_koncowy = new String();
		File plikTowary = null;
		try
		{
			plikTowary = MojeUtils.wczytajPlik("xls");
		} catch (Exception e)
		{
			if (e.getMessage().equals("Anulowano"))
				return false;
		}

		Sheet arkusz = pobierzArkuszXLS(plikTowary);
		String[][] produkty = pobierzProduktyZPliku(arkusz);

		Produkt produktSlownik = null;
		List<ProduktWFakturze> listaProduktow = new ArrayList<ProduktWFakturze>();
		/*
		 * index odpowiadajacy zmiennej i, z tym że nie wszystkie produkty
		 * dodamy do faktury. Te ktorych nie ma w bazie pomijamy i nie
		 * zwiekszamy indeksu indeks numerujemy od 1
		 */
		int index_produktu = 1;
		for (int i = 0; i < produkty.length; ++i)
		{
			String kodProduktu = produkty[i][0];
			int ilosc_produktu = Integer.parseInt(produkty[i][1]);
			try
			{
				produktSlownik = (Produkt) ProduktBaza
						.pobierzObiektZBazyPoKodzie(kodProduktu);
				if (produktSlownik == null)
					throw new NullPointerException();
			} catch (Exception e)
			{
				niewczytane_rekordy += "Produkt o nr " + (i + 1)
						+ " o kodzie \"" + produkty[i][0] + "\"\n";
				continue;
			}

			int cena = produktSlownik.cena_zakupu;
			int mnoznik = 100;
			cena = ((cena) * mnoznik / 100);
			if (produktSlownik.ilosc < Integer.parseInt(produkty[i][1]))
			{
				zle += "Produktu o nr " + (i + 1) + " o kodzie \""
						+ produkty[i][0] + "\" jest za mało w magazynie!\n"
						+ "magazyn=" + produktSlownik.ilosc + " faktura="
						+ produkty[i][1] + "\n";
			} else
			{
				listaProduktow.add(new ProduktWFakturze(index_produktu, cena,
						ilosc_produktu, produktSlownik, false, 0, 0));
				++index_produktu;
			}
		}

		long waluta = 1;

		Faktura nowa = FakturaBaza.nowaFaktura(Faktura.SPRZEDAZ, 0,
				listaProduktow, waluta, DataUtils.pobierzAktualnaDate());
		FakturaBaza.instance().dodaj(nowa);
		WidokGlowny.frame.panelFakturSprz.tabelaDodajWiersz(nowa
				.piszWierszTabeli());

		if (isRachunek)
		{
			rachunek.id_faktury_odpowiadajacej = nowa.id_faktura;
			TransportRozliczenieBaza.instance().dodaj_encje_transport(rachunek);
			WidokGlowny.frame.panelTransportRozliczenie
					.tabelaDodajWiersz(rachunek.piszWierszTabeli());
		} else
			MojeUtils
					.showMsg("W fakturze nie znaleziono rachunku transportowego.");
		/* Brak kodu bądź nie rozpoznano produktu */
		if (!niewczytane_rekordy.isEmpty())
		{
			komunikat_koncowy += "Produkty znajdujące się na liście nie występują w bazie.\nNależy je dodać ręcznie:\n";
			komunikat_koncowy += niewczytane_rekordy;
			/* Towaru w fakturze jest więcej niż w magazynie */
			if (!zle.isEmpty())
			{
				komunikat_koncowy += "\nProdukty znajdujące się na liście występują w ilości\n"
						+ "większej niż w bazie. Zostaną one pominięte!:\n";
				komunikat_koncowy += zle;
				komunikat_koncowy += "\nDodaną fakturę należy zedytować ręcznie!\nPamiętaj o dodaniu kontrahenta!";
				/* Pokaż komunikat */
				MojeUtils.showMsg(komunikat_koncowy);
				return false;
			} else
			{
				MojeUtils.showMsg(komunikat_koncowy);
				return false;
			}
		} else
		{
			/* Towaru w fakturze jest więcej niż w magazynie */
			if (!zle.isEmpty())
			{
				komunikat_koncowy += "Produkty znajdujące się na liście występują w ilości\n"
						+ "większej niż w bazie. Zostaną one pominięte!:\n\n";
				komunikat_koncowy += zle;
				komunikat_koncowy += "\nDodaną fakturę należy zedytować ręcznie!\nPamiętaj o dodaniu kontrahenta!";
				/* Pokaż komunikat */
				MojeUtils.showMsg(komunikat_koncowy);
				return false;
			}
		}
		return true;
	}

	public static Sheet pobierzArkuszXLS(File plik) throws Exception
	{
		if (plik != null)
		{
			WorkbookSettings setting = new WorkbookSettings();
			setting.setEncoding("Cp1251");
			Workbook plikXLS = Workbook.getWorkbook(plik, setting);
			Sheet arkusz = plikXLS.getSheet(0);
			return arkusz;
		} else
			throw new UserShowException("Nie udało się pobrać arkusza!");

	}

	private static String[][] pobierzProduktyZPliku(Sheet arkusz)
			throws Exception
	{
		int kod_kolumna;
		int ilosc_kolumna;
		int[] start_end = znajdzPoczatekKoniec(arkusz, 0);
		if (start_end[0] == 0 && start_end[1] == 0)
		{
			start_end = znajdzPoczatekKoniec(arkusz, 1);
			kod_kolumna = 2;
			ilosc_kolumna = 6;
		} else
		{
			kod_kolumna = 1;
			ilosc_kolumna = 6;
		}

		String[][] result = null;
		try
		{
			Cell[] kody = arkusz.getColumn(kod_kolumna);
			Cell[] ilosci = arkusz.getColumn(ilosc_kolumna);
			/* zarezerwuj miejsce */
			result = new String[start_end[1] - start_end[0] + 1][2];// -sieci

			int result_index = 0;
			for (int i = start_end[0]; i <= start_end[1]; i++)
			{
				result[result_index][0] = kody[i].getContents().toString();
				result[result_index][1] = ilosci[i].getContents().toString()
						.replaceAll(",000", "");
				++result_index;
			}
			try
			{
				String[] rachunek_transportowy = pobierzRachunekTrasportowy(
						arkusz, start_end[1]);
				rachunek = new TransportRozliczenie(0, new Date(),
						rachunek_transportowy[1],
						Integer.parseInt(rachunek_transportowy[2]),
						Long.parseLong(rachunek_transportowy[0]), 0);
			} catch (Exception e)
			{
				/* W fakturze z pliku nie znaleziono rachunku transportowego */
				isRachunek = false;
			}
		} catch (Exception e)
		{
			MojeUtils.showError("Wczytany plik jest niekompatybilny!");
			e.printStackTrace();
		}
		return result;
	}

	private static String[] pobierzRachunekTrasportowy(Sheet arkusz,
			int koniec_towarow)
	{
		String[] result = new String[3];
		/* Waluta */
		String tmp_waluta = arkusz.getCell(7, 8).getContents().toString();
		switch (tmp_waluta)
		{
		case TransportRozliczenie.EURO:
		{
			result[0] = "" + 2;
			break;
		}
		case TransportRozliczenie.PLN:
		{
			result[0] = "" + 1;
			break;
		}
		default:
			break;
		}
		/* Numer oryginału faktury */
		String tmp_numer = arkusz.getCell(0, 12).getContents().toString();
		result[1] = tmp_numer;/* .substring(tmp_numer.length() - 9); */
		/* Wartosc */
		String tmp_wartosc = arkusz.getCell(8, (koniec_towarow + 4))
				.getContents().toString().replaceAll(",", "");
		/*
		 * Znak spacji nie jest rozpoznawany jako spacja, w funkcji znajduje się
		 * znak żywcem wklejony...
		 */
		result[2] = tmp_wartosc.replaceAll(" ", "").trim();
		return result;
	}

	protected static int[] znajdzPoczatekKoniec(Sheet arkusz,
			int index_kolumy_lp)
	{
		int[] poczatek_koniec = new int[2];
		/* szukanie poczatku i konca tabelki produktow */
		Cell[] liczbyporzadkowe = arkusz.getColumn(index_kolumy_lp);
		int start = 0;
		int end = 0;
		int index = 0;
		do
		{
			try
			{
				int value = Integer.parseInt(liczbyporzadkowe[index]
						.getContents().toString());
				if (value != 0)
				{
					/*
					 * wczytuje liczbe jesli tam jest, wtedy probuje ustawic
					 * pierszą jako START i ostatnią jako END
					 */
					if (start == 0)
						start = index;
					end = index;
				}
			} catch (Exception e)
			{
				/* Przemilcz */
			}

			++index;
		} while (index < liczbyporzadkowe.length);
		poczatek_koniec[0] = start;
		poczatek_koniec[1] = end;
		return poczatek_koniec;
	}

}