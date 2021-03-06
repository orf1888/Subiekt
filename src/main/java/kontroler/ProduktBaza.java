package kontroler;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Kontrachent;
import model.ObiektWiersz;
import model.ObiektZId;
import model.produkt.Produkt;
import model.produkt.ProduktNaSztuki;
import model.produkt.ProduktWFakturze;
import model.produkt.ProduktWWysylce;
import utils.BazaDanych;
import utils.BazaStatementFunktor;
import utils.MojeUtils;
import utils.SqlUtils;
import utils.UserShowException;
import widok.MagazynPanel;

/**
 * @author oRF1888
 * 
 */
public class ProduktBaza implements ObiektBazaManager
{
	private MagazynPanel panel;

	public void setMagazynPanel(MagazynPanel panel)
	{
		this.panel = panel;
	}

	private static ProduktBaza instance = new ProduktBaza();

	public static ProduktBaza instance()
	{
		return instance;
	}

	// prywatny konstruktor - uzywac instance()!
	private ProduktBaza() {}

	public static void dodajListeProduktow(List<ProduktWFakturze> produkty,
			int fakturaId) throws Exception
	{
		MojeUtils.println("Dodaje " + produkty.size()
				+ " produktow do faktury o ID " + fakturaId);
		for (ProduktWFakturze produkt : produkty)
		{
			BazaDanych
					.getInstance()
					.wstaw("INSERT INTO  "
							+ Produkt.tableLaczacaFaktura
							+ " (id_produkt, id_faktura, ilosc_produktu, cena_jednostkowa, lp, korekta, wartosc_po_korekcie, rabat)"
							+ " VALUES ("
							+ SqlUtils.popraw(produkt.produkt.id_produkt) + ","
							+ SqlUtils.popraw(fakturaId) + ","
							+ SqlUtils.popraw(produkt.ilosc_produktu) + ","
							+ SqlUtils.popraw(produkt._cena_jednostkowa) + ","
							+ SqlUtils.popraw(produkt.lp) + ","
							+ SqlUtils.popraw(produkt.korekta) + ","
							+ SqlUtils.popraw(produkt.wartoscPoKorekcie) + ","
							+ SqlUtils.popraw(rabatDoubleToInt(produkt.rabat))
							+ ")");
		}
	}

	@SuppressWarnings("unchecked")
	public static List<ProduktWFakturze> pobierzDlaFaktury(int id_faktury,
			final boolean korekta)
	{
		try
		{
			String querySql = "SELECT P.kod, P.nazwa, P.ilosc, P.widoczny, P.id_producenta, P.cena_zakupu, P.id_produkt, "
					+ " P_F.lp, P_F.cena_jednostkowa, P_F.ilosc_produktu, P_F.wartosc_po_korekcie, P_F.rabat FROM "
					+ Produkt.tableName
					+ " P JOIN "
					+ Produkt.tableLaczacaFaktura
					+ " P_F ON P.id_produkt=P_F.id_produkt WHERE P_F.id_faktura= "
					+ SqlUtils.popraw(id_faktury)
					+ " AND P_F.korekta= "
					+ SqlUtils.popraw(korekta);

			MojeUtils.println(querySql);

			return (List<ProduktWFakturze>) BazaDanych.getInstance().zapytanie(
			// sql do pobrania
					querySql,

					// operacja na pobranych danych
					new BazaStatementFunktor()
					{
						@Override
						public Object operacja(ResultSet result)
								throws Exception
						{
							List<ProduktWFakturze> produkty = new ArrayList<ProduktWFakturze>();
							while (result.next())
							{
								// result.getArray( columnIndex )
								// String kod, String nazwa, int ilosc, boolean
								// widoczny, int id_producenta, int cena_zakupu,
								// int id_produkt)
								Produkt produktSlownik = new Produkt(result
										.getString(1), result.getString(2),
										result.getInt(3), result.getBoolean(4),
										result.getInt(5), result.getInt(6),
										result.getInt(7));
								int lp = result.getInt(8);
								int cena = result.getInt(9);
								int ilosc = result.getInt(10);
								int wartoscPoKorekcie = result.getInt(11);
								double rabat = rabatIntToDouble(result
										.getInt(12));
								produkty.add(new ProduktWFakturze(lp, cena,
										ilosc, produktSlownik, korekta,
										wartoscPoKorekcie, rabat));
							}
							return produkty;
						}
					});
		} catch (Exception e)
		{
			MojeUtils.println("pobierzDlaFaktury error");
			MojeUtils.error(e);
			return null;
		}
	}

