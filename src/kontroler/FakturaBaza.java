package kontroler;

import java.io.IOException;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Faktura;
import model.Kontrachent;
import model.ObiektWiersz;
import model.ObiektZId;
import model.Waluta;
import model.produkt.Produkt;
import model.produkt.ProduktWFakturze;
import utils.BazaDanych;
import utils.BazaStatementFunktor;
import utils.DataUtils;
import utils.MojeUtils;
import utils.SqlUtils;
import utils.UserShowException;

public class FakturaBaza implements ObiektBazaManager
{
	private static FakturaBaza instance = new FakturaBaza();

	public static FakturaBaza instance()
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
				String data = DataUtils.formatujDate(result.getString(2));
				String[] wiersz =
				{
						MojeUtils.poprawNrFaktury(result.getInt(7),
								result.getInt(1),
								DataUtils.getYear(result.getString(2)),
								result.getBoolean(8)),
						data,
						DataUtils.formatujDate(result.getString(3)),
						KontrachentBaza.pobierzNazweZBazy(result.getInt(4)),
						MojeUtils.formatujWartosc(result.getInt(5))
								+ " "
								+ WalutaManager.pobierzNazweZBazy(
										result.getLong(9) - 1,
										Waluta.tabelaWaluta),
						"" + result.getInt(6), };
				wynik.add(wiersz);
			}
			return wynik;
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	public String[][] pobierzWierszeZBazy(ObiektWyszukanieWarunki warunki)
			throws Exception
	{
		String querySql = "SELECT DISTINCT numer, data_wystawienia, termin_platnosci, id_kontrachent, wartosc, id_faktura, rodzaj, czy_korekta, waluta FROM "
				+ Faktura.tableName;
		querySql += warunki.generujWarunekWhere() + " ORDER BY id_faktura";

		MojeUtils.println(querySql);

		List<String[]> wynik = (List<String[]>) BazaDanych.getInstance()
				.zapytanie(
				/* sql do pobrania */querySql,
				/* operacja na pobranych danych */pobieranieWierszaFunktor);

		/* probujemy wyszukac po nazwie kontrachenta */
		if (warunki.dowolnaKolumna != null
				&& !warunki.dowolnaKolumna.equals("*"))
			wynik.addAll(wyszukajPoKontrachencie(warunki));

		/* druga i trzecia wartosc to daty wynik[1] */
		return wynik.toArray(new String[wynik.size()][]);
	}

	@SuppressWarnings("unchecked")
	private List<String[]> wyszukajPoKontrachencie(
			ObiektWyszukanieWarunki warunki) throws Exception
	{
		ObiektWyszukanieWarunki warunekTmp = new ObiektWyszukanieWarunki(
				warunki.typObiektu);
		warunekTmp.warunki = warunki.warunki;
		warunekTmp.tylkoWidoczne = warunki.tylkoWidoczne;

		String querySql = "SELECT DISTINCT f.numer, f.data_wystawienia,f.termin_platnosci, f.id_kontrachent, f.wartosc, f.id_faktura, f.rodzaj FROM "
				+ Faktura.tableName
				+ " f JOIN "
				+ Kontrachent.tableName
				+ " k ON k.id_kontrachent=f.id_kontrachent WHERE k.nazwa like '"
				+ SqlUtils.poprawZAsteryskem(warunki.dowolnaKolumna)
				+ "' AND "
				+ warunekTmp.generujWarunekWhere().substring(6);

		MojeUtils.println(querySql);

		return (List<String[]>) BazaDanych.getInstance().zapytanie(querySql,
				pobieranieWierszaFunktor);
	}

	@Override
	public ObiektZId pobierzObiektZBazy(ObiektWiersz wiersz)
	{
		try
		{
			final int id = getIdFromWiersz(wiersz);

			final List<ProduktWFakturze> produkty = ProduktBaza
					.pobierzDlaFaktury(id, false);

			final List<ProduktWFakturze> produktyKorekta = ProduktBaza
					.pobierzDlaFaktury(id, true);

			MojeUtils.println("Pobrano " + produkty.size() + " produktow");

			String querySql = "SELECT numer, data_wystawienia, termin_platnosci, zaplacona, rodzaj, id_kontrachent, wartosc, aktualna, czy_korekta, waluta FROM "
					+ Faktura.tableName
					+ " WHERE id_faktura= "
					+ SqlUtils.popraw(id);

			MojeUtils.println(querySql);

			return (ObiektZId) BazaDanych.getInstance().zapytanie(
			/* sql do pobrania */querySql,
			/* operacja na pobranych danych */
			new BazaStatementFunktor()
			{
				@Override
				public Object operacja(ResultSet result) throws Exception
				{
					// dodajemy do tabeli kolejne pobrane wiersze,
					// skladajace sie z 4 elementow
					return new Faktura(id, result.getInt(1), DataUtils
							.parsujDate(result.getString(2)), DataUtils
							.parsujDate(result.getString(3)), result
							.getBoolean(4), result.getInt(5),
							(Kontrachent) KontrachentBaza
									.pobierzObiektZBazy(result.getInt(6)),
							result.getInt(7), result.getBoolean(8), result
									.getBoolean(9), produkty, produktyKorekta,
							result.getLong(10));
				}
			});
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static int pobierzNrFaktury(int rodzaj, String dataFaktury)
	{
		try
		{
			String querySql = "SELECT max( numer ) FROM " + Faktura.tableName
					+ " WHERE rodzaj= " + SqlUtils.popraw(rodzaj)
					+ " AND data_wystawienia BETWEEN '"
					+ DataUtils.pierwszyDzienRoku(dataFaktury) + "' AND '"
					+ DataUtils.ostatniDzienRoku(dataFaktury) + "'";
			return (Integer) BazaDanych.getInstance().zapytanie(querySql,
					new BazaStatementFunktor()
					{
						@Override
						public Object operacja(ResultSet result)
								throws Exception
						{
							try
							{
								return result.getInt(1);
							} catch (Exception przemilcz)
							{
								return null;
							}
						}
					});
		} catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public void dodaj(Object nowy) throws Exception
	{
		Faktura nowaFaktura = (Faktura) nowy;

		ObiektWyszukanieWarunki warunki = new ObiektWyszukanieWarunki(
				new Faktura());
		warunki.dodajWarunek(nowaFaktura.numer, "numer");
		warunki.dodajWarunek(nowaFaktura.rodzaj, "rodzaj");
		String[][] znalezioneWiersze = pobierzWierszeZBazy(warunki);

		if (nowaFaktura.rodzaj == Faktura.SPRZEDAZ)
		{
			dodaj_encje_faktura(nowaFaktura);

			/* edycja magazynu */
			ProduktBaza.dodajListeProduktow(nowaFaktura.produkty,
					nowaFaktura.id_faktura);
			boolean dodac = (nowaFaktura.rodzaj == Faktura.SPRZEDAZ) ? false
					: true;
			ProduktBaza.instance().zmienIloscProduktyZMagazynu(
					nowaFaktura.produkty, dodac);
		} else
		{
			if (znalezioneWiersze.length == 0)
			{
				dodaj_encje_faktura(nowaFaktura);

				/* edycja magazynu */
				ProduktBaza.dodajListeProduktow(nowaFaktura.produkty,
						nowaFaktura.id_faktura);
				boolean dodac = (nowaFaktura.rodzaj == Faktura.SPRZEDAZ) ? false
						: true;
				ProduktBaza.instance().zmienIloscProduktyZMagazynu(
						nowaFaktura.produkty, dodac);
			} else
				throw new UserShowException(
						"Znaleziono już fakturę o takim numerze");
		}
	}

	public static Faktura nowaFaktura(int rodzajFaktury, int numer,
			List<ProduktWFakturze> listaProduktow, Long waluta,
			String dataFaktury) throws UserShowException, IOException,
			ParseException
	{
		if (rodzajFaktury == Faktura.ZAKUP)
		{
			if (numer < 1)
			{
				throw new UserShowException("Podano niepoprawny numer");
			}
		} else
		{
			numer = pobierzNrFaktury(rodzajFaktury,
					DataUtils.stringToDate_format.format(new SimpleDateFormat(
							"dd-mm-yyyy").parse(dataFaktury))) + 1;
		}
		Date data_wystawienia = new Date();
		Date termin_platnosci = new Date(data_wystawienia.getTime());
		if (data_wystawienia.after(termin_platnosci))
			throw new UserShowException(
					"Data wystawienia nie może być po terminie płatności!");

		int wartosc = 0;
		for (ProduktWFakturze p : listaProduktow)
			wartosc += p.liczWartoscZNarzutem();

		List<ProduktWFakturze> copy_lista = new ArrayList<ProduktWFakturze>();
		copy_lista.addAll(listaProduktow);
		boolean zaplacona = false;
		return new Faktura(0, numer, data_wystawienia, termin_platnosci,
				zaplacona, rodzajFaktury, null, wartosc, true, false,
				copy_lista, null, waluta);
	}

	/**
	 * funkcja juz bez sprawdzania, czy numer sie powtarza, i bez edycji
	 * produktow w bazie
	 */
	public void dodaj_encje_faktura(Faktura nowaFaktura) throws Exception
	{
		String data_wystawienia_faktury_nowej = DataUtils.stringToDate_format
				.format(nowaFaktura.data_wystawienia);
		String termin_platnosci_faktury_nowej = DataUtils.stringToDate_format
				.format(nowaFaktura.termin_platnosci);
		int generatedId = BazaDanych
				.getInstance()
				.wstaw("INSERT INTO  "
						+ Faktura.tableName
						+ " (numer, data_wystawienia, termin_platnosci, zaplacona, rodzaj, id_kontrachent, wartosc, aktualna, czy_korekta, waluta"
						+ ") VALUES ('"
						+ SqlUtils.popraw(nowaFaktura.numer)
						+ "','"
						+ SqlUtils.popraw(data_wystawienia_faktury_nowej)
						+ "','"
						+ SqlUtils.popraw(termin_platnosci_faktury_nowej)
						+ "','"
						+ SqlUtils.popraw((nowaFaktura.zaplacona ? 1 : 0))
						+ "',"
						+ SqlUtils.popraw(nowaFaktura.rodzaj)
						+ ","
						+ SqlUtils.popraw(((nowaFaktura.kontrahent == null ? 0
								: nowaFaktura.kontrahent.id_kontrachent)))
						+ ",'"
						+ SqlUtils.popraw(nowaFaktura.wartosc_z_narzutem)
						+ "','"
						+ SqlUtils.popraw((nowaFaktura.aktualna ? 1 : 0))
						+ "','"
						+ SqlUtils.popraw((nowaFaktura.isKorekta ? 1 : 0))
						+ "','" + SqlUtils.popraw(nowaFaktura.waluta) + "')");

		nowaFaktura.id_faktura = generatedId;
	}

	public void ustawNieaktualna(Faktura faktura) throws Exception
	{
		BazaDanych.getInstance().aktualizacja(
				"UPDATE " + Faktura.tableName + " set aktualna = '"
						+ SqlUtils.popraw((faktura.aktualna ? 1 : 0))
						+ "' WHERE id_faktura = "
						+ SqlUtils.popraw(faktura.id_faktura));
	}

	@Override
	public void edytuj(Object stara, Object nowa) throws Exception
	{
		Faktura staraFaktura = (Faktura) stara;
		Faktura nowaFaktura = (Faktura) nowa;
		if (staraFaktura.isKorekta != nowaFaktura.isKorekta)
			throw new NullPointerException();
		if (MojeUtils.equals("" + nowaFaktura.numer, "" + staraFaktura.numer))
		{
			/* numer sie nie zmienil - to jedyny warunek, jest OK */
		} else
		{
			/*
			 * numer sie zmienil - sprawdz czy taki sam juz nie istnieje w
			 * bazie?
			 */
			ObiektWyszukanieWarunki warunki = new ObiektWyszukanieWarunki(
					new Faktura());
			warunki.dodajWarunek(nowaFaktura.numer, "numer");
			warunki.dodajWarunek(1, "aktualna");

			String[][] znalezioneWiersze = pobierzWierszeZBazy(warunki);
			if (znalezioneWiersze.length == 0)
			{
				/* mozna edytować bo numer nie wystepuje w bazie */
			} else
				MojeUtils.showError("Znaleziono taki sam numer: "
						+ getNumerFromWiersz(znalezioneWiersze[0]));
		}
		String data_wystawienia_faktury_nowej = DataUtils.stringToDate_format
				.format(nowaFaktura.data_wystawienia);
		String termin_platnosci_faktury_nowej = DataUtils.stringToDate_format
				.format(nowaFaktura.termin_platnosci);
		BazaDanych.getInstance().aktualizacja(
				"UPDATE "
						+ Faktura.tableName
						+ " set numer= '"
						+ SqlUtils.popraw(nowaFaktura.numer)
						+ "', data_wystawienia= '"
						+ SqlUtils.popraw(data_wystawienia_faktury_nowej)
						+ "', termin_platnosci= '"
						+ SqlUtils.popraw(termin_platnosci_faktury_nowej)
						+ "', zaplacona= '"
						+ SqlUtils.popraw((nowaFaktura.zaplacona ? 1 : 0))
						+ "', rodzaj= '"
						+ SqlUtils.popraw(nowaFaktura.rodzaj)
						+ "', id_kontrachent = '"
						+ SqlUtils
								.popraw(nowaFaktura.kontrahent.id_kontrachent)
						+ "', wartosc = '"
						+ SqlUtils.popraw(nowaFaktura.wartosc_z_narzutem)
						+ "', aktualna = '"
						+ SqlUtils.popraw((nowaFaktura.aktualna ? 1 : 0))
						+ "', waluta = '" + nowaFaktura.waluta
						+ "' WHERE id_faktura = "
						+ SqlUtils.popraw(staraFaktura.id_faktura));

		if (!nowaFaktura.isKorekta)
		{
			ProduktBaza.usunListeProduktow(Produkt.tableLaczacaFaktura, // staraFaktura.produkty,
					"id_faktura", staraFaktura.id_faktura);
			ProduktBaza.dodajListeProduktow(nowaFaktura.produkty,
					staraFaktura.id_faktura);
			boolean dodac = (nowaFaktura.rodzaj == Faktura.SPRZEDAZ) ? false
					: true;
			ProduktBaza.instance().zmienIloscProduktyZMagazynu(
					staraFaktura.produkty, !dodac);
			ProduktBaza.instance().zmienIloscProduktyZMagazynu(
					nowaFaktura.produkty, dodac);
		} else
		{
			ProduktBaza.usunListeProduktow(Produkt.tableLaczacaFaktura, // staraFaktura.produkty,
					"id_faktura", staraFaktura.id_faktura);
			ProduktBaza.dodajListeProduktow(nowaFaktura.produkty,
					staraFaktura.id_faktura);
			ProduktBaza.dodajListeProduktow(nowaFaktura.produktyKorekta,
					staraFaktura.id_faktura);
			boolean dodac = (nowaFaktura.rodzaj == Faktura.SPRZEDAZ) ? false
					: true;
			/* wlasciwe produkty (z korekty) */
			ProduktBaza.instance().zmienIloscProduktyZMagazynu(
					staraFaktura.produktyKorekta, !dodac);
			ProduktBaza.instance().zmienIloscProduktyZMagazynu(
					nowaFaktura.produktyKorekta, dodac);

			ProduktBaza.instance().zmienIloscProduktyZMagazynu(
					staraFaktura.produkty, !dodac);
			ProduktBaza.instance().zmienIloscProduktyZMagazynu(
					nowaFaktura.produkty, dodac);
		}
	}

	/* Refactoring */
	@Override
	public void zmienWidocznosc(ObiektWiersz wiersz, boolean aktualna)
			throws Exception
	{
		BazaDanych.getInstance().aktualizacja(
				"UPDATE " + Faktura.tableName + " set aktualna = "
						+ (aktualna ? 1 : 0) + " WHERE id_faktura = "
						+ SqlUtils.popraw(getIdFromWiersz(wiersz)));
	}

	public static int getIdFromWiersz(ObiektWiersz wiersz)
	{
		return Integer.parseInt(wiersz.wiersz[5]);
	}

	public static String getNumerFromWiersz(String[] wiersz)
	{
		return wiersz[0];
	}

	public static void ustawZaplacona(ObiektWiersz wiersz, boolean zaplacona)
			throws Exception
	{
		BazaDanych.getInstance().aktualizacja(
				"UPDATE " + Faktura.tableName + " set zaplacona = "
						+ (zaplacona ? "1" : "0") + " WHERE id_faktura = "
						+ SqlUtils.popraw(getIdFromWiersz(wiersz)));
	}
}
