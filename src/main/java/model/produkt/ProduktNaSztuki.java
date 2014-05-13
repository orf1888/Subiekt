package model.produkt;

public class ProduktNaSztuki
{
	public int lp;

	public int ilosc_produktu;

	public Produkt produkt;

	public ProduktNaSztuki( int lp, int ilosc_produktu, Produkt produkt )
	{
		this.lp = lp;
		this.ilosc_produktu = ilosc_produktu;
		this.produkt = produkt;
	}
}
