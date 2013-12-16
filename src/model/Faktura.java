package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kontroler.ObiektWyszukanieWarunki.StrukturaWarunku;
import kontroler.WalutaManager;
import model.produkt.ProduktWFakturze;
import utils.MojeUtils;

public class Faktura
	implements ObiektZId
{
	public static String tableName = "faktura";

	public static String[] kolumnyWyswietlane = { "Numer faktury", "Data wystawienia",
		" Termin płatności", "Kontrahent", "Wartość", "id" };

	public static String[] kolumnyWBazie =
		{ "numer", "data_wystawienia", "termin_platnosci", "wartosc" };

	public static final int SPRZEDAZ = 1;

	public static final int ZAKUP = 2;

	public int id_faktura;

	public int numer;

	public Date data_wystawienia;

	public Date termin_platnosci;

	public boolean zaplacona;

	public int rodzaj; 				//int okresla rodzaj

	public Kontrachent kontrahent;

	public int wartosc_z_narzutem;

	public boolean aktualna;

	public boolean isKorekta;

	public List<ProduktWFakturze> produkty;

	public List<ProduktWFakturze> produktyKorekta;		// uzywane tylko dla Korekt

	public Long waluta;

	public Faktura()
	{

	}

	public Faktura( int id_faktura, int numer, Date data_wystawienia, Date termin_platnosci,
			boolean zaplacona, int rodzaj, Kontrachent kontrahent, int wartosc, boolean aktualna,
			boolean isKorekta, List<ProduktWFakturze> produkty, List<ProduktWFakturze> produktyKorekta,
			Long waluta )
	{
		this.id_faktura = id_faktura;
		this.numer = numer;
		this.data_wystawienia = data_wystawienia;
		this.termin_platnosci = termin_platnosci;
		this.zaplacona = zaplacona;
		this.rodzaj = rodzaj;
		this.kontrahent = kontrahent;
		this.wartosc_z_narzutem = wartosc;
		this.aktualna = aktualna;
		this.isKorekta = isKorekta;
		this.produkty = produkty;
		this.produktyKorekta = produktyKorekta;
		this.waluta = waluta;
	}

	@Override
	public String[] piszWierszTabeli()
	{
		String[] wiersz = new String[7];
		wiersz[0] = piszNumerFaktury();
		wiersz[1] = MojeUtils.formatujDate( data_wystawienia.toString() );
		wiersz[2] = MojeUtils.formatujDate( termin_platnosci.toString() );
		wiersz[3] = "" + ( kontrahent == null ? "Brak!"
				: kontrahent.nazwa );
		wiersz[4] =
			MojeUtils.formatujWartosc( wartosc_z_narzutem ) + " "
				+ WalutaManager.pobierzNazweZBazy( waluta - 1, Waluta.tabelaWaluta );
		wiersz[5] = "" + id_faktura;
		return wiersz;
	}

	@SuppressWarnings( "deprecation" )
	public String piszNumerFaktury()
	{
		return MojeUtils.poprawNrFaktury( rodzaj, numer, ( data_wystawienia.getYear() + 1900 ) + "",
			isKorekta );
	}

	@Override
	public int getId()
	{
		return id_faktura;
	}

	public int getNr()
	{
		return numer;
	}

	@Override
	public String[] getKolumnyPrzeszukiwania()
	{
		return kolumnyWBazie;
	}

	public List<ProduktWFakturze> getProdukty_copy()
	{
		List<ProduktWFakturze> result = new ArrayList<ProduktWFakturze>( produkty.size() );
		for ( ProduktWFakturze p : produkty )
			result.add( p.copy() );
		return result;
	}

	public List<ProduktWFakturze> getProduktyKorekta_copy()
	{
		List<ProduktWFakturze> result = new ArrayList<ProduktWFakturze>( produktyKorekta.size() );
		for ( ProduktWFakturze p : produktyKorekta )
			result.add( p.copy() );
		return result;
	}

	private static StrukturaWarunku warunekWidoczny = new StrukturaWarunku( 1, "aktualna", false );

	@Override
	public StrukturaWarunku getWarunekWidoczny()
	{
		return warunekWidoczny;
	}
}
