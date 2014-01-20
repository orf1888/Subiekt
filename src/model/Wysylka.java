package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kontroler.ObiektWyszukanieWarunki.StrukturaWarunku;
import model.produkt.ProduktWWysylce;
import utils.MojeUtils;

public class Wysylka implements ObiektZId
{
	public static String tableName = "wysylka";

	public static String[] kolumnyWyswietlane =
	{ "Numer wysyłki", "Data wysyłki", "id" };

	public static String[] kolumnyWBazie =
	{ "id", "data" };

	public int id;

	public Date data_wysylki;

	public List<ProduktWWysylce> produkty;

	public Wysylka() {}

	public Wysylka(int id, Date data_wysylki, List<ProduktWWysylce> produkty) {
		this.id = id;
		this.data_wysylki = data_wysylki;
		this.produkty = produkty;
	}

	public static final int wierszIndeks_wartoscId = 2;

	@Override
	public String[] piszWierszTabeli()
	{
		String[] wiersz = new String[3];
		wiersz[0] = getNumer();
		wiersz[1] = MojeUtils.formatujDate(MojeUtils.stringToDate_format
				.format(data_wysylki));
		wiersz[wierszIndeks_wartoscId] = "" + id;
		return wiersz;
	}

	@Override
	public int getId()
	{
		return id;
	}

	@Override
	public String[] getKolumnyPrzeszukiwania()
	{
		return kolumnyWBazie;
	}

	public List<ProduktWWysylce> getProdukty_copy()
	{
		List<ProduktWWysylce> result = new ArrayList<ProduktWWysylce>(
				produkty.size());
		for (ProduktWWysylce p : produkty)
			result.add(p.copy());
		return result;
	}

	@Override
	public StrukturaWarunku getWarunekWidoczny()
	{
		return null;
	}

	@SuppressWarnings("deprecation")
	public String getNumer()
	{
		return "W/" + id + "/" + (data_wysylki.getYear() + 1900);
	}
}