package model;

import java.util.Date;

import kontroler.KontrachentBaza;
import kontroler.ObiektWyszukanieWarunki.StrukturaWarunku;
import kontroler.WalutaManager;
import utils.DataUtils;
import utils.MojeUtils;
import widok.abstrakt.PanelOgolnyParametry.OpisKolumn;

public class Wplata implements ObiektZId
{

	public int id_wplata;
	public Date data_wplaty;
	public int id_kontrachent;
	public int wartosc;
	public int waluta;

	public final static int kolumnaUkryta = 3; // liczac od 0, kolumna id

	public final static OpisKolumn opisKolumn = new OpisKolumn(new String[]
	{ "Data wpłaty", "Kontrachent", "Wartość wpłaty", "id" }, kolumnaUkryta);
	public final static String tableName = "wplata";
	public final static String[] kolumnyPrzeszukiwania =
	{ "id_wplata", "data_wplaty", "id_kontrachent", "wartosc", "waluta" };

	public Wplata() {}

	public Wplata(int id_wplata, Date data_wplaty, int id_kontrachent,
			int wartosc, int waluta) {
		this.id_wplata = id_wplata;
		this.data_wplaty = data_wplaty;
		this.id_kontrachent = id_kontrachent;
		this.wartosc = wartosc;
		this.waluta = waluta;
	}

	@Override
	public int getId()
	{
		return id_wplata;
	}

	@Override
	public String[] piszWierszTabeli() throws Exception
	{
		String[] wiersz = new String[4];
		wiersz[0] = DataUtils.formatujDate(DataUtils.stringToDate_format
				.format(data_wplaty));
		wiersz[1] = KontrachentBaza.pobierzNazweZBazy(id_kontrachent);
		wiersz[2] = MojeUtils.utworzWartoscZlotowki(wartosc)
				+ " "
				+ WalutaManager.pobierzNazweZBazy((long) waluta - 1,
						Waluta.tabelaWaluta);
		wiersz[3] = "" + id_wplata;
		return wiersz;
	}

	@Override
	public String[] getKolumnyPrzeszukiwania()
	{
		return Wplata.kolumnyPrzeszukiwania;
	}

	@Override
	public StrukturaWarunku getWarunekWidoczny()
	{
		return null;
	}

}
