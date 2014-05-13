package kontroler;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
				String wartosc = MojeUtils.formatujWartosc(result.getInt(3))
						+ " ";

				if (WalutaManager.pobierzNazweZBazy(result.getLong(4) - 1,
						Waluta.tabelaWaluta) != null)
					wartosc += (WalutaManager.pobierzNazweZBazy(
							result.getLong(4) - 1, Waluta.tabelaWaluta));
				else
					wartosc += "Nierozpoznano";

				String[] wiersz =
				{
						result.getString(1),
						MojeUtils.pobierzNrFakturyOdpowiadajacej(result
								.getInt(5)),// id faktury
						data_ksiegowania, wartosc, "" + result.getInt(6) };
				wynik.add(wiersz);
			}
			return wynik;
		}
	};

	@Override
	public String[][] pobierzWierszeZBazy(ObiektWyszukanieWarunki warunki)
			throws Exception
	{
		String querySql = "SELECT DISTINCT nr_faktury, data_ksiegowania, wartosc, waluta, id_faktury_odpowiadajacej, id FROM "
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
		MojeUtils.println("" + wynik.size());
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

	@Override
	public ObiektZId pobierzObiektZBazy(ObiektWiersz wiersz)
	{
		final int id = getIdFromWiersz(wiersz);
		String querySql = "SELECT DISTINCT nr_faktury, data_ksiegowania, wartosc, waluta, id_faktury_odpowiadajacej, id FROM "
				+ TransportRozliczenie.tableName;
		querySql += " WHERE id= " + SqlUtils.popraw(id);
		try
		{
			return (ObiektZId) BazaDanych.getInstance().zapytanie(querySql,
					new BazaStatementFunktor()
					{
						@Override
						public Object operacja(ResultSet result)
								throws Exception
						{
							try
							{
								return new TransportRozliczenie(result
										.getInt(6), DataUtils.parsujDate(result
										.getString(2)), result.getString(1),
										result.getInt(3), result.getLong(4),
										result.getInt(5));
							} catch (Exception e)
							{
								return null;
							}
						}
					});
		} catch (Exception e)
		{
			MojeUtils.error(e);
			return null;
		}
	}

	@Override
	public void dodaj(Object nowy) throws Exception
	{
		/* Narazie nieużywane */
		// TODO Auto-generated method stub

	}

	@Override
	public void edytuj(Object stary, Object nowy) throws Exception
	{
		TransportRozliczenie nowy_t = (TransportRozliczenie) nowy;
		TransportRozliczenie stary_t = (TransportRozliczenie) stary;
		String data_ksiegowania = new SimpleDateFormat("yyyy-mm-dd hh:mm",
				Locale.US).format(stary_t.data_ksiegowania);
		BazaDanych.getInstance().aktualizacja(
				"UPDATE " + TransportRozliczenie.tableName
						+ " set nr_faktury= '"
						+ SqlUtils.popraw(nowy_t.nr_faktury)
						+ "', data_ksiegowania= '"
						+ SqlUtils.popraw(data_ksiegowania) + "', wartosc= '"
						+ SqlUtils.popraw(nowy_t.wartosc) + "', waluta= '"
						+ SqlUtils.popraw(nowy_t.waluta)
						+ "', id_faktury_odpowiadajacej= '"
						+ SqlUtils.popraw(nowy_t.id_faktury_odpowiadajacej)
						+ "' WHERE id = " + SqlUtils.popraw(stary_t.id));

	}

	@Override
	public void zmienWidocznosc(ObiektWiersz wiersz, boolean widoczny)
			throws SQLException
	{
		/* narazie nie używana */
		// TODO Auto-generated method stub

	}

	public static int getIdFromWiersz(ObiektWiersz wiersz)
	{
		return Integer.parseInt(wiersz.wiersz[4]);
	}

}
