package kontroler;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Faktura;
import model.Kontrachent;
import model.ObiektWiersz;
import model.ObiektZId;
import model.Wplata;
import utils.BazaDanych;
import utils.BazaStatementFunktor;
import utils.MojeUtils;
import utils.SqlUtils;
import utils.UserShowException;

public class KontrachentBaza implements ObiektBazaManager
{

	private static final String SELECT_QUERY_SQL = "SELECT nazwa, miasto, kod_pocztowy,"
			+ " ulica, cena_mnoznik, widoczny, nip, regon, nr_konta,"
			+ " kredyt_kupiecki, id_kontrachent, dlug_pln, dlug_eur,"
			+ " dlug_usd, dlug_uah,nadplata_pln, nadplata_eur, nadplata_usd, nadplata_uah FROM "
			+ Kontrachent.tableName;

	@Override
	public String[][] pobierzWierszeZBazy(ObiektWyszukanieWarunki warunki)
			throws Exception
	{
		final List<String[]> wynik = new ArrayList<String[]>();

		String querySql = "SELECT nazwa,miasto,nip,id_kontrachent FROM "
				+ Kontrachent.tableName;

		querySql += warunki.generujWarunekWhere();

		MojeUtils.println(querySql);

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
								throws Exception
						{
							try
							{
								return new Kontrachent(result.getString(1),
										result.getString(2), result
												.getString(3), result
												.getString(4),
										result.getInt(5), result.getBoolean(6),
										result.getString(7), result
												.getString(8), result
												.getString(9), result
												.getInt(10), result.getInt(11),
										result.getInt(12), result.getInt(13),
										result.getInt(14), result.getInt(15),
										result.getInt(16), result.getInt(17),
										result.getInt(18), result.getInt(19));
							} catch (Exception e)
							{
								MojeUtils.error(e);
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
							+ " (nazwa, miasto, kod_pocztowy, ulica, cena_mnoznik, widoczny, nip, regon, nr_konta,"
							+ " kredyt_kupiecki, dlug_pln, dlug_eur, dlug_usd, dlug_uah,nadplata_pln, nadplata_eur,"
							+ " nadplata_usd, nadplata_uah" + ") VALUES ('"
							+ SqlUtils.popraw(nowyKontr.nazwa) + "','"
							+ SqlUtils.popraw(nowyKontr.miasto) + "','"
							+ SqlUtils.popraw(nowyKontr.kod_pocztowy) + "','"
							+ SqlUtils.popraw(nowyKontr.ulica) + "',"
							+ SqlUtils.popraw(nowyKontr._cena_mnoznik) + ","
							+ SqlUtils.popraw((nowyKontr.widoczny ? 1 : 0))
							+ ",'" + SqlUtils.popraw(nowyKontr.nip) + "','"
							+ SqlUtils.popraw(nowyKontr.regon) + "','"
							+ SqlUtils.popraw(nowyKontr.nrKonta) + "','"
							+ SqlUtils.popraw(nowyKontr.kredyt_kupiecki)
							+ "','" + SqlUtils.popraw(nowyKontr.dlug_pln)
							+ "','" + SqlUtils.popraw(nowyKontr.dlug_eur)
							+ "','" + SqlUtils.popraw(nowyKontr.dlug_usd)
							+ "','" + SqlUtils.popraw(nowyKontr.dlug_uah)
							+ "','" + SqlUtils.popraw(nowyKontr.nadplata_pln)
							+ "','" + SqlUtils.popraw(nowyKontr.nadplata_eur)
							+ "','" + SqlUtils.popraw(nowyKontr.nadplata_usd)
							+ "','" + SqlUtils.popraw(nowyKontr.nadplata_uah)
							+ "')");

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

		String updateSQL = "UPDATE " + Kontrachent.tableName + " set nazwa= '"
				+ SqlUtils.popraw(nowyKontrachent.nazwa) + "', miasto= '"
				+ SqlUtils.popraw(nowyKontrachent.miasto)
				+ "', kod_pocztowy= '"
				+ SqlUtils.popraw(nowyKontrachent.kod_pocztowy) + "', ulica= '"
				+ SqlUtils.popraw(nowyKontrachent.ulica) + "', cena_mnoznik= '"
				+ SqlUtils.popraw(nowyKontrachent._cena_mnoznik) + "', nip = '"
				+ SqlUtils.popraw(nowyKontrachent.nip) + "', regon = '"
				+ SqlUtils.popraw(nowyKontrachent.regon) + "', nr_konta = '"
				+ SqlUtils.popraw(nowyKontrachent.nrKonta)
				+ "', kredyt_kupiecki = '"
				+ SqlUtils.popraw(nowyKontrachent.kredyt_kupiecki);
		/* dług walutowy */
		if (nowyKontrachent.dlug_pln != null)
			updateSQL += "', dlug_pln = '"
					+ SqlUtils.popraw(nowyKontrachent.dlug_pln);
		if (nowyKontrachent.dlug_eur != null)
			updateSQL += "', dlug_eur = '"
					+ SqlUtils.popraw(nowyKontrachent.dlug_eur);
		if (nowyKontrachent.dlug_usd != null)
			updateSQL += "', dlug_usd = '"
					+ SqlUtils.popraw(nowyKontrachent.dlug_usd);
		if (nowyKontrachent.dlug_uah != null)
			updateSQL += "', dlug_uah = '"
					+ SqlUtils.popraw(nowyKontrachent.dlug_uah);
		/* środki nierozliczone z wpłaty */
		if (nowyKontrachent.nadplata_pln != null)
			updateSQL += "', nadplata_pln = '"
					+ SqlUtils.popraw(nowyKontrachent.nadplata_pln);
		if (nowyKontrachent.nadplata_eur != null)
			updateSQL += "', nadplata_eur = '"
					+ SqlUtils.popraw(nowyKontrachent.nadplata_eur);
		if (nowyKontrachent.nadplata_usd != null)
			updateSQL += "', nadplata_usd = '"
					+ SqlUtils.popraw(nowyKontrachent.nadplata_usd);
		if (nowyKontrachent.nadplata_uah != null)
			updateSQL += "', nadplata_uah = '"
					+ SqlUtils.popraw(nowyKontrachent.nadplata_uah);
		updateSQL += "' WHERE id_kontrachent = "
				+ SqlUtils.popraw(kontrachent.id_kontrachent);
		MojeUtils.println(updateSQL);
		BazaDanych.getInstance().aktualizacja(updateSQL);
	}

	@Override
	public void zmienWidocznosc(ObiektWiersz wiersz, boolean widoczny)
			throws Exception
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

	public static String pobierzNazweZBazy(int id) throws Exception
	{
		return (String) BazaDanych.getInstance().zapytanie(
				/* sql do pobrania */
				"SELECT nazwa FROM " + Kontrachent.tableName
						+ " WHERE id_kontrachent= " + id,
				/* operacja na pobranych danych */
				new BazaStatementFunktor()
				{
					@Override
					public Object operacja(ResultSet result) throws Exception
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

	public static String[] pobierzWszystkieNazwyZBazy() throws Exception
	{
		return (String[]) BazaDanych.getInstance().zapytanie(
				"SELECT nazwa FROM " + Kontrachent.tableName
						+ " WHERE widoczny= 1", new BazaStatementFunktor()
				{
					@Override
					public Object operacja(ResultSet result) throws Exception
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

	public void dodajDoDlugu(Faktura faktura, Long waluta) throws Exception
	{
		switch (waluta.intValue())
		{
		case 1:
		{
			faktura.kontrahent.dlug_pln += faktura.wartosc_z_narzutem;
			break;
		}
		case 2:
		{
			faktura.kontrahent.dlug_eur += faktura.wartosc_z_narzutem;
			break;
		}
		case 3:
		{
			faktura.kontrahent.dlug_usd += faktura.wartosc_z_narzutem;
			break;
		}
		case 4:
		{
			faktura.kontrahent.dlug_uah += faktura.wartosc_z_narzutem;
			break;
		}
		}
		edytuj(faktura.kontrahent, faktura.kontrahent);
	}

	public void odejmijOdDlugu(Faktura faktura, Long waluta) throws Exception
	{
		int wartosc = 0;
		switch (waluta.intValue())
		{
		case 1:
		{
			wartosc = faktura.kontrahent.dlug_pln - faktura.wartosc_z_narzutem;
			if (wartosc < 0)
			{
				faktura.kontrahent.dlug_pln = 0;
				faktura.kontrahent.nadplata_pln += -wartosc;
			} else
				faktura.kontrahent.dlug_pln -= faktura.wartosc_z_narzutem;
			break;
		}
		case 2:
		{
			wartosc = faktura.kontrahent.dlug_eur - faktura.wartosc_z_narzutem;
			if (wartosc < 0)
			{
				faktura.kontrahent.dlug_eur = 0;
				faktura.kontrahent.nadplata_eur += -wartosc;
			} else
				faktura.kontrahent.dlug_eur -= faktura.wartosc_z_narzutem;
			break;
		}
		case 3:
		{
			wartosc = faktura.kontrahent.dlug_usd - faktura.wartosc_z_narzutem;
			if (wartosc < 0)
			{
				faktura.kontrahent.dlug_usd = 0;
				faktura.kontrahent.nadplata_usd += -wartosc;
			} else
				faktura.kontrahent.dlug_usd -= faktura.wartosc_z_narzutem;
			break;
		}
		case 4:
		{
			wartosc = faktura.kontrahent.dlug_uah - faktura.wartosc_z_narzutem;
			if (wartosc < 0)
			{
				faktura.kontrahent.dlug_uah = 0;
				faktura.kontrahent.nadplata_uah += -wartosc;
			} else
				faktura.kontrahent.dlug_uah -= faktura.wartosc_z_narzutem;
			break;
		}
		}
		edytuj(faktura.kontrahent, faktura.kontrahent);
	}

	public void odejmijOdDlugu(Wplata wplata, Long waluta) throws Exception
	{
		Kontrachent tmp_kontrachent = (Kontrachent) KontrachentBaza
				.pobierzObiektZBazy(wplata.id_kontrachent);
		int wartosc;
		switch (waluta.intValue())
		{
		case 1:
		{
			wartosc = tmp_kontrachent.dlug_pln - wplata.wartosc;
			if (wartosc < 0)
			{
				tmp_kontrachent.dlug_pln = 0;
				tmp_kontrachent.nadplata_pln += -wartosc;
			} else
			{
				tmp_kontrachent.dlug_pln -= wplata.wartosc;
				tmp_kontrachent.nadplata_pln = tmp_kontrachent.dlug_pln
						- wplata.wartosc;
			}
			break;
		}
		case 2:
		{
			wartosc = tmp_kontrachent.dlug_eur - wplata.wartosc;
			if (wartosc < 0)
			{
				tmp_kontrachent.dlug_eur = 0;
				tmp_kontrachent.nadplata_eur += -wartosc;
			} else
				tmp_kontrachent.dlug_eur -= wplata.wartosc;
			break;
		}
		case 3:
		{
			wartosc = tmp_kontrachent.dlug_usd - wplata.wartosc;
			if (wartosc < 0)
			{
				tmp_kontrachent.dlug_usd = 0;
				tmp_kontrachent.nadplata_usd += -wartosc;
			} else
				tmp_kontrachent.dlug_usd -= wplata.wartosc;;
			break;
		}
		case 4:
		{
			wartosc = tmp_kontrachent.dlug_uah - wplata.wartosc;
			if (wartosc < 0)
			{
				tmp_kontrachent.dlug_uah = 0;
				tmp_kontrachent.nadplata_uah += -wartosc;
			} else
				tmp_kontrachent.dlug_uah -= wplata.wartosc;;
			break;
		}
		}
		edytuj(tmp_kontrachent, tmp_kontrachent);
	}

	public static int getDlugByWaluta(int waluta, Kontrachent tmp_kontrachent)
	{
		switch (waluta)
		{
		case 1:
			return tmp_kontrachent.dlug_pln;
		case 2:
			return tmp_kontrachent.dlug_eur;
		case 3:
			return tmp_kontrachent.dlug_usd;
		case 4:
			return tmp_kontrachent.dlug_uah;
		default:
			MojeUtils.showError("Nipoprawna waluta!");
			return 0;
		}
	}
}