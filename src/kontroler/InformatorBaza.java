package kontroler;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Faktura;
import model.Kontrachent;
import model.Wysylka;
import model.produkt.Produkt;
import utils.BazaDanych;
import utils.BazaStatementFunktor;
import utils.DataUtils;
import utils.MojeUtils;

public class InformatorBaza
{

	private static InformatorBaza instance = new InformatorBaza();

	public static InformatorBaza instance()
	{
		return instance;
	}

	public static String[] kolumny_sprzedaz =
	{ "Numer faktury", "Data wystawienia", "Ilość produktu", "kontrachent" };

	public static String[] kolumny_sprzedaz_ilosc =
	{ "Kod", "Nazwa", "Ilość sprzedanych" };

	public static String[] kolumny_wyslane_ilosc =
	{ "Kod", "Nazwa", "Ilość wysłanych" };

	public static String[] kolumny_wysylki =
	{ "Numer wysyłki", "Data wysyłki", "Ilość produktu" };

	public static String[] kolumny_ruch_towaru =
	{ "Numer operacji", "Data operacji", "Ilość w operacji" };

	BazaStatementFunktor pobieranieWierszaFunktorSprzedazy = new BazaStatementFunktor()
	{
		@Override
		public List<String[]> operacja(ResultSet result) throws Exception
		{
			List<String[]> wynik = new ArrayList<String[]>();
			// dodajemy do tabeli kolejne pobrane wiersze
			while (result.next())
			{
				String data = DataUtils.formatujDate(result.getString(2));
				String[] wiersz =
				{
						MojeUtils.poprawNrFaktury(1, result.getInt(1),
								data.substring(6), false), data,
						"" + result.getInt(3),
						KontrachentBaza.pobierzNazweZBazy(result.getInt(4)), };
				wynik.add(wiersz);
			}
			return wynik;
		}
	};

	BazaStatementFunktor pobieranieWierszaFunktorSprzedazyBezDaty = new BazaStatementFunktor()
	{
		@Override
		public List<String[]> operacja(ResultSet result) throws Exception
		{
			List<String[]> wynik = new ArrayList<String[]>();
			// dodajemy do tabeli kolejne pobrane wiersze
			while (result.next())
			{
				String data = DataUtils.formatujDate(result.getString(2));
				String[] wiersz =
				{
						MojeUtils.poprawNrFaktury(1, result.getInt(1),
								data.substring(6), false), result.getString(2),
						"" + result.getInt(3),
						KontrachentBaza.pobierzNazweZBazy(result.getInt(4)), };
				wynik.add(wiersz);
			}
			return wynik;
		}
	};

	BazaStatementFunktor pobieranieWierszaFunktorWysylek = new BazaStatementFunktor()
	{
		@Override
		public List<String[]> operacja(ResultSet result) throws Exception
		{
			List<String[]> wynik = new ArrayList<String[]>();
			// dodajemy do tabeli kolejne pobrane wiersze
			while (result.next())
			{
				String data = DataUtils.formatujDate(result.getString(2));
				String[] wiersz =
				{ "Wysyłka nr. " + result.getInt(1), data,
						"" + result.getInt(3) };
				wynik.add(wiersz);
			}
			return wynik;
		}
	};

	BazaStatementFunktor pobieranieWierszaFunktorWysylekBezDaty = new BazaStatementFunktor()
	{
		@Override
		public List<String[]> operacja(ResultSet result) throws Exception
		{
			List<String[]> wynik = new ArrayList<String[]>();
			// dodajemy do tabeli kolejne pobrane wiersze
			while (result.next())
			{
				String[] wiersz =
				{ "Wysyłka nr. " + result.getInt(1), result.getString(2),
						"" + result.getInt(3) };
				wynik.add(wiersz);
			}
			return wynik;
		}
	};

	BazaStatementFunktor pobieranieIlosciSprzedazyIWysylekFunktor = new BazaStatementFunktor()
	{
		@Override
		public List<String[]> operacja(ResultSet result) throws Exception
		{
			List<String[]> wynik = new ArrayList<String[]>();
			// dodajemy do tabeli kolejne pobrane wiersze
			while (result.next())
			{
				String[] wiersz =
				{ result.getString(1), result.getString(2),
						"" + result.getInt(3) };
				wynik.add(wiersz);
			}
			return wynik;
		}
	};

	@SuppressWarnings("unchecked")
	public String[][] pobierzSrzedazZBazy(int id_produkt, String data_od,
			String data_do, boolean z_data) throws Exception
	{
		String querySql = "SELECT faktura.numer, faktura.data_wystawienia, c_faktura_produkt.ilosc_produktu, kontrachent.id_kontrachent FROM "
				+ Faktura.tableName
				+ ", "
				+ Kontrachent.tableName
				+ ", "
				+ Produkt.tableName + ", " + Produkt.tableLaczacaFaktura;

		querySql += " WHERE produkt.id_produkt=" + id_produkt
				+ " and produkt.id_produkt=c_faktura_produkt.id_produkt "
				+ " and faktura.id_faktura=c_faktura_produkt.id_faktura "
				+ " AND faktura.data_wystawienia BETWEEN '" + data_od
				+ "' AND '" + data_do + "'"
				+ "and kontrachent.id_kontrachent=faktura.id_kontrachent"
				+ " and faktura.rodzaj=" + Faktura.SPRZEDAZ;

		List<String[]> wynik = null;
		if (z_data)
		{
			wynik = (List<String[]>) BazaDanych
					.getInstance()
					.zapytanie(
							/* sql do pobrania */querySql,
							/* operacja na pobranych danych */pobieranieWierszaFunktorSprzedazy);
		} else
		{
			wynik = (List<String[]>) BazaDanych
					.getInstance()
					.zapytanie(
							/* sql do pobrania */querySql,
							/* operacja na pobranych danych */pobieranieWierszaFunktorSprzedazyBezDaty);

		}
		return wynik.toArray(new String[wynik.size()][]);
	}

	@SuppressWarnings("unchecked")
	public String[][] pobierzWysylekZBazy(int id_produkt, String data_od,
			String data_do, boolean z_data) throws Exception
	{
		String querySql = "SELECT wysylka.id, wysylka.data, c_wysylka_produkt.ilosc FROM "
				+ Wysylka.tableName
				+ ", "
				+ Produkt.tableName
				+ ", "
				+ Produkt.tableLaczacaWysylka;

		querySql += " WHERE produkt.id_produkt=" + id_produkt
				+ " and produkt.id_produkt=c_wysylka_produkt.id_produkt "
				+ "and wysylka.data BETWEEN '" + data_od + "' AND '" + data_do
				+ "'" + " and wysylka.id=c_wysylka_produkt.id_wysylka";

		List<String[]> wynik = null;
		if (z_data)
		{
			wynik = (List<String[]>) BazaDanych.getInstance().zapytanie(
			/* sql do pobrania */querySql,
			/* operacja na pobranych danych */pobieranieWierszaFunktorWysylek);
		} else
		{
			wynik = (List<String[]>) BazaDanych
					.getInstance()
					.zapytanie(
							/* sql do pobrania */querySql,
							/* operacja na pobranych danych */pobieranieWierszaFunktorWysylekBezDaty);

		}
		return wynik.toArray(new String[wynik.size()][]);
	}

	@SuppressWarnings("unchecked")
	public String[][] pobierzIloscSprzedazZBazy(int id_produkt, String data_od,
			String data_do) throws Exception
	{
		String querySql = "SELECT produkt.kod, produkt.nazwa, SUM(c_faktura_produkt.ilosc_produktu) FROM "
				+ Produkt.tableName
				+ ", "
				+ Produkt.tableLaczacaFaktura
				+ ", "
				+ Faktura.tableName;

		querySql += " WHERE produkt.id_produkt=" + id_produkt
				+ " and produkt.id_produkt=c_faktura_produkt.id_produkt"
				+ " AND faktura.data_wystawienia BETWEEN '" + data_od
				+ "' AND '" + data_do + "'"
				+ " AND faktura.id_faktura=c_faktura_produkt.id_faktura"
				+ " and faktura.rodzaj=1";

		List<String[]> wynik = (List<String[]>) BazaDanych
				.getInstance()
				.zapytanie(
						/* sql do pobrania */querySql,
						/* operacja na pobranych danych */pobieranieIlosciSprzedazyIWysylekFunktor);

		return wynik.toArray(new String[wynik.size()][]);
	}

	@SuppressWarnings("unchecked")
	public String[][] pobierzIloscWyslanychZBazy(int id_produkt,
			String data_od, String data_do) throws Exception
	{
		String querySql = "SELECT produkt.kod, produkt.nazwa, SUM(c_wysylka_produkt.ilosc) FROM "
				+ Wysylka.tableName
				+ ", "
				+ Produkt.tableName
				+ ", "
				+ Produkt.tableLaczacaWysylka;
		querySql += " WHERE produkt.id_produkt=" + id_produkt
				+ " and produkt.id_produkt=c_wysylka_produkt.id_produkt "
				+ " and wysylka.data BETWEEN '" + data_od + "' AND '" + data_do
				+ "'" + " and wysylka.id=c_wysylka_produkt.id_wysylka";

		List<String[]> wynik = (List<String[]>) BazaDanych
				.getInstance()
				.zapytanie(
						/* sql do pobrania */querySql,
						/* operacja na pobranych danych */pobieranieIlosciSprzedazyIWysylekFunktor);

		return wynik.toArray(new String[wynik.size()][]);
	}

	public String[][] pobierzRuchTowaruZBazy(int id_produkt, String data_od,
			String data_do) throws Exception
	{
		String[][] wysylki = pobierzWysylekZBazy(id_produkt, data_od, data_do,
				false);
		String[][] faktury = pobierzSrzedazZBazy(id_produkt, data_od, data_do,
				false);
		List<String[]> wynik = new ArrayList<>();
		wynik.addAll(Arrays.asList(wysylki));
		wynik.addAll(Arrays.asList(faktury));
		DataUtils.sortujPoDacie(wynik);
		return DataUtils.foratujDate(wynik.toArray(new String[wynik.size()][]));
	}
}