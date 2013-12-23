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

	public static List<String[]> pobierzMagazyn() throws SQLException
	{
		ObiektWyszukanieWarunki warunki = new ObiektWyszukanieWarunki(
				new Produkt());
		warunki.dodajWarunek(1, "widoczny");
		List<String[]> lista_m = Arrays.asList(ProduktBaza.instance()
				.pobierzWierszeZBazy(warunki));
		return lista_m;
	}

	public static List<String[]> pobierzMagazynZPliku() throws Exception
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
			MojeUtils.showError("Błąd wczytywania pliku");
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

	public static String[][] porownajMagazyny(List<String[]> magazyn,
			List<String[]> magazyn_plik)
	{
		List<String[]> data = new ArrayList<>();
		for (int i = 0; i < magazyn.size(); i++)
		{
			for (int j = 0; j < magazyn_plik.size(); j++)
			{
				if (magazyn.get(i)[0].equals(magazyn_plik.get(j)[0]))
				{
					String[] data_tmp = new String[4];
					data_tmp[0] = magazyn.get(i)[0];
					data_tmp[1] = magazyn.get(i)[1];
					data_tmp[2] = magazyn.get(i)[2];
					if (Integer.parseInt(magazyn_plik.get(j)[1]) == Integer
							.parseInt(magazyn.get(i)[2]))
						data_tmp[3] = "OK";
					else
						data_tmp[3] = "SPRAWDŹ!";
					data.add(data_tmp);
					break;
				}
			}
		}
		return data.toArray(new String[data.size()][]);
	}

	public static String[][] stworzOstatki() throws Exception
	{
		List<String[]> magazyn = pobierzMagazyn();
		List<String[]> magazyn_plik = pobierzMagazynZPliku();
		return porownajMagazyny(magazyn, magazyn_plik);
	}
}