	@Override
	public String[][] pobierzWierszeZBazy(ObiektWyszukanieWarunki warunki)
			throws Exception
	{
		final List<String[]> wynik = new ArrayList<String[]>();

		// String querySql =
		// "SELECT kod, nazwa, ilosc, id_produkt, cena_zakupu FROM "
		// + Produkt.tableName;
		String querySql = "SELECT kod, nazwa, ilosc, id_produkt FROM "
				+ Produkt.tableName;
		querySql += warunki.generujWarunekWhere();

		MojeUtils.println(querySql);

		BazaDanych.getInstance().zapytanie(
		// sql do pobrania
				querySql,

				// operacja na pobranych danych
				new BazaStatementFunktor()
				{
					@Override
					public Object operacja(ResultSet result) throws Exception
					{

						// dodajemy do tabeli kolejne pobrane wiersze
						while (result.next())
						{
							String[] wiersz =
							{ result.getString(1), result.getString(2),
									"" + result.getInt(3),
									"" + result.getInt(4)
							// "" + result.getInt(5)
							};
							wynik.add(wiersz);
						}
						return null;
					}
				});

		// if ( wynik.isEmpty() )
		// wynik.add( new String[] { "Brak wyników" } );

		return wynik.toArray(new String[wynik.size()][]);
	}

	@Override
	public ObiektZId pobierzObiektZBazy(final ObiektWiersz wiersz)

	{
		try
		{
			final int id = getIdFromWiersz(wiersz);
			String querySql = "SELECT kod, nazwa, ilosc, widoczny, id_producenta, cena_zakupu FROM "
					+ Produkt.tableName
					+ " WHERE id_produkt= "
					+ SqlUtils.popraw(id);

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
							return new Produkt(result.getString(1), result
									.getString(2), result.getInt(3), result
									.getBoolean(4), result.getInt(5), result
									.getInt(6), id);
						}
					});
		} catch (Exception e)
		{
			MojeUtils.error(e);
			return null;
		}
	}

	public static ObiektZId pobierzObiektZBazyPoKodzie(final String kod)
	{
		try
		{
			String querySql = "SELECT id_produkt, nazwa, ilosc, widoczny, id_producenta, cena_zakupu FROM "
					+ Produkt.tableName
					+ " WHERE kod= '"
					+ SqlUtils.popraw(kod) + "'";

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
							try
							{
								// dodajemy do tabeli kolejne pobrane wiersze,
								// skladajace sie z 4 elementow
								return new Produkt(kod, result.getString(2),
										result.getInt(3), result.getBoolean(4),
										result.getInt(5), result.getInt(6),
										result.getInt(1));
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
		Produkt nowyProd = (Produkt) nowy;
		ObiektWyszukanieWarunki warunki = new ObiektWyszukanieWarunki(
				new Produkt());
		warunki.dodajWarunek(nowyProd.kod, "kod");
		warunki.dodajWarunek(1, "widoczny");
		/*
		 * ProduktWyszukanieWarunki warunki = (ProduktWyszukanieWarunki)
		 * tworzWybrane( null, nowyProd.kod, Globals.Widoczny,
		 * Globals.SzukajRownaSie );
		 */
		String[][] znalezioneWiersze = pobierzWierszeZBazy(warunki);

		if (znalezioneWiersze.length == 0)
		{
			// mozna edytować bo nip unikalny
			int generatedId = BazaDanych
					.getInstance()
					.wstaw("INSERT INTO  "
							+ Produkt.tableName
							+ " (kod, nazwa, ilosc, widoczny, id_producenta, cena_zakupu"
							+ ") VALUES ('" + SqlUtils.popraw(nowyProd.kod)
							+ "','" + SqlUtils.popraw(nowyProd.nazwa) + "',"
							+ SqlUtils.popraw(nowyProd.ilosc) + ","
							+ SqlUtils.popraw((nowyProd.widoczny ? 1 : 0))
							+ "," + SqlUtils.popraw(nowyProd.id_producenta)
							+ "," + SqlUtils.popraw(nowyProd.cena_zakupu) + ")");

			// MojeUtils.println( generatedId );

			nowyProd.id_produkt = generatedId;
		} else
			throw new UserShowException("Znaleziono już towar o takim kodzie!");
	}

	@Override
	public void edytuj(Object stary, Object nowy) throws Exception
	{
		Produkt produkt = (Produkt) stary;
		Produkt nowyProdukt = (Produkt) nowy;
		if (MojeUtils.equals(nowyProdukt.kod, produkt.kod))
		{
			// kod sie nie zmienil - to jedyny warunek, jest OK
		} else
		{
			// kod sie zmienil - sprawdz czy taki sam juz nie istnieje w bazie?
			ObiektWyszukanieWarunki warunki = new ObiektWyszukanieWarunki(
					new Kontrachent());
			warunki.dodajWarunek(nowyProdukt.kod, "kod");
			warunki.dodajWarunek(1, "widoczny");

			/*
			 * ProduktWyszukanieWarunki warunki = (ProduktWyszukanieWarunki)
			 * tworzWybrane( null, nowyProdukt.kod, Globals.Widoczny,
			 * Globals.SzukajRownaSie );
			 */
			String[][] znalezioneWiersze = pobierzWierszeZBazy(warunki);
			if (znalezioneWiersze.length == 0)
			{
				// mozna edytować bo kod nie wystepuje w bazie
			} else
				throw new UserShowException("Znaleziono taki sam kod: "
						+ getKodFromWiersz(znalezioneWiersze[0]) + ", nazwa: "
						+ getNazwaFromWiersz(znalezioneWiersze[0]));
		}

		BazaDanych.getInstance().aktualizacja(
				"UPDATE " + Produkt.tableName + " set kod= '"
						+ SqlUtils.popraw(nowyProdukt.kod) + "', nazwa= '"
						+ SqlUtils.popraw(nowyProdukt.nazwa) + "', ilosc= '"
						+ SqlUtils.popraw(nowyProdukt.ilosc)
						+ "', id_producenta= '"
						+ SqlUtils.popraw(nowyProdukt.id_producenta)
						+ "', cena_zakupu = '"
						+ SqlUtils.popraw(nowyProdukt.cena_zakupu)
						+ "' WHERE id_produkt = "
						+ SqlUtils.popraw(produkt.id_produkt));
	}

	@Override
	public void zmienWidocznosc(ObiektWiersz wiersz, boolean widoczny)
			throws Exception
	{
		BazaDanych.getInstance().aktualizacja(
				"UPDATE " + Produkt.tableName + " set widoczny = "
						+ (widoczny ? 1 : 0) + " WHERE id_produkt = "
						+ SqlUtils.popraw(getIdFromWiersz(wiersz)));
	}

	public static String getKodFromWiersz(String[] wiersz)
	{
		return wiersz[0];
	}

	public static String getNazwaFromWiersz(String[] wiersz)
	{
		return wiersz[1];
	}

	public static int getIdFromWiersz(ObiektWiersz wiersz)
	{
		return Integer.parseInt(wiersz.wiersz[3]);
	}

	public static ObiektZId pobierzObiektZBazy(String id)
	{
		return pobierzObiektZBazy(Integer.parseInt(id));
	}

	public static ObiektZId pobierzObiektZBazy(final int id)
	{
		try
		{
			String querySql = "SELECT kod, nazwa, ilosc, widoczny, id_producenta, cena_zakupu FROM "
					+ Produkt.tableName
					+ " WHERE id_produkt= "
					+ SqlUtils.popraw(id);

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
							return new Produkt(result.getString(1), result
									.getString(2), result.getInt(3), result
									.getBoolean(4), result.getInt(5), result
									.getInt(6), id);
						}
					});
		} catch (Exception e)
		{
			MojeUtils.error(e);
			return null;
		}
	}

	public static void usunListeProduktow(String table,
			String id_parent_column, int id_parent) throws Exception
	{
		BazaDanych.getInstance().aktualizacja(
				"DELETE FROM " + table + " WHERE " + id_parent_column + "= "
						+ id_parent);
	}

	/**
	 * @param dodaj
	 *            - jesli true, zwieksza stan na magazynie (cofniecie faktury),
	 *            jesli false - odejmuje od magazynu (dodanie faktury)
	 */
	public void zmienIloscProduktyZMagazynu(
			List<? extends ProduktNaSztuki> produkty, boolean dodaj)
			throws Exception
	{
		if (produkty.isEmpty())
			return;
		/*
		 * UPDATE categories SET display_order = CASE id WHEN 1 THEN 3 WHEN 2
		 * THEN 4 WHEN 3 THEN 5 END, title = CASE id WHEN 1 THEN 'New Title 1'
		 * WHEN 2 THEN 'New Title 2' WHEN 3 THEN 'New Title 3' END WHERE id IN
		 * (1,2,3)
		 */

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(Produkt.tableName)
				.append(" set ilosc = CASE id_produkt ");
		for (ProduktNaSztuki pr : produkty)
		{
			int ilosc = pr.ilosc_produktu;
			if (!dodaj)
				ilosc = -ilosc;
			if (ilosc < 0)
				sql.append(" WHEN ").append(pr.produkt.id_produkt)
						.append(" THEN ilosc").append(SqlUtils.popraw(ilosc));
			else
				sql.append(" WHEN ").append(pr.produkt.id_produkt)
						.append(" THEN ilosc").append("+")
						.append(SqlUtils.popraw(ilosc));
		}
		sql.append(" END WHERE id_produkt in (");
		for (ProduktNaSztuki pr : produkty)
		{
			sql.append(pr.produkt.id_produkt).append(',');
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");
		BazaDanych.getInstance().aktualizacja(sql.toString());

		// przeladowanie panelu Magazynu, zeby byly aktualne ilosci produktow
		panel.przeladujTabele(false);
	}

	@SuppressWarnings("unchecked")
	public static List<ProduktWWysylce> pobierzDlaWysylki(int id)
	{
		try
		{
			String querySql = "SELECT P.kod, P.nazwa, P.ilosc, P.widoczny, P.id_producenta, P.cena_zakupu, P.id_produkt, "
					+ " P_W.lp, P_W.ilosc FROM "
					+ Produkt.tableName
					+ " P JOIN "
					+ Produkt.tableLaczacaWysylka
					+ " P_W ON P.id_produkt=P_W.id_produkt WHERE P_W.id_wysylka= "
					+ SqlUtils.popraw(id) + " ORDER BY lp";

			MojeUtils.println(querySql);

			return (List<ProduktWWysylce>) BazaDanych.getInstance().zapytanie(
			// sql do pobrania
					querySql,

					// operacja na pobranych danych
					new BazaStatementFunktor()
					{
						@Override
						public Object operacja(ResultSet result)
								throws Exception
						{
							List<ProduktWWysylce> produkty = new ArrayList<ProduktWWysylce>();
							while (result.next())
							{
								// result.getArray( columnIndex )
								// String kod, String nazwa, int ilosc, boolean
								// widoczny, int id_producenta, int cena_zakupu,
								// int id_produkt)
								Produkt produktSlownik = new Produkt(result
										.getString(1), result.getString(2),
										result.getInt(3), result.getBoolean(4),
										result.getInt(5), result.getInt(6),
										result.getInt(7));
								int lp = result.getInt(8);
								int ilosc = result.getInt(9);
								produkty.add(new ProduktWWysylce(lp, ilosc,
										produktSlownik));
							}
							return produkty;
						}
					});
		} catch (Exception e)
		{
			MojeUtils.println("pobierzDlaWysylki error");
			MojeUtils.error(e);
			return null;
		}
	}

	public static void dodajListeProduktowWysylka(
			List<ProduktWWysylce> produkty, int id) throws Exception
	{
		MojeUtils.println("Dodaje " + produkty.size()
				+ " produktow do wysylki o ID " + id);
		for (ProduktWWysylce produkt : produkty)
		{
			BazaDanych.getInstance().wstaw(
					"INSERT INTO  " + Produkt.tableLaczacaWysylka
							+ " (id_produkt, id_wysylka, ilosc, lp)"
							+ " VALUES ("
							+ SqlUtils.popraw(produkt.produkt.id_produkt) + ","
							+ SqlUtils.popraw(id) + ","
							+ SqlUtils.popraw(produkt.ilosc_produktu) + ","
							+ SqlUtils.popraw(produkt.lp) + ")");
		}
	}

	public static void dodajListeProduktow_wysylka(
			List<ProduktWWysylce> produkty, int id) throws Exception
	{
		MojeUtils.println("Dodaje " + produkty.size()
				+ " produktow do wysylki o ID " + id);
		for (ProduktWWysylce produkt : produkty)
		{
			BazaDanych.getInstance().wstaw(
					"INSERT INTO  " + Produkt.tableLaczacaWysylka
							+ " (id_produkt, id_wysylka, ilosc, lp)"
							+ " VALUES ("
							+ SqlUtils.popraw(produkt.produkt.id_produkt) + ","
							+ SqlUtils.popraw(id) + ","
							+ SqlUtils.popraw(produkt.ilosc_produktu) + ","
							+ SqlUtils.popraw(produkt.lp) + ")");
		}
	}

	/**
	 * Metoda konwertuje double*100 na inty.
	 * 
	 * @param value
	 *            - wartosc double
	 * @return int wartosc
	 */
	private static int rabatDoubleToInt(Double value)
	{
		return (int) (value * 100);
	}

	/**
	 * Metoda konwertuje inty na double.
	 * 
	 * @param value
	 *            wartosc int
	 * @return double wartosc
	 */
	private static double rabatIntToDouble(int value)
	{
		return ((double) value / 100);
	}
}
