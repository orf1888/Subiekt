package kontroler;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.ObiektZId;
import model.SlownikElement;
import utils.BazaDanych;
import utils.BazaStatementFunktor;
import utils.MojeUtils;
import utils.SqlUtils;
import widok.MagazynPanel;

public class SlownikManager
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
			MojeUtils.error(e);
			return null;
		}
	}

	static public String[] pobierzWszystkieZBazy(String slownikNazwaTabela,
			boolean dolaczPustyElement)
	{
		try
		{
			final List<String> wynik = new ArrayList<String>();
			if (dolaczPustyElement)
				wynik.add(MagazynPanel.dowolnyProducent);

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
			MojeUtils.error(e);
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
			MojeUtils.error(e);
			return null;
		}
	}

	static public String[] pobierzNazwyZBazy(long id) throws Exception
	{
		final List<String> wynik = new ArrayList<String>();
		String querySql = "SELECT wartosc FROM "
				+ SlownikElement.tabelaProducent + " WHERE id=" + id;

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

	public static boolean dodaj(String nazwaProducenta) throws Exception
	{
		if (nazwaProducenta.trim().isEmpty())
		{
			MojeUtils.showMsg("Nie podano nazwy producenta");
			return false;
		}
		if (pobierzIdZBazy(nazwaProducenta, SlownikElement.tabelaProducent) == null)
		{
			SlownikElement nowyProducent = new SlownikElement();
			nowyProducent.wartosc = nazwaProducenta;
			// mozna dodawać bo nazwa unikalna

			int generatedId = BazaDanych.getInstance().wstaw(
					"INSERT INTO  " + SlownikElement.tabelaProducent
							+ " (wartosc" + ") VALUES ('"
							+ SqlUtils.popraw(nowyProducent.wartosc) + "')");

			// MojeUtils.println( generatedId );

			nowyProducent.id = generatedId;
			return true;
		} else
		{
			MojeUtils.showError("Znaleziono już producenta o podanej nazwie!");
			return false;
		}
	}

	public static boolean edytuj(String nazwaProducenta,
			String nowaNazwaProducenta) throws Exception
	{
		if (nowaNazwaProducenta.trim().isEmpty())
		{
			MojeUtils.showMsg("Nie podano nazwy producenta!");
			return false;
		}
		if (nowaNazwaProducenta.equals(nazwaProducenta))
			return false;
		if (pobierzIdZBazy(nowaNazwaProducenta, SlownikElement.tabelaProducent) == null)
		{
			SlownikElement nowyProducent = new SlownikElement();
			nowyProducent.id = pobierzIdZBazy(nazwaProducenta,
					SlownikElement.tabelaProducent);
			nowyProducent.wartosc = nowaNazwaProducenta;
			// mozna dodawać bo nazwa unikalna
			BazaDanych.getInstance().aktualizacja(
					"UPDATE " + SlownikElement.tabelaProducent
							+ " set wartosc= '"
							+ SqlUtils.popraw(nowyProducent.wartosc)
							+ "' WHERE id=" + nowyProducent.id);
			return true;

		} else
		{
			MojeUtils.showError("Znaleziono już producenta o podanej nazwie!");
			return false;
		}
	}
}
