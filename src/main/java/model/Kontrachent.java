package model;

import kontroler.ObiektWyszukanieWarunki.StrukturaWarunku;
import widok.abstrakt.PanelOgolnyParametry.OpisKolumn;

public class Kontrachent implements ObiektZId
{
	public final static String tableName = "kontrachent";

	public final static int kolumnaUkryta = 3; // liczac od 0, kolumna id

	public final static OpisKolumn opisKolumn = new OpisKolumn(new String[]
	{ "Nazwa", "Miejscowość", "NIP", "id" }, kolumnaUkryta);

	public final static String[] kolumnyPrzeszukiwania =
	{ "nazwa", "miasto", "kod_pocztowy", "ulica", "regon", "nr_konta", "nip" };

	public int id_kontrachent;

	public String nazwa;

	public String miasto;

	public String kod_pocztowy;

	public String ulica;

	public int _cena_mnoznik;

	public String regon;

	public String nrKonta;

	public boolean widoczny;

	public String nip;

	// dodałem do bazy kredyt_kupiecki
	public int kredyt_kupiecki;

	/* długi walutowe kontrachenta */
	public Integer dlug_pln, dlug_eur, dlug_usd, dlug_uah;

	/*
	 * środki nierozliczone w waluciepodczas wpłaty automatycznie płaconesą
	 * najstarsze niezałacone faktury,kolumny odpowiadają różnicy wpłaty oraz
	 * faktur opłaconych dzięki wpłacie
	 */
	public Integer nadplata_pln, nadplata_eur, nadplata_usd, nadplata_uah;

	public Kontrachent() {}

	public Kontrachent(String nazwa, String miasto, String kod_pocztowy,
			String ulica, int cena_mnoznik, boolean widoczny, String nip,
			String regon, String nrKonta, int kredyt_kupiecki, int id,
			Integer dlug_pln, Integer dlug_eur, Integer dlug_usd,
			Integer dlug_uah, Integer nadplata_pln, Integer nadplata_eur,
			Integer nadplata_usd, Integer nadplata_uah) {
		this.nazwa = nazwa;
		this.miasto = miasto;
		this.kod_pocztowy = kod_pocztowy;
		this.ulica = ulica;
		this._cena_mnoznik = cena_mnoznik;
		this.widoczny = widoczny;
		this.nip = nip;
		this.regon = regon;
		this.nrKonta = nrKonta;
		this.kredyt_kupiecki = kredyt_kupiecki;
		this.id_kontrachent = id;
		this.dlug_pln = dlug_pln;
		this.dlug_eur = dlug_eur;
		this.dlug_usd = dlug_usd;
		this.dlug_uah = dlug_uah;
		this.nadplata_pln = nadplata_pln;
		this.nadplata_eur = nadplata_eur;
		this.nadplata_usd = nadplata_usd;
		this.nadplata_uah = nadplata_uah;
	}

	@Override
	public String[] piszWierszTabeli()
	{
		String[] wiersz = new String[4];
		wiersz[0] = nazwa;
		wiersz[1] = miasto;
		wiersz[2] = nip;
		wiersz[3] = "" + id_kontrachent;
		return wiersz;
	}

	@Override
	public int getId()
	{
		return id_kontrachent;
	}

	@Override
	public String[] getKolumnyPrzeszukiwania()
	{
		return kolumnyPrzeszukiwania;
	}

	private static StrukturaWarunku warunekWidoczny = new StrukturaWarunku(1,
			"widoczny", false);

	@Override
	public StrukturaWarunku getWarunekWidoczny()
	{
		return warunekWidoczny;
	}

	public String piszDoFaktury()
	{
		return nazwa + "\n" + ulica + "\n" + kod_pocztowy + " " + miasto
				+ "\nNIP: " + nip;
	}
}