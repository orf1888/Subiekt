package model.produkt;

import utils.MojeUtils;

public class ProduktWFakturze extends ProduktNaSztuki
{
	public int _cena_jednostkowa;

	public boolean korekta;

	// @Transient
	public int wartoscPoKorekcie;

	public int rabat;

	public ProduktWFakturze(int lp, int cena_jednostkowa, int ilosc_produktu,
			Produkt produkt, boolean korekta, int wartoscPoKorekcie, int rabat) {
		super(lp, ilosc_produktu, produkt);
		this._cena_jednostkowa = cena_jednostkowa;
		this.korekta = korekta;
		this.wartoscPoKorekcie = wartoscPoKorekcie;
		this.rabat = rabat;
	}

	public String[] pisz()
	{
		if (korekta)
		{
			return new String[]
			{ "", produkt.nazwa,
					MojeUtils.utworzWartoscZlotowki(_cena_jednostkowa),
					"" + ilosc_produktu,
					MojeUtils.utworzWartoscZlotowki(wartoscPoKorekcie),
					"" + rabat };
		} else
		{
			return new String[]
			{ "" + lp, produkt.nazwa,
					MojeUtils.utworzWartoscZlotowki(_cena_jednostkowa),
					"" + ilosc_produktu,
					MojeUtils.utworzWartoscZlotowki(liczWartoscZNarzutem()),
					"" + rabat };
		}
	}

	/**
	 * tylko dla nie-korekty
	 */
	public int liczWartoscZNarzutem()
	{
		return ((_cena_jednostkowa * ilosc_produktu * (100 - rabat)) / 100);
	}

	public ProduktWFakturze copy()
	{
		return new ProduktWFakturze(lp, _cena_jednostkowa, ilosc_produktu,
				produkt, korekta, wartoscPoKorekcie, rabat);
	}
}
