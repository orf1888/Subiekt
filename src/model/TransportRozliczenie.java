package model;

import java.io.IOException;
import java.util.Date;

import kontroler.ObiektWyszukanieWarunki.StrukturaWarunku;
import kontroler.WalutaManager;
import utils.DataUtils;
import utils.MojeUtils;
import utils.UserShowException;
import widok.abstrakt.PanelOgolnyParametry.OpisKolumn;

public class TransportRozliczenie implements ObiektZId
{
	public int id;
	public String nr_faktury;
	public Date data_ksiegowania;
	public int wartosc;
	public Long waluta;
	public int id_faktury_odpowiadajacej;// id faktury do której odnosi się
											// rachunek
	public String nr_faktury_odpowiadajacej; // zmienna używana wyłącznie w
												// Drukarzu
	/* Stałe do rozpoznania waluty */
	public static final String EURO = "ЕВРО";
	public static final String PLN = "Злотий";
	/* Struktura widoku i DB */
	public static String tableName = "transport_rozliczenie";

	public final static int kolumnaUkryta = 4; // id

	public final static OpisKolumn opisKolumn = new OpisKolumn(
			new String[]
			{ "Numer oryginału", "Numer faktury", "Data wystawienia",
					"Wartość", "ID" }, kolumnaUkryta);

	public static String[] kolumnyWBazie =
	{ "nr_faktury", "data_ksiegowania", "wartosc", "waluta",
			"id_faktury_odpowiadajacej" };

	public TransportRozliczenie(int id, Date data_ksiegowania,
			String nr_faktury, int wartosc, Long waluta, int id_faktury) {
		this.id = id;
		this.data_ksiegowania = data_ksiegowania;
		this.nr_faktury = nr_faktury;
		this.wartosc = wartosc;
		this.waluta = waluta;
		this.id_faktury_odpowiadajacej = id_faktury;
	}

	/***
	 * Używać wyłącznie w przypadku tworzenia miesięcznego rozliczenia.
	 * 
	 * Utworzony obiekt posiada minimalną ilość danych.
	 * 
	 * @param wiersz
	 *            - Sttring[] z bazy danych
	 * @throws IOException
	 * @throws UserShowException
	 */
	public TransportRozliczenie(String[] wiersz) throws UserShowException,
			IOException {
		this.nr_faktury = wiersz[0];
		this.nr_faktury_odpowiadajacej = wiersz[1];
		this.wartosc = Integer.parseInt(wiersz[3]
				.substring(0, wiersz[3].length() - 4).replaceAll(",", "")
				.trim());
		this.waluta = (long) WalutaManager.konwertujWalute(wiersz[3].substring(
				wiersz[3].length() - 3).trim());
		this.id = Integer.parseInt(wiersz[4]);
	}

	@Override
	public int getId()
	{
		return id;
	}

	@Override
	public String[] piszWierszTabeli() throws Exception
	{
		String[] wiersz = new String[5];
		wiersz[0] = nr_faktury;
		wiersz[1] = MojeUtils
				.pobierzNrFakturyOdpowiadajacej(this.id_faktury_odpowiadajacej);
		wiersz[2] = DataUtils.formatujDate(DataUtils.stringToDate_format
				.format(data_ksiegowania));
		wiersz[3] = "" + MojeUtils.formatujWartosc(wartosc) + " ";
		if (WalutaManager.pobierzNazweZBazy(waluta - 1, Waluta.tabelaWaluta)
				.equals(null))
			wiersz[3] += "Nierozpoznana";
		else
			wiersz[3] += WalutaManager.pobierzNazweZBazy(waluta - 1,
					Waluta.tabelaWaluta);
		wiersz[4] = "" + id;
		return wiersz;
	}

	/* Metody narazie nie używane */
	@Override
	public String[] getKolumnyPrzeszukiwania()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StrukturaWarunku getWarunekWidoczny()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static String getEuro()
	{
		return EURO;
	}

	public static String getPln()
	{
		return PLN;
	}
}
