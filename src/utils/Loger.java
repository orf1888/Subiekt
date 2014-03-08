package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Loger
{
	public enum LogerNazwa
	{
		FinanseLog("log\\finanse.txt"), BledyLog("log\\err.txt");

		LogerNazwa(String _sciezka) {
			sciezka = _sciezka;
		}

		public String sciezka;
	};

	public static void log(LogerNazwa rodzaj, String logContent)
			throws IOException
	{
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
}
