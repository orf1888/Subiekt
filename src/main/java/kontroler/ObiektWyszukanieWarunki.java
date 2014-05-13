package kontroler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import model.Faktura;
import model.ObiektZId;
import utils.SqlUtils;

public class ObiektWyszukanieWarunki
{
	public static class StrukturaWarunku
	{
		Object wartosc;

		String kolumna;

		boolean zAsteryskem;

		public StrukturaWarunku(Object wartosc, String kolumna,
				boolean zAsteryskem) {
			this.wartosc = wartosc;
			this.kolumna = kolumna;
			this.zAsteryskem = zAsteryskem;
		}
	}

	public static class StrukturaWarunkuZData extends StrukturaWarunku
	{
		Object wartosc_2;

		public StrukturaWarunkuZData(Object wartosc, String kolumna,
				boolean zAsteryskem, Object wartosc_2) {
			super(wartosc, kolumna, zAsteryskem);
			this.wartosc_2 = wartosc_2;
		}
	}

	// warunki + warunek dowolnej kolumny

	public List<StrukturaWarunku> warunki = new ArrayList<StrukturaWarunku>();

	public String dowolnaKolumna = null;

	public ObiektZId typObiektu;

	public boolean tylkoWidoczne;

	//

	private ObiektWyszukanieWarunki() {}

	public ObiektWyszukanieWarunki(ObiektZId typObiektu) throws Exception {
		this.typObiektu = typObiektu;
	}

	public static ObiektWyszukanieWarunki TworzWarunekFaktura()
	{
		ObiektWyszukanieWarunki result = new ObiektWyszukanieWarunki();
		result.typObiektu = new Faktura();
		result.dodajWarunek("1", "aktualna");
		return result;
	}

	public String generujWarunekWhere()
	{
		String sql = "";

		for (StrukturaWarunku warunek : warunki)
		{
			sql = dodajWarunek(sql, warunek);
		}

		// dodaj do warunku wyszukiwanie po dowolnej kolumnie
		if (dowolnaKolumna != null)
		{
			List<String> kolumnyWBazie = Arrays.asList(typObiektu
					.getKolumnyPrzeszukiwania());
			Iterator<String> it = kolumnyWBazie.iterator();
			while (it.hasNext())
			{
				String itStr = it.next();
				// sprawdz czy taka kolumna byla uzyta w warunkach? wtedy ja
				// usun
				for (StrukturaWarunku warunek : warunki)
					if (warunek.kolumna.equals(itStr))
					{
						// it.remove();
						break;
					}
			}

			// dodaj warunek na dowolna kolumne
			String poprDowolna = "'"
					+ SqlUtils.poprawZAsteryskem(dowolnaKolumna) + "'";
			// pierwszy warunek ma " AND( "
			int i = 0;
			sql += " and ( " + kolumnyWBazie.get(i) + " like " + poprDowolna;
			++i;
			// pozostale warunki maja " OR "
			for (; i < kolumnyWBazie.size(); ++i)
			{
				sql += " or " + kolumnyWBazie.get(i) + " like " + poprDowolna;
			}
			sql += ")";
		}

		if (tylkoWidoczne)
		{
			sql = dodajWarunek(sql, typObiektu.getWarunekWidoczny());
		}

		if (sql.length() > 4)
			sql = " WHERE " + sql.substring(4);

		if (sql.isEmpty())
			return "";
		else
			return sql;
	}

	private String dodajWarunek(String sql, StrukturaWarunku warunek)
	{
		if (warunek instanceof StrukturaWarunkuZData)
		{
			StrukturaWarunkuZData warunek1 = (StrukturaWarunkuZData) warunek;
			sql += " and " + warunek1.kolumna + " between '" + warunek1.wartosc
					+ "' and '" + warunek1.wartosc_2 + "'";
		} else
		{
			if (warunek == null)
				return sql;// Przypadek gdy nie mamy warunku
			String rodzajPorownania = (warunek.zAsteryskem ? "like " : "= ");
			sql += " and " + warunek.kolumna + rodzajPorownania;

			String ciapki = (warunek.wartosc instanceof String) ? "'" : " ";
			if (warunek.zAsteryskem)
				sql += ciapki
						+ SqlUtils
								.poprawZAsteryskem(warunek.wartosc.toString())
						+ ciapki;
			else
				sql += ciapki + SqlUtils.popraw(warunek.wartosc.toString())
						+ ciapki;
		}

		return sql;
	}

	public void dodajWarunek(Object wartosc, String kolumna, boolean zAsteryskem)
	{
		// najpierw czyscimy stary warunek, potem wstawiamy nowy
		removeWarunek(kolumna);
		warunki.add(new StrukturaWarunku(wartosc, kolumna, zAsteryskem));
	}

	public void dodajWarunek(Object wartosc, String kolumna)
	{
		// najpierw czyscimy stary warunek, potem wstawiamy nowy
		removeWarunek(kolumna);
		warunki.add(new StrukturaWarunku(wartosc, kolumna, false));
	}

	public void dodajWarunekZData(Object wartosc, Object wartosc_2,
			String kolumna)
	{
		// najpierw czyscimy stary warunek, potem wstawiamy nowy
		removeWarunek(kolumna);
		warunki.add(new StrukturaWarunkuZData(wartosc, kolumna, false,
				wartosc_2));
	}

	/**
	 * znajdz warunek z kolumna, jesli jakis jest, i go usun
	 */
	public void removeWarunek(String kolumna)
	{
		for (int i = 0; i < warunki.size(); ++i)
			if (warunki.get(i).kolumna.equals(kolumna))
			{
				warunki.remove(i);
				return;
			}
	}

	public void dodajWarunekDowolnaKolumna(String wartosc)
	{
		dowolnaKolumna = wartosc;
	}

	public void removeWarunekDowolnaKolumna()
	{
		dowolnaKolumna = null;
	}

	public void ustawTylkoWidoczne()
	{
		tylkoWidoczne = true;
	}
}
