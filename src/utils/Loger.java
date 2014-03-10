package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import kontroler.KontrachentBaza;
import kontroler.WalutaManager;
import model.Faktura;
import model.Kontrachent;
import model.Wplata;

public class Loger
{
	private static String katalog = "log";

	public enum LogerNazwa
	{
		FinanseLog(katalog + "\\finanse.txt"), BledyLog(katalog + "\\err.txt");

		LogerNazwa(String _sciezka) {
			sciezka = _sciezka;
		}

		public String sciezka;
	};

	public static void log(LogerNazwa rodzaj, String logContent)
			throws IOException
	{
		tworzKatalogJesliNieIstnieje();
		String info = getTime() + " --> ";
		info += rodzaj + " ";
		switch (rodzaj)
		{
		case FinanseLog:
		{
			piszDoPliku(rodzaj.sciezka, info + logContent);
			break;
		}
		case BledyLog:
		{
			piszDoPliku(rodzaj.sciezka, info + logContent);
			break;
		}
		default:
			MojeUtils.showError("Nieznany rodzaj log'a!!!");
		}
	}

	public static String tworzLog(Object stary, Object nowy)
	{
		if (stary == null)
			/* Dodanie faktury/wpłaty */
			return pobierzDane(nowy, false);
		else
		{
			/* Edycja faktury/wpłaty */
			String result = pobierzDane(stary, true);
			result += "\n" + pobierzDane(nowy, true);
			return result;
		}
	}

	private static String pobierzDane(Object nowy, boolean isEdytuj)
	{
		String waluta = new String();
		String result = new String();
		if (nowy instanceof Faktura)
		{
			waluta = WalutaManager.pobierzNazweZBazy(
					((Faktura) nowy).waluta - 1, "slownikWaluta");
			if (isEdytuj)
				result += "Edytowano fakturę o nr ";
			else
				result += "Dodano fakturę o nr ";

			result += ((Faktura) nowy).numer
					+ " dla kontrachenta "
					+ ((Faktura) nowy).kontrahent.nazwa
					+ " o wartości "
					+ MojeUtils
							.utworzWartoscZlotowki(((Faktura) nowy).wartosc_z_narzutem)
					+ " w walucie "
					+ waluta
					+ " długi ("
					+ MojeUtils
							.utworzWartoscZlotowki(((Faktura) nowy).kontrahent.dlug_pln)
					+ "PLN;"
					+ MojeUtils
							.utworzWartoscZlotowki(((Faktura) nowy).kontrahent.dlug_eur)
					+ "EUR;"
					+ MojeUtils
							.utworzWartoscZlotowki(((Faktura) nowy).kontrahent.dlug_usd)
					+ "USD;"
					+ MojeUtils
							.utworzWartoscZlotowki(((Faktura) nowy).kontrahent.dlug_uah)
					+ "UAH)"
					+ ", nadpłaty("
					+ MojeUtils
							.utworzWartoscZlotowki(((Faktura) nowy).kontrahent.nadplata_pln)
					+ "PLN;"
					+ MojeUtils
							.utworzWartoscZlotowki(((Faktura) nowy).kontrahent.nadplata_eur)
					+ "EUR;"
					+ MojeUtils
							.utworzWartoscZlotowki(((Faktura) nowy).kontrahent.nadplata_usd)
					+ "USD;"
					+ MojeUtils
							.utworzWartoscZlotowki(((Faktura) nowy).kontrahent.nadplata_uah)
					+ "UAH)";
		} else
		{
			waluta = WalutaManager.pobierzNazweZBazy(
					(long) ((Wplata) nowy).waluta - 1, "slownikWaluta");
			Kontrachent kontrachent = (Kontrachent) KontrachentBaza
					.pobierzObiektZBazy(((Wplata) nowy).id_kontrachent);
			if (isEdytuj)
				result += "Edytowano wpłatę o id ";
			else
				result += "Dodano wpłatę o id ";
			result += ((Wplata) nowy).id_wplata + " dla kontrachenta "
					+ kontrachent.nazwa + " o wartości "
					+ MojeUtils.utworzWartoscZlotowki(((Wplata) nowy).wartosc)
					+ " w walucie " + waluta + " długi ("
					+ MojeUtils.utworzWartoscZlotowki(kontrachent.dlug_pln)
					+ "PLN;"
					+ MojeUtils.utworzWartoscZlotowki(kontrachent.dlug_eur)
					+ "EUR;"
					+ MojeUtils.utworzWartoscZlotowki(kontrachent.dlug_usd)
					+ "USD;"
					+ MojeUtils.utworzWartoscZlotowki(kontrachent.dlug_uah)
					+ "UAH)" + "," + "nadpłaty("
					+ MojeUtils.utworzWartoscZlotowki(kontrachent.nadplata_pln)
					+ "PLN;"
					+ MojeUtils.utworzWartoscZlotowki(kontrachent.nadplata_eur)
					+ "EUR;"
					+ MojeUtils.utworzWartoscZlotowki(kontrachent.nadplata_usd)
					+ "USD;"
					+ MojeUtils.utworzWartoscZlotowki(kontrachent.nadplata_uah)
					+ "UAH)";
		}
		return result;
	}

	private static void piszDoPliku(String sciezka, String logContent)
			throws IOException
	{
		FileWriter plik = new FileWriter(sciezka, true);
		plik.write(logContent + "\n");
		plik.close();
	}

	private static String getTime()
	{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar
				.getInstance().getTime());
	}

	public static String pobierzStackStrace(Exception e)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	private static void tworzKatalogJesliNieIstnieje()
	{
		File dir = new File(katalog);
		if (!dir.exists())
			dir.mkdir();
		else
			return;
	}
}
