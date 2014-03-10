package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kontroler.ObiektWyszukanieWarunki.StrukturaWarunku;
import kontroler.WalutaManager;
import model.produkt.ProduktWFakturze;
import utils.DataUtils;
import utils.MojeUtils;
import widok.abstrakt.PanelOgolnyParametry.OpisKolumn;

public class Faktura implements ObiektZId
{
	public static String tableName = "faktura";

	public final static int kolumnaUkryta = 5; // liczac od 0, kolumna id

	public final static OpisKolumn opisKolumn = new OpisKolumn(new String[]
	{ "Numer faktury", "Data wystawienia", " Termin płatności", "kontrachent",
			"Wartość", "id" }, kolumnaUkryta);

	public static String[] kolumnyWBazie =
	{ "numer", "data_wystawienia", "termin_platnosci", "wartosc" };

	public static final int SPRZEDAZ = 1;

	public static final int ZAKUP = 2;

	public int id_faktura;

	public int numer;

	public Date data_wystawienia;

	public Date termin_platnosci;

	public boolean zaplacona;

	public int rodzaj; // int okresla rodzaj

	public Kontrachent kontrachent;

	public int wartosc_z_narzutem;

	public boolean aktualna;

	public boolean isKorekta;

	public List<ProduktWFakturze> produkty;

	public List<ProduktWFakturze> produktyKorekta; // uzywane tylko dla Korekt

	public Long waluta;

	public Faktura() {}

	public Faktura(int id_faktura, int numer, Date data_wystawienia,
			Date termin_platnosci, boolean zaplacona, int rodzaj,
			Kontrachent kontrachent, int wartosc, boolean aktualna,
			boolean isKorekta, List<ProduktWFakturze> produkty,
			List<ProduktWFakturze> produktyKorekta, Long waluta) {
		this.id_faktura = id_faktura;
		this.numer = numer;
		this.data_wystawienia = data_wystawienia;
		this.termin_platnosci = termin_platnosci;
		this.zaplacona = zaplacona;
		this.rodzaj = rodzaj;
		this.kontrachent = kontrachent;
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
		wiersz[1] = DataUtils.formatujDate(DataUtils.stringToDate_format
				.format(data_wystawienia));
		wiersz[2] = DataUtils.formatujDate(DataUtils.stringToDate_format
				.format(termin_platnosci));
		wiersz[3] = "" + (kontrachent == null ? "Brak!" : kontrachent.nazwa);
		wiersz[4] = MojeUtils.formatujWartosc(wartosc_z_narzutem)
				+ " "
				+ WalutaManager.pobierzNazweZBazy(waluta - 1,
						Waluta.tabelaWaluta);
		wiersz[5] = "" + id_faktura;
		return wiersz;
	}

	public String piszNumerFaktury()
	{
		return MojeUtils.poprawNrFaktury(
				rodzaj,
				numer,
				DataUtils.getYear(DataUtils.stringToDate_format
						.format(data_wystawienia)) + "", isKorekta);
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
		List<ProduktWFakturze> result = new ArrayList<ProduktWFakturze>(
				produkty.size());
		for (ProduktWFakturze p : produkty)
			result.add(p.copy());
		return result;
	}

	public List<ProduktWFakturze> getProduktyKorekta_copy()
	{
		List<ProduktWFakturze> result = new ArrayList<ProduktWFakturze>(
				produktyKorekta.size());
		for (ProduktWFakturze p : produktyKorekta)
			result.add(p.copy());
		return result;
	}

	private static StrukturaWarunku warunekWidoczny = new StrukturaWarunku(1,
			"aktualna", false);

	@Override
	public StrukturaWarunku getWarunekWidoczny()
	{
		return warunekWidoczny;
	}
}