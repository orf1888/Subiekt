package kontroler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Faktura;
import model.Kontrachent;
import model.Wysylka;
import model.produkt.Produkt;
import utils.BazaDanych;
import utils.BazaStatementFunktor;
import utils.MojeUtils;

public class InformatorBaza
{

	private static InformatorBaza instance = new InformatorBaza();

	public static InformatorBaza instance()
	{
		return instance;
	}

	public static String[] kolumny_sprzedaz =
	{ "Numer faktury", "Data wystawienia", "Ilość produktu", "Kontrahent" };

	public static String[] kolumny_wysylki =
	{ "Numer wysyłki", "Data wysyłki", "Ilość produktu" };

	BazaStatementFunktor pobieranieWierszaFunktorSprzedazy = new BazaStatementFunktor()
	{
		@Override
		public List<String[]> operacja(ResultSet result) throws SQLException
		{
			List<String[]> wynik = new ArrayList<String[]>();
			// dodajemy do tabeli kolejne pobrane wiersze
			while (result.next())
			{
				String data = MojeUtils.formatujDate(result.getString(2));
				String[] wiersz =
				{
						MojeUtils.poprawNrFaktury(1, result.getInt(1),
								data.substring(6), false), data,
						"" + result.getInt(3),
						KontrachentBaza.pobierzNazweZBazy(result.getInt(4)), };
				System.out.println(wiersz[0] + " " + wiersz[1] + " "
						+ wiersz[2] + " " + wiersz[3]);
				wynik.add(wiersz);
			}
			return wynik;
		}
	};

	@SuppressWarnings("unchecked")
	public String[][] pobierzSrzedazZBazy(int id_produkt) throws SQLException
	{
		String querySql = "SELECT DISTINCT faktura.numer, faktura.data_wystawienia, c_faktura_produkt.ilosc_produktu, kontrachent.id_kontrachent FROM "
				+ Faktura.tableName
				+ ", "
				+ Kontrachent.tableName
				+ ", "
				+ Produkt.tableName + ", " + Produkt.tableLaczacaFaktura;

		querySql += " WHERE produkt.id_produkt=c_faktura_produkt.id_produkt "
				+ "and produkt.id_produkt=" + id_produkt
				+ " and faktura.id_faktura=c_faktura_produkt.id_faktura "
				+ "and kontrachent.id_kontrachent=faktura.id_kontrachent";

		List<String[]> wynik = (List<String[]>) BazaDanych
				.getInstance()
				.zapytanie(
						/* sql do pobrania */querySql,
						/* operacja na pobranych danych */pobieranieWierszaFunktorSprzedazy);

		/* druga i trzecia wartosc to daty wynik[1] */
		return wynik.toArray(new String[wynik.size()][]);
	}

	BazaStatementFunktor pobieranieWierszaFunktorWysylek = new BazaStatementFunktor()
	{
		@Override
		public List<String[]> operacja(ResultSet result) throws SQLException
		{
			List<String[]> wynik = new ArrayList<String[]>();
			// dodajemy do tabeli kolejne pobrane wiersze
			while (result.next())
			{
				String data = MojeUtils.formatujDate(result.getString(2));
				String[] wiersz =
				{ "Wysyłka nr. " + result.getInt(1), data,
						"" + result.getInt(3) };
				System.out.println(wiersz[0] + " " + wiersz[1] + " "
						+ wiersz[2]);
				wynik.add(wiersz);
			}
			return wynik;
		}
	};

	@SuppressWarnings("unchecked")
	public String[][] pobierzWysylekZBazy(int id_produkt) throws SQLException
	{
		String querySql = "SELECT DISTINCT wysylka.id, wysylka.data, c_wysylka_produkt.ilosc FROM "
				+ Wysylka.tableName
				+ ", "
				+ Produkt.tableName
				+ ", "
				+ Produkt.tableLaczacaWysylka;

		querySql += " WHERE produkt.id_produkt=c_wysylka_produkt.id_produkt "
				+ "and produkt.id_produkt=" + id_produkt
				+ " and wysylka.id=c_wysylka_produkt.id_wysylka";

		List<String[]> wynik = (List<String[]>) BazaDanych
				.getInstance()
				.zapytanie(
						/* sql do pobrania */querySql,
						/* operacja na pobranych danych */pobieranieWierszaFunktorWysylek);

		/* druga i trzecia wartosc to daty wynik[1] */
		return wynik.toArray(new String[wynik.size()][]);
	}

}
