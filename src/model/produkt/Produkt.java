package model.produkt;

import model.ObiektZId;
import kontroler.ObiektWyszukanieWarunki.StrukturaWarunku;

public class Produkt
	implements ObiektZId
{
	public static final String tableName = "produkt";

	public static final String tableLaczacaFaktura = "c_faktura_produkt";

	public static final String tableLaczacaWysylka = "c_wysylka_produkt";

	public final static String[] kolumnyWyswietlane = { "Kod", "Nazwa", "Ilość", "id" };

	public final static String[] kolumnyWBazie = { "kod", "nazwa", "ilosc", "widoczny", "id_producenta",
		"cena_zakupu" };

	/*
	 * 
	 */

	public String kod;

	public String nazwa;

	public int ilosc;

	public boolean widoczny;

	public int id_producenta;

	public int cena_zakupu;

	public int id_produkt;

	public Produkt( String kod, String nazwa, int ilosc, boolean widoczny, int id_producenta,
			int cena_zakupu, int id_produkt )
	{
		this.kod = kod;
		this.nazwa = nazwa;
		this.ilosc = ilosc;
		this.widoczny = widoczny;
		this.id_producenta = id_producenta;
		this.cena_zakupu = cena_zakupu;
		this.id_produkt = id_produkt;
	}

	public Produkt()
	{}

	@Override
	public String[] piszWierszTabeli()
	{
		String[] wiersz = new String[4];
		wiersz[0] = kod;
		wiersz[1] = nazwa;
		wiersz[2] = "" + ilosc;
		wiersz[3] = "" + id_produkt;
		return wiersz;
	}

	@Override
	public int getId()
	{
		return id_produkt;
	}

	@Override
	public String[] getKolumnyPrzeszukiwania()
	{
		return kolumnyWBazie;
	}

	private static StrukturaWarunku warunekWidoczny = new StrukturaWarunku( 1, "widoczny", false );

	@Override
	public StrukturaWarunku getWarunekWidoczny()
	{
		return warunekWidoczny;
	}
}
