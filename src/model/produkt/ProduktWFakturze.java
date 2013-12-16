package model.produkt;

import utils.MojeUtils;

public class ProduktWFakturze
	extends ProduktNaSztuki
{
	public int _cena_jednostkowa;

	public boolean korekta;

	//@Transient
	public int wartoscPoKorekcie;

	public ProduktWFakturze( int lp, int cena_jednostkowa, int ilosc_produktu, Produkt produkt,
			boolean korekta, int wartoscPoKorekcie )
	{
		super( lp, ilosc_produktu, produkt );
		this._cena_jednostkowa = cena_jednostkowa;
		this.korekta = korekta;
		this.wartoscPoKorekcie = wartoscPoKorekcie;
	}

	public String[] pisz()
	{
		if ( korekta ) {
			return new String[] { "", produkt.nazwa,
				MojeUtils.utworzWartoscZlotowki( _cena_jednostkowa ), "" + ilosc_produktu,
				MojeUtils.utworzWartoscZlotowki( wartoscPoKorekcie ) };
		}
		else {
			return new String[] { "" + lp, produkt.nazwa,
				MojeUtils.utworzWartoscZlotowki( _cena_jednostkowa ), "" + ilosc_produktu,
				MojeUtils.utworzWartoscZlotowki( liczWartoscZNarzutem() ) };
		}
	}

	/**
	 * tylko dla nie-korekty
	 */
	public int liczWartoscZNarzutem()
	{
		return ( _cena_jednostkowa * ilosc_produktu );
	}

	public ProduktWFakturze copy()
	{
		return new ProduktWFakturze( lp, _cena_jednostkowa, ilosc_produktu, produkt, korekta,
				wartoscPoKorekcie );
	}
}
