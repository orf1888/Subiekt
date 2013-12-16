package model.produkt;


public class ProduktWWysylce
	extends ProduktNaSztuki
{
	public ProduktWWysylce( int lp, int ilosc_produktu, Produkt produkt )
	{
		super( lp, ilosc_produktu, produkt );
	}

	public String[] pisz()
	{
		return new String[] { "" + lp, produkt.kod, "" + produkt.nazwa, "" + ilosc_produktu };
	}

	public ProduktWWysylce copy()
	{
		return new ProduktWWysylce( lp, ilosc_produktu, produkt );
	}
}
