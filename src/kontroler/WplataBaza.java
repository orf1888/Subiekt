package kontroler;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Kontrachent;
import model.ObiektWiersz;
import model.ObiektZId;
import model.Waluta;
import model.Wplata;
import utils.BazaDanych;
import utils.BazaStatementFunktor;
import utils.DataUtils;
import utils.MojeUtils;
import utils.SqlUtils;

public class WplataBaza implements ObiektBazaManager
{

	@Override
	public String[][] pobierzWierszeZBazy(ObiektWyszukanieWarunki warunki)
			throws Exception
	{
		final List<String[]> wynik = new ArrayList<String[]>();

		String querySql = "SELECT data_wplaty , id_kontrachent , wartosc, waluta, id_wplata FROM "
				+ Wplata.tableName;

		querySql += warunki.generujWarunekWhere();

		System.out.println(querySql);

		BazaDanych.getInstance().zapytanie(querySql, new BazaStatementFunktor()
		{
			@Override
			public Object operacja(ResultSet result) throws Exception
			{
				/*
				 * dodajemy do tabeli kolejne pobrane wiersze,skladajace sie z 4
				 * elementow
				 */
				while (result.next())
				{
					String waluta = WalutaManager.pobierzNazweZBazy(
							(long) result.getInt(4) - 1, Waluta.tabelaWaluta);
					String nazwa_kontrachenta = KontrachentBaza
							.pobierzNazweZBazy(result.getInt(2));
					String[] wiersz =
					{
							DataUtils.formatujDate(result.getString(1)),
							nazwa_kontrachenta,
							""
									+ MojeUtils.utworzWartoscZlotowki(result
											.getInt(3)) + " " + waluta,
							"" + result.getInt(5) };
					wynik.add(wiersz);
				}
				return null;
			}
		});
		return wynik.toArray(new String[wynik.size()][]);
	}

	public static int getIdFromWiersz(ObiektWiersz wiersz)
	{
		return Integer.parseInt(wiersz.wiersz[3]);
	}

	@Override
	public ObiektZId pobierzObiektZBazy(ObiektWiersz wiersz)
	{
		int id = getIdFromWiersz(wiersz);
		String querySql = "SELECT data_wplaty , id_kontrachent , wartosc, waluta, id_wplata FROM "
				+ Wplata.tableName + " WHERE id_wplata= " + id;
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
								/*
								 * dodajemy do tabeli kolejne pobrane
								 * wiersze,skladajace sie z 4 elementow
								 */
								return new Wplata(result.getInt(5), DataUtils
										.parsujDate(result.getString(1)),
										result.getInt(2), result.getInt(3),
										result.getInt(4));
							} catch (Exception e)
							{
								MojeUtils.error(e);;
								return null;
							}
						}
					});
		} catch (Exception e)
		{
			MojeUtils.error(e);;
		}
		return null;
	}

	public static List<Wplata> pobierzObiektyZBazy(int id_kontrachent,
			int waluta) throws Exception
	{
		ObiektWyszukanieWarunki warunki = new ObiektWyszukanieWarunki(
				new Kontrachent());
		warunki.dodajWarunek(id_kontrachent, "id_kontrachent");
		warunki.dodajWarunek(waluta, "waluta");

		final List<Wplata> wynik = new ArrayList<Wplata>();

		String querySql = "SELECT data_wplaty , id_kontrachent , wartosc, waluta, id_wplata FROM "
				+ Wplata.tableName;

		querySql += warunki.generujWarunekWhere();

		BazaDanych.getInstance().zapytanie(querySql, new BazaStatementFunktor()
		{
			@Override
			public Object operacja(ResultSet result) throws Exception
			{
				try
				{
					while (result.next())
					{
						Wplata wp = new Wplata(result.getInt(5), DataUtils
								.parsujDate(result.getString(1)), result
								.getInt(2), result.getInt(3), result.getInt(4));
						wynik.add(wp);
					}
				} catch (Exception e)
				{
					MojeUtils.error(e);;
				}
				return null;
			}
		});

		return wynik;
	}

	@Override
	public void dodaj(Object nowy) throws Exception
	{
		Wplata nowaWplata = (Wplata) nowy;
		String data_wplaty = DataUtils.stringToDate_format
				.format(nowaWplata.data_wplaty);
		String insertSQL = "INSERT INTO  " + Wplata.tableName
				+ " (data_wplaty, id_kontrachent, wartosc, waluta"
				+ ") VALUES ('" + SqlUtils.popraw(data_wplaty) + "','"
				+ SqlUtils.popraw(nowaWplata.id_kontrachent) + "','"
				+ SqlUtils.popraw(nowaWplata.wartosc) + "','"
				+ SqlUtils.popraw(nowaWplata.waluta) + "')";
		System.err.println(insertSQL);
		int generatedId = BazaDanych.getInstance().wstaw(insertSQL);
		nowaWplata.id_wplata = generatedId;
		MojeUtils.println("" + generatedId);
	}

	@Override
	public void edytuj(Object stary, Object nowy) throws Exception
	{
		Wplata nowaWplata = (Wplata) nowy;
		String data_wplaty = DataUtils.stringToDate_format
				.format(nowaWplata.data_wplaty);
		String updateSQL = "UPDATE " + Wplata.tableName + " set data_wplaty= '"
				+ SqlUtils.popraw(data_wplaty) + "', id_kontrachent= '"
				+ SqlUtils.popraw(nowaWplata.id_kontrachent) + "', wartosc= '"
				+ SqlUtils.popraw(nowaWplata.wartosc) + "', waluta= '"
				+ SqlUtils.popraw(nowaWplata.waluta) + "' WHERE id_wplata = "
				+ SqlUtils.popraw(nowaWplata.id_wplata);
		System.out.println(updateSQL);
		BazaDanych.getInstance().aktualizacja(updateSQL);
	}

	@Override
	public void zmienWidocznosc(ObiektWiersz wiersz, boolean widoczny)
			throws Exception
	{
		return;
	}
}
