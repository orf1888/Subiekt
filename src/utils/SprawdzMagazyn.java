package utils;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import kontroler.ObiektWyszukanieWarunki;
import kontroler.ProduktBaza;
import model.produkt.Produkt;

public class SprawdzMagazyn
{
	public static String[] kolumny =
	{ "Kod", "Nazwa", "Ilość", "Status" };

	/***
	 * Funkcja pobiera wszystkie widoczne produkty z db.
	 * 
	 * @return lista_produktow
	 * @throws SQLException
	 */
	private static List<String[]> pobierzMagazyn() throws SQLException
	{
		ObiektWyszukanieWarunki warunki = new ObiektWyszukanieWarunki(
				new Produkt());
		warunki.dodajWarunek(1, "widoczny");
		List<String[]> lista_m = Arrays.asList(ProduktBaza.instance()
				.pobierzWierszeZBazy(warunki));
		return lista_m;
	}

	/***
	 * Funkcja pobiera listę produktów z wczytanego pliku .xls.
	 * 
	 * @return lista_produktow
	 * @throws Exception
	 */
	private static List<String[]> pobierzProduktyZPliku() throws Exception
	{
		File plik_magazyn = null;
		Sheet arkusz = null;
		Cell[] kody_p;
		Cell[] ilosci_p;

		try
		{
			plik_magazyn = MojeUtils.wczytajPlik("xls");
		} catch (Exception e)
		{
			if (!e.getMessage().equals("Anulowano"))
			{
				MojeUtils.showError("Błąd wczytywania pliku");
			} else
				throw new Exception("Anulowano");
		}
		arkusz = FakturaZPliku.pobierzArkuszXLS(plik_magazyn);
		kody_p = arkusz.getColumn(0);
		ilosci_p = arkusz.getColumn(3);
		List<String[]> lista = new ArrayList<String[]>();

		for (int i = 10; i < kody_p.length; i++)
		{
			if (!kody_p[i].getContents().toString().trim().equals(""))
			{
				String[] tmp = new String[2];
				tmp[0] = kody_p[i].getContents().toString().trim();
				tmp[1] = ilosci_p[i].getContents().toString()
						.replaceAll(",000", "").trim();
				lista.add(tmp);
			}
		}
		return lista;
	}

	/***
	 * Funkcja odpowiedzialna za utworzenie tablicy String, zawierającej dane z
	 * funkcji: porownajPlikZMagazynem() oraz porownajMagazynZPlikiem().
	 * 
	 * @param magazyn
	 *            - aktualny stan magazynowy produktów w db
	 * @param magazyn_plik
	 *            - rzeczywiste stany magazynowe
	 * @return data.toArray(new String[data.size()][])
	 */
	private static String[][] porownajMagazyny(List<String[]> magazyn,
			List<String[]> magazyn_plik)
	{
		List<String[]> data = new ArrayList<>();
		data = porownajPlikZMagazynem(magazyn, magazyn_plik, data);
		data.add(new String[]
		{ "----", "TOWARY DO WYJAŚNIENIA", "---" });
		data = porownajMagazynZPlikiem(magazyn, magazyn_plik, data);
		return data.toArray(new String[data.size()][]);
	}

	/**
	 * Funkcja sprawdza, czy w bazie danych występują towary o niezerowym
	 * stanie, których brak jest w magazynie.
	 * 
	 * @param magazyn
	 *            - aktualny stan magazynowy produktów w db
	 * @param magazyn_plik
	 *            - rzeczywiste stany magazynowe
	 * @param data
	 *            - lista która zostanie dopełniona porównaniem (wstępnie na
	 *            liście powinny znajdować się porównanie z funkcji
	 *            porownajPlikZMagazynem())
	 * @return data
	 */
	private static List<String[]> porownajMagazynZPlikiem(
			List<String[]> magazyn, List<String[]> magazyn_plik,
			List<String[]> data)
	{
		for (int i = 0; i < magazyn.size(); i++)
		{
			boolean znaleziony = false;
			boolean niezerowy = false;
			for (int j = 0; j < magazyn_plik.size(); j++)
			{
				if (Integer.parseInt(magazyn.get(i)[2]) != 0)
				{
					niezerowy = true;
					if (magazyn.get(i)[0].equals(magazyn_plik.get(j)[0]))
					{
						znaleziony = true;
						break;
					}
				}
			}
			if (!znaleziony && niezerowy)
				data.add(new String[]
				{ magazyn.get(i)[0], magazyn.get(i)[1], magazyn.get(i)[2],
						"NIE WYSTĘPUJE W OSTATKACH!" });
		}
		return data;
	}

	/***
	 * Funkcja porównuje ilosci produktów w db oraz rzczywiste ilości
	 * dostarczone z magazynu.
	 * 
	 * @param magazyn
	 *            - aktualny stan magazynowy produktów w db
	 * @param magazyn_plik
	 *            - rzeczywiste stany magazynowe
	 * @param data
	 *            - lista która zostanie wypełniona porównaniem
	 * @return data
	 */
	private static List<String[]> porownajPlikZMagazynem(
			List<String[]> magazyn, List<String[]> magazyn_plik,
			List<String[]> data)
	{
		for (int i = 0; i < magazyn_plik.size(); i++)
		{
			boolean znaleziony = false;
			String[] data_tmp = new String[4];
			for (int j = 0; j < magazyn.size(); j++)
			{
				if (magazyn_plik.get(i)[0].equals(magazyn.get(j)[0]))
				{
					znaleziony = true;
					data_tmp[0] = magazyn.get(j)[0];
					data_tmp[1] = magazyn.get(j)[1];
					data_tmp[2] = magazyn.get(j)[2];
					if (Integer.parseInt(magazyn_plik.get(i)[1]) == Integer
							.parseInt(magazyn.get(j)[2]))
						data_tmp[3] = "OK";
					if (Integer.parseInt(magazyn_plik.get(i)[1]) > Integer
							.parseInt(magazyn.get(j)[2]))
						data_tmp[3] = "ZA DUŻO NA UKRAINIE";
					if (Integer.parseInt(magazyn_plik.get(i)[1]) < Integer
							.parseInt(magazyn.get(j)[2]))
						data_tmp[3] = "ZA MAŁO NA UKRAINIE";
					break;
				}
			}
			if (!znaleziony)
			{
				data_tmp[0] = magazyn_plik.get(i)[0];
				data_tmp[3] = "BRAK PRODUKTU W BAZIE DANYCH";
			}
			data.add(data_tmp);
		}
		return data;
	}

	/***
	 * Funkcja pobiera dane z db oraz pliku magazynowego, następnie robi
	 * porównania.
	 * 
	 * @return tablica String gotowa do wstawienia w tabelę
	 * @throws Exception
	 */
	public static String[][] stworzOstatki() throws Exception
	{
		List<String[]> magazyn = pobierzMagazyn();
		List<String[]> magazyn_plik = pobierzProduktyZPliku();
		return porownajMagazyny(magazyn, magazyn_plik);
	}
}