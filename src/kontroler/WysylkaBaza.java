package kontroler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.ObiektWiersz;
import model.ObiektZId;
import model.Wysylka;
import model.produkt.Produkt;
import model.produkt.ProduktWWysylce;
import utils.BazaDanych;
import utils.BazaStatementFunktor;
import utils.MojeUtils;
import utils.SqlUtils;

public class WysylkaBaza implements ObiektBazaManager
{

	BazaStatementFunktor pobieranieWierszaFunktor = new BazaStatementFunktor()
	{
		@Override
		public Object operacja(ResultSet result) throws SQLException
		{
			List<String[]> wynik = new ArrayList<String[]>();
			// dodajemy do tabeli kolejne pobrane wiersze
			while (result.next())
			{
				String data = MojeUtils.formatujDate(result.getString(2));
				String[] wiersz =
				{ "W/" + result.getInt(1) + "/" + data.substring(6), data,
				/** id **/
				"" + result.getInt(1) };
				wynik.add(wiersz);
			}
			return wynik;
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	public String[][] pobierzWierszeZBazy(ObiektWyszukanieWarunki warunki)
			throws SQLException
	{
		String querySql = "SELECT DISTINCT id, data FROM " + Wysylka.tableName;
		querySql += warunki.generujWarunekWhere();

		MojeUtils.println(querySql);

		List<String[]> wynik = (List<String[]>) BazaDanych.getInstance()
				.zapytanie(
				// sql do pobrania
						querySql,
						// operacja na pobranych danych
						pobieranieWierszaFunktor);

		return wynik.toArray(new String[wynik.size()][]);
	}

	@Override
	public ObiektZId pobierzObiektZBazy(ObiektWiersz wiersz)
	{
		try
		{
			final int id = getIdFromWiersz(wiersz);

			final List<ProduktWWysylce> produkty = ProduktBaza
					.pobierzDlaWysylki(id);

			MojeUtils.println("Pobrano " + produkty.size() + " produktow");

			String querySql = "SELECT data FROM " + Wysylka.tableName
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
								throws SQLException
						{
							// dodajemy do tabeli kolejne pobrane wiersze,
							// skladajace sie z 4 elementow
							return new Wysylka(id, MojeUtils.parsujDate(result
									.getString(1)), produkty);
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
		Wysylka nowaWysylka = (Wysylka) nowy;
		String data_wysylki = MojeUtils.stringToDate_format
				.format(nowaWysylka.data_wysylki);
		int generatedId = BazaDanych.getInstance().wstaw(
				"INSERT INTO  " + Wysylka.tableName + " (data) VALUES ('"

				+ SqlUtils.popraw(data_wysylki) + "')");

		ProduktBaza.dodajListeProduktowWysylka(nowaWysylka.produkty,
				generatedId);
		ProduktBaza.instance().zmienIloscProduktyZMagazynu(
				nowaWysylka.produkty, true);
		nowaWysylka.id = generatedId;
	}

	@Override
	public void edytuj(Object stara, Object nowa) throws Exception
	{
		Wysylka staraWysylka = (Wysylka) stara;
		Wysylka nowaWysylka = (Wysylka) nowa;
		String data_wysylki_nowa = MojeUtils.stringToDate_format
				.format(nowaWysylka.data_wysylki);
		BazaDanych.getInstance().aktualizacja(
				"UPDATE " + Wysylka.tableName + " set data= '"
						+ SqlUtils.popraw(data_wysylki_nowa) + "' WHERE id = "
						+ SqlUtils.popraw(staraWysylka.id));

		ProduktBaza.usunListeProduktow(Produkt.tableLaczacaWysylka, // staraWysylka.produkty,
				"id_wysylka", staraWysylka.id);
		ProduktBaza.dodajListeProduktow_wysylka(nowaWysylka.produkty,
				staraWysylka.id);
		ProduktBaza.instance().zmienIloscProduktyZMagazynu(
				staraWysylka.produkty, false);
		ProduktBaza.instance().zmienIloscProduktyZMagazynu(
				nowaWysylka.produkty, true);

	}

	@Override
	public void zmienWidocznosc(ObiektWiersz wiersz, boolean aktualna)
			throws SQLException
	{
		throw new NullPointerException();
	}

	public static int getIdFromWiersz(ObiektWiersz wiersz)
	{
		return Integer.parseInt(wiersz.wiersz[Wysylka.wierszIndeks_wartoscId]);
	}
}