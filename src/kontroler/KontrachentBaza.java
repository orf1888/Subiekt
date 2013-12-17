package kontroler;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Kontrachent;
import model.ObiektWiersz;
import model.ObiektZId;
import utils.BazaDanych;
import utils.BazaStatementFunktor;
import utils.MojeUtils;
import utils.SqlUtils;
import utils.UserShowException;

public class KontrachentBaza implements ObiektBazaManager
{

	private static final String SELECT_QUERY_SQL = "SELECT nazwa, miasto, kod_pocztowy, ulica, cena_mnoznik, widoczny, nip, regon, nr_konta, kredyt_kupiecki, id_kontrachent FROM "
			+ Kontrachent.tableName;

	@Override
	public String[][] pobierzWierszeZBazy(ObiektWyszukanieWarunki warunki)
			throws SQLException
	{
		final List<String[]> wynik = new ArrayList<String[]>();

		String querySql = "SELECT nazwa,miasto,nip,id_kontrachent FROM "
				+ Kontrachent.tableName;

		querySql += warunki.generujWarunekWhere();

		MojeUtils.println(querySql);

		BazaDanych.getInstance().zapytanie(querySql, new BazaStatementFunktor()
		{
			@Override
			public Object operacja(ResultSet result) throws SQLException
			{
				/*
				 * dodajemy do tabeli kolejne pobrane wiersze,skladajace sie z 4
				 * elementow
				 */
				while (result.next())
				{
					String[] wiersz =
					{ result.getString(1), result.getString(2),
							result.getString(3), "" + result.getInt(4) };
					wynik.add(wiersz);
				}
				return null;
			}
		});
		return wynik.toArray(new String[wynik.size()][]);
	}

	@Override
	public ObiektZId pobierzObiektZBazy(final ObiektWiersz wiersz)
	{
		final int id = getIdFromWiersz(wiersz);
		return pobierzObiektZBazy(id);
	}

	public static ObiektZId pobierzObiektZBazy(final int id)
	{
		String querySql = SELECT_QUERY_SQL + " WHERE id_kontrachent= "
				+ SqlUtils.popraw(id);
		return pobierzObiektZBazyCore(querySql);
	}

	public static ObiektZId pobierzObiektZBazy(final String nazwa)
	{
		String querySql = SELECT_QUERY_SQL + " WHERE nazwa= '"
				+ SqlUtils.popraw(nazwa) + "'";
		return pobierzObiektZBazyCore(querySql);
	}

