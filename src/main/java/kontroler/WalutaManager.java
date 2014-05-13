package kontroler;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.ObiektZId;
import model.SlownikElement;
import model.Waluta;
import utils.BazaDanych;
import utils.BazaStatementFunktor;
import utils.MojeUtils;
import utils.SqlUtils;
import utils.UserShowException;

public class WalutaManager
{
	static public ObiektZId pobierzZBazy(final long id,
			String slownikNazwaTabela)
	{
		try
		{
			String querySql = "SELECT wartosc FROM " + slownikNazwaTabela
					+ " WHERE id= " + SqlUtils.popraw(id);

			MojeUtils.println(querySql);

			return (ObiektZId) BazaDanych.getInstance().zapytanie(
			// sql do pobrania
					querySql,

					// operacja na pobranych danych
					new BazaStatementFunktor()
					{
						@Override
						public Object operacja(ResultSet result)
								throws Exception
						{
							// dodajemy do tabeli kolejne pobrane wiersze,
							// skladajace sie z 4 elementow
							return new SlownikElement(id, result.getString(1));
						}
					});
		} catch (Exception e)
		{
			MojeUtils.error(e);;
			return null;
		}
	}

	static public String[] pobierzWszystkieZBazy(String slownikNazwaTabela,
			boolean dolaczPustyElement)
	{
		try
		{
			final List<String> wynik = new ArrayList<String>();
			/*
			 * if ( dolaczPustyElement ) wynik.add(
			 * MagazynPanel.dowolnyProducent );
			 */

			String querySql = "SELECT wartosc FROM " + slownikNazwaTabela;

			MojeUtils.println(querySql);

			BazaDanych.getInstance().zapytanie(
			// sql do pobrania
					querySql,

					// operacja na pobranych danych
					new BazaStatementFunktor()
					{
						@Override
						public Object operacja(ResultSet result)
								throws Exception
						{
							while (result.next())
							{
								String wiersz = result.getString(1);
								wynik.add(wiersz);
							}
							return null;
						}
					});

			return wynik.toArray(new String[wynik.size()]);
		} catch (Exception e)
		{
			MojeUtils.error(e);;
			return null;
		}
	}

	static public Long pobierzIdZBazy(final String wartosc,
			String slownikNazwaTabela)
	{
		try
		{
			String querySql = "SELECT id FROM " + slownikNazwaTabela
					+ " WHERE wartosc= '" + SqlUtils.popraw(wartosc) + "'";

			MojeUtils.println(querySql);

			return (Long) BazaDanych.getInstance().zapytanie(querySql,

			new BazaStatementFunktor()
			{
				@Override
				public Object operacja(ResultSet result) throws Exception
				{
					try
					{
						return result.getLong(1);
					} catch (Exception przemilcz)
					{
						return null;
					}
				}
			});
		} catch (Exception e)
		{
			MojeUtils.error(e);;
			return null;
		}
	}

	static public String pobierzNazweZBazy(final Long id,
			String slownikNazwaTabela)
	{
		try
		{
			String querySql = "SELECT wartosc FROM " + slownikNazwaTabela
					+ " WHERE id= '" + SqlUtils.popraw(id + 1) + "'";

			MojeUtils.println(querySql);

			return (String) BazaDanych.getInstance().zapytanie(querySql,

			new BazaStatementFunktor()
			{
				@Override
				public Object operacja(ResultSet result) throws Exception
				{
					try
					{
						return result.getString(1);
					} catch (Exception przemilcz)
					{
						return null;
					}
				}
			});
		} catch (Exception e)
		{
			MojeUtils.error(e);;
			return null;
		}
	}

	static public String[] pobierzNazwyZBazy(long id) throws Exception
	{
		final List<String> wynik = new ArrayList<String>();
		String querySql = "SELECT wartosc FROM " + Waluta.tabelaWaluta
				+ " WHERE id=" + id;

		MojeUtils.println(querySql);
		BazaDanych.getInstance().zapytanie(querySql, new BazaStatementFunktor()
		{
			@Override
			public Object operacja(ResultSet result) throws Exception
			{
				while (result.next())
				{
					String wiersz = result.getString(1);
					wynik.add(wiersz);
				}
				return null;
			}
		});
		return wynik.toArray(new String[wynik.size()]);
	}

	/***
	 * 
	 * @param nazwa_waluty
	 * @return int - id waluty z bazy
	 * @throws UserShowException
	 * @throws IOException
	 */
	public static int konwertujWalute(String nazwa_waluty)
			throws UserShowException, IOException
	{
		switch (nazwa_waluty)
		{
		case "PLN":
			return 1;
		case "EUR":
			return 2;
		case "USD":
			return 3;
		case "UAH":
			return 4;
		default:
			throw new UserShowException("Nie rozpoznano waluty");
		}

	}
}
