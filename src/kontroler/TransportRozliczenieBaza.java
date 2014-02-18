package kontroler;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.ObiektWiersz;
import model.ObiektZId;
import model.TransportRozliczenie;
import model.Waluta;
import utils.BazaDanych;
import utils.BazaStatementFunktor;
import utils.DataUtils;
import utils.MojeUtils;
import utils.SqlUtils;
import utils.UserShowException;

public class TransportRozliczenieBaza implements ObiektBazaManager
{

	private static TransportRozliczenieBaza instance = new TransportRozliczenieBaza();

	public static TransportRozliczenieBaza instance()
	{
		return instance;
	}

	BazaStatementFunktor pobieranieWierszaFunktor = new BazaStatementFunktor()
	{
		@Override
		public Object operacja(ResultSet result) throws Exception
		{
			List<String[]> wynik = new ArrayList<String[]>();
			// dodajemy do tabeli kolejne pobrane wiersze
			while (result.next())
			{
				String data_ksiegowania = DataUtils.formatujDate(result
						.getString(2));
				String[] wiersz =
				{
						result.getString(1),
						MojeUtils.pobierzNrFakturyOdpowiadającej(result
								.getInt(5)),// id faktury
						data_ksiegowania,
						MojeUtils.formatujWartosc(result.getInt(3))
								+ " "
								+ WalutaManager.pobierzNazweZBazy(
										result.getLong(4) - 1,
										Waluta.tabelaWaluta) };
				wynik.add(wiersz);
			}
			return wynik;
		}
	};

	@Override
	public String[][] pobierzWierszeZBazy(ObiektWyszukanieWarunki warunki)
			throws Exception
	{
		String querySql = "SELECT DISTINCT nr_faktury, data_ksiegowania, wartosc, waluta, id_faktury_odpowiadajacej FROM "
				+ TransportRozliczenie.tableName;
		if (warunki != null)
			querySql += warunki.generujWarunekWhere()
					+ " ORDER BY data_ksiegowania";
		else
			querySql += " ORDER BY data_ksiegowania";
		MojeUtils.println(querySql);

		@SuppressWarnings("unchecked")
		List<String[]> wynik = (List<String[]>) BazaDanych.getInstance()
				.zapytanie(
				/* sql do pobrania */querySql,
				/* operacja na pobranych danych */pobieranieWierszaFunktor);
		return wynik.toArray(new String[wynik.size()][]);
	}

	public void dodaj_encje_transport(TransportRozliczenie nowaFaktura)
			throws Exception
	{
		String data_ksiegowania_rozliczenia = DataUtils.stringToDate_format
				.format(nowaFaktura.data_ksiegowania);
		int generatedId = BazaDanych
				.getInstance()
				.wstaw("INSERT INTO  "
						+ TransportRozliczenie.tableName
						+ " (nr_faktury, data_ksiegowania, wartosc, waluta, id_faktury_odpowiadajacej"
						+ ") VALUES ('"
						+ SqlUtils.popraw(nowaFaktura.nr_faktury) + "','"
						+ SqlUtils.popraw(data_ksiegowania_rozliczenia) + "','"
						+ SqlUtils.popraw(nowaFaktura.wartosc) + "','"
						+ SqlUtils.popraw(nowaFaktura.waluta) + "', '"
						+ nowaFaktura.id_faktury_odpowiadajacej + "')");

		nowaFaktura.id = generatedId;
	}

	public static List<TransportRozliczenie> rozliczZaOkres(String[][] dane)
			throws SQLException, UserShowException, IOException
	{
		ArrayList<TransportRozliczenie> result = new ArrayList<>();
		for (int i = 0; i < dane.length; i++)
		{
			result.add(getObiektFromWiersz(dane[i]));
		}
		return result;
	}

	private static TransportRozliczenie getObiektFromWiersz(String[] wiersz)
			throws UserShowException, IOException
	{
		return new TransportRozliczenie(wiersz);
	}

	/* Narazie nieużywane */
	@Override
	public ObiektZId pobierzObiektZBazy(ObiektWiersz wiersz)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dodaj(Object nowy) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void edytuj(Object stary, Object nowy) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void zmienWidocznosc(ObiektWiersz wiersz, boolean widoczny)
			throws SQLException
	{
		// TODO Auto-generated method stub

	}

}