	private static ObiektZId pobierzObiektZBazyCore(String querySql)
	{
		try
		{
			MojeUtils.println(querySql);

			return (ObiektZId) BazaDanych.getInstance().zapytanie(querySql,
					new BazaStatementFunktor()
					{
						@Override
						public Object operacja(ResultSet result)
								throws SQLException
						{
							try
							{
								/*
								 * dodajemy do tabeli kolejne pobrane
								 * wiersze,skladajace sie z 4 elementow
								 */
								return new Kontrachent(result.getString(1),
										result.getString(2), result
												.getString(3), result
												.getString(4),
										result.getInt(5), result.getBoolean(6),
										result.getString(7), result
												.getString(8), result
												.getString(9), result
												.getInt(10), result.getInt(11));
							} catch (Exception e)
							{
								return null;
							}
						}
					});
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void dodaj(Object nowy) throws Exception
	{
		Kontrachent nowyKontr = (Kontrachent) nowy;

		ObiektWyszukanieWarunki warunki = new ObiektWyszukanieWarunki(
				new Kontrachent());
		warunki.dodajWarunek(nowyKontr.nip, "nip");
		warunki.dodajWarunek(1, "widoczny");

		String[][] znalezioneWiersze = pobierzWierszeZBazy(warunki);

		if (znalezioneWiersze.length == 0)
		{
			/* mozna edytować bo nip unikalny */
			int generatedId = BazaDanych
					.getInstance()
					.wstaw("INSERT INTO  "
							+ Kontrachent.tableName
							+ " (nazwa, miasto, kod_pocztowy, ulica, cena_mnoznik, widoczny, nip, regon, nr_konta, kredyt_kupiecki"
							+ ") VALUES ('" + SqlUtils.popraw(nowyKontr.nazwa)
							+ "','" + SqlUtils.popraw(nowyKontr.miasto) + "','"
							+ SqlUtils.popraw(nowyKontr.kod_pocztowy) + "','"
							+ SqlUtils.popraw(nowyKontr.ulica) + "',"
							+ SqlUtils.popraw(nowyKontr._cena_mnoznik) + ","
							+ SqlUtils.popraw((nowyKontr.widoczny ? 1 : 0))
							+ ",'" + SqlUtils.popraw(nowyKontr.nip) + "','"
							+ SqlUtils.popraw(nowyKontr.regon) + "','"
							+ SqlUtils.popraw(nowyKontr.nrKonta) + "','"
							+ SqlUtils.popraw(nowyKontr.kredyt_kupiecki) + "')");

			MojeUtils.println("" + generatedId);

			nowyKontr.id_kontrachent = generatedId;
		} else
			throw new UserShowException(
					"Znaleziono już kontrachenta o takim NIP");
	}

	@Override
	public void edytuj(Object stary, Object nowy) throws Exception
	{
		Kontrachent kontrachent = (Kontrachent) stary;
		Kontrachent nowyKontrachent = (Kontrachent) nowy;
		if (MojeUtils.equals(nowyKontrachent.nip, kontrachent.nip))
		{
			/* NIP sie nie zmienil - to jedyny warunek, jest OK */
		} else
		{
			/* NIP sie zmienil - sprawdz czy taki sam juz nie istnieje w bazie? */
			ObiektWyszukanieWarunki warunki = new ObiektWyszukanieWarunki(
					new Kontrachent());
			warunki.dodajWarunek(nowyKontrachent.nip, "nip");
			warunki.dodajWarunek(1, "widoczny");
			String[][] znalezioneWiersze = pobierzWierszeZBazy(warunki);
			if (znalezioneWiersze.length == 0)
			{
				/* mozna edytować bo nip nie wystepuje w bazie */
			} else
				throw new UserShowException("Znaleziono taki sam NIP: "
						+ getNipFromWiersz(znalezioneWiersze[0]) + ", nazwa: "
						+ getNazwaFromWiersz(znalezioneWiersze[0]));
		}

		BazaDanych.getInstance().aktualizacja(
				"UPDATE " + Kontrachent.tableName + " set nazwa= '"
						+ SqlUtils.popraw(nowyKontrachent.nazwa)
						+ "', miasto= '"
						+ SqlUtils.popraw(nowyKontrachent.miasto)
						+ "', kod_pocztowy= '"
						+ SqlUtils.popraw(nowyKontrachent.kod_pocztowy)
						+ "', ulica= '"
						+ SqlUtils.popraw(nowyKontrachent.ulica)
						+ "', cena_mnoznik= '"
						+ SqlUtils.popraw(nowyKontrachent._cena_mnoznik)
						+ "', nip = '" + SqlUtils.popraw(nowyKontrachent.nip)
						+ "', regon = '"
						+ SqlUtils.popraw(nowyKontrachent.regon)
						+ "', nr_konta = '"
						+ SqlUtils.popraw(nowyKontrachent.nrKonta)
						+ "', kredyt_kupiecki = '"
						+ SqlUtils.popraw(nowyKontrachent.kredyt_kupiecki)
						+ "' WHERE id_kontrachent = "
						+ SqlUtils.popraw(kontrachent.id_kontrachent));
	}

	@Override
	public void zmienWidocznosc(ObiektWiersz wiersz, boolean widoczny)
			throws SQLException
	{
		BazaDanych.getInstance().aktualizacja(
				"UPDATE " + Kontrachent.tableName + " set widoczny = "
						+ (widoczny ? 1 : 0) + " WHERE id_kontrachent = "
						+ SqlUtils.popraw(getIdFromWiersz(wiersz)));
	}

	public static String getNipFromWiersz(String[] wiersz)
	{
		return wiersz[2];
	}

	public static String getNazwaFromWiersz(String[] wiersz)
	{
		return wiersz[0];
	}

	public static int getIdFromWiersz(ObiektWiersz wiersz)
	{
		return Integer.parseInt(wiersz.wiersz[3]);
	}

	public static String pobierzNazweZBazy(int id) throws SQLException
	{
		return (String) BazaDanych.getInstance().zapytanie(
				/* sql do pobrania */
				"SELECT nazwa FROM " + Kontrachent.tableName
						+ " WHERE id_kontrachent= " + id,
				/* operacja na pobranych danych */
				new BazaStatementFunktor()
				{
					@Override
					public Object operacja(ResultSet result)
							throws SQLException
					{
						try
						{
							String wynik = result.getString(1);
							MojeUtils.println(wynik);
							return wynik;
						} catch (Exception e)
						{
							return "Brak!";
						}
					}
				});
	}

	public static String[] pobierzWszystkieNazwyZBazy() throws SQLException
	{
		return (String[]) BazaDanych.getInstance().zapytanie(
				"SELECT nazwa FROM " + Kontrachent.tableName
						+ " WHERE widoczny= 1", new BazaStatementFunktor()
				{
					@Override
					public Object operacja(ResultSet result)
							throws SQLException
					{
						List<String> wynik = new ArrayList<String>();
						while (result.next())
						{
							wynik.add(result.getString(1));
						}
						return wynik.toArray(new String[wynik.size()]);
					}
				});
	}
}