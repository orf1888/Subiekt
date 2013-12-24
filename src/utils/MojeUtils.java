package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import model.ObiektWiersz;

public class MojeUtils
{
	public static final boolean systemOutEnable = false;

	public static void println(String s)
	{
		if (systemOutEnable)
			System.out.println(s);
	}

	public static boolean equals(String a, String b)
	{
		if (a == null || b == null)
			return false;
		return a.equals(b);
	}

	public static void showPrintError(Exception e)
	{
		showError(e.getLocalizedMessage());
		e.printStackTrace();
	}

	public static void showMsg(String str)
	{
		JOptionPane.showMessageDialog(null, str, "Informacja",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showError(String str)
	{
		JOptionPane.showMessageDialog(null, str, "Błąd",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * sprawdza czy str jest numerem (bez minusów, ułamków itd.)
	 */
	public static boolean isNumer(String str)
	{
		return str.matches("\\d+");
	}

	/**
	 * @param isKorekta
	 * @param int typFaktury przedrostek(1-FS, 2-FZ, 3-FK)
	 * @param int nr - nr porządkowy faktury
	 * @return String numer faktury z odpowiednim przedroskiem
	 */
	public static String poprawNrFaktury(int typFaktury, int nr, String rok,
			boolean isKorekta)
	{
		String rodzaj;
		if (typFaktury == 1)
			rodzaj = "FS";
		else if (typFaktury == 2)
			rodzaj = "FZ";
		else
			throw new NullPointerException();
		if (isKorekta)
			rodzaj += "K";
		return new String(rodzaj + "/" + nr + "/" + rok);

	}

	/**
	 * Zwraca wartość w formacie zz,gg
	 * 
	 * @param String
	 *            cena
	 * @return int cena
	 */
	public static String formatujWartosc(int wartosc)
	{
		int groszy = (wartosc % 100);
		String strGroszy = (groszy < 10) ? "0" + groszy : groszy + "";
		return new String("" + (wartosc / 100) + "," + strGroszy);

	}

	/**
	 * parsuje przypadki 100 i 100.00 i 100,00
	 * 
	 * @param wartosc
	 * @param nazwaPola
	 *            - w przypadku kilku pol na formatce mozemy okreslic ktorej
	 *            dotyczy blad
	 * @return - wartosc w groszach
	 * @throws Exception
	 */
	public static int utworzWartoscGrosze(String wartosc, String nazwaPola)
			throws Exception
	{
		boolean przecinek = wartosc.contains(",");
		boolean kropka = wartosc.contains(".");
		if (przecinek && kropka)
			throw new UserShowException(
					nazwaPola
							+ " - wpisz wartość tylko z przecinkiem lub tylko z kropką!");
		if (przecinek || kropka)
		{
			String[] podzielone = (przecinek) ? wartosc.split(",") : wartosc
					.split("\\.");
			if (podzielone.length > 2)
				throw new UserShowException(
						nazwaPola
								+ " - wpisz wartość tylko z przecinkiem lub tylko z kropką!");

			int zlotowki = Walidator.parsujInteger(podzielone[0], nazwaPola);
			int grosze = 0;
			if (podzielone.length > 1)
			{
				if (podzielone[1].length() > 2)
					throw new UserShowException(
							"Podano niepoprawna liczbe groszy");
				if (podzielone[1].length() == 2)
					grosze = (podzielone[1].charAt(0) - '0') * 10
							+ podzielone[1].charAt(1) - '0';
				if (podzielone[1].length() == 1)
					grosze = (podzielone[1].charAt(0) - '0') * 10;
			}
			return zlotowki * 100 + grosze;
		} else
		{
			/* zawiera tylko zlotowki */
			int zlotowki = Walidator.parsujInteger(wartosc, nazwaPola);
			return zlotowki * 100;
		}
	}

	public static String utworzWartoscZlotowki(int grosze)
	{
		String prefix = (grosze < 0) ? "-" : "";
		if (grosze < 0)
			grosze = -grosze;
		int obcieteGrosze = grosze % 100;
		if (obcieteGrosze < 10)
			return prefix + (grosze / 100) + ",0" + (obcieteGrosze);
		else
			return prefix + (grosze / 100) + "," + (obcieteGrosze);
	}

	static DateFormat stringToDate_format = new SimpleDateFormat(
			"dd MMM yyyy kk:mm:ss ZZZ", Locale.US);

	static DateFormat stringToDate_formatEnglish = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

	static DateFormat dateToString_format = new SimpleDateFormat("dd-MM-yyyy",
			Locale.US);

	public static String formatujDate(String string)
	{
		try
		{
			return dateToString_format
					.format(stringToDate_format.parse(string));
		} catch (Exception e)
		{
			try
			{
				return dateToString_format.format(stringToDate_formatEnglish
						.parse(string));
			} catch (Exception ex)
			{
				return string.substring(0, 11);
			}
		}
	}

	public static Date parsujDate(String string)
	{
		@SuppressWarnings("deprecation")
		Date d = new Date(Date.parse(string));
		return d;
	}

	public static void showError(Exception e)
	{
		showError(e.getMessage());
	}

	public static void kopiujPlik(File source, File destination)
			throws IOException
	{
		FileInputStream input = new FileInputStream(source);
		kopiujPlik(input, destination);
	}

	public static void kopiujPlik(InputStream input, File destination)
			throws IOException
	{
		OutputStream output = null;
		try
		{
			output = new FileOutputStream(destination);
			byte[] buffer = new byte[1024];
			int bytesRead = input.read(buffer);
			while (bytesRead >= 0)
			{
				output.write(buffer, 0, bytesRead);
				bytesRead = input.read(buffer);
			}
		} catch (Exception e)
		{
			MojeUtils.showPrintError(e);
		} finally
		{
			input.close();
			output.close();
		}
		input = null;
		output = null;
	}

	public static void zapiszPlik(File plik, String rozszerzenie, boolean backup)
			throws Exception
	{
		JFileChooser chooser = new JFileChooser(".");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new FiltrPlikow(rozszerzenie));
		int retrival = chooser.showSaveDialog(null);
		if (retrival == JFileChooser.APPROVE_OPTION)
		{
			try
			{
				String s = chooser.getSelectedFile().toString();
				if (!s.endsWith("." + rozszerzenie))
					if (backup)
						s += "-" + formatujDate(pobierzAktualnaDate()) + "."
								+ rozszerzenie;
					else
						s += "." + rozszerzenie;
				File nowyPlik = new File(s);
				kopiujPlik(plik, nowyPlik);
			} catch (Exception ex)
			{
				MojeUtils.showPrintError(ex);
			}
		} else
			throw new Exception("Anulowano");
	}

	public static boolean backup()
	{
		File bazaTmp = new File("db//oksana_subiekt_gtc.db");
		try
		{
			zapiszPlik(bazaTmp, "db", true);
		} catch (Exception e)
		{
			if (e.getMessage().equals("Anulowano"))
				return false;
		}
		return true;
	}

	public static void zamknijSystem()
	{
		int wybor = dialogTakNie("Kończenie pracy",
				"Czy chcesz przeprowadzić backup bazy danych przed zamknięciem programu?");
		if (wybor == 0)
		{
			if (MojeUtils.backup())
				System.exit(0);
			else
				return;
		} else if (wybor == 1)
			System.exit(0);
		else
			return;
	}

	public static void oProgramie()
	{
		JOptionPane
				.showMessageDialog(
						null,
						"                                 "
								+ Globals.WersjaAplikacji
								+ "\nWysyłając sms o terści \"POMAGAM\" na nr 7322 za 1,23 PLN"
								+ "\nuzyskujesz dostęp do jedynej działającej wersji oraz pomagasz"
								+ "\nautorowi przetrwać zimę.",
						"O programie...", JOptionPane.INFORMATION_MESSAGE, null);
	}

	public static void oAutorach()
	{
		JOptionPane.showMessageDialog(null, "Autorem aplikacji jest:\n\n"
				+ "\nSerhii Khilinich\n", "O autorach...",
				JOptionPane.INFORMATION_MESSAGE, null);
	}

	public static void tetris()
	{
		Tetris.intit();
	}

	public static File wczytajPlik(String rozszerzenie) throws Exception
	{
		JFileChooser chooser = new JFileChooser(".");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new FiltrPlikow(rozszerzenie));
		int retrival = chooser.showOpenDialog(null);
		if (retrival == JFileChooser.APPROVE_OPTION)
		{
			try
			{
				String s = chooser.getSelectedFile().toString();
				if (!s.endsWith("." + rozszerzenie))
					s += "." + rozszerzenie;
				return new File(s);
			} catch (Exception ex)
			{
				MojeUtils.showPrintError(ex);
				return null;
			}
		} else
			throw new Exception("Anulowano");
	}

	public static String zliczWartoscFakturDoRaportu(String[][] dane)
	{
		double wartosc = 0;
		String tmp = new String();
		for (int i = 0; i < dane.length; i++)
		{
			tmp = dane[i][4].substring(0, (dane[i][4].length() - 4))
					.replaceAll(",", ".");
			wartosc += Double.parseDouble(tmp);
		}
		return new String(wartosc + "").replace('.', ',');
	}

	@SuppressWarnings("deprecation")
	public static String pobierzAktualnaDate()
	{
		Date d = new Date(new Date().getTime());
		return d.toGMTString();
	}

	public static int dialogTakNie(String komunikat_naglowek,
			String komunikat_contetnt)
	{
		Object[] polskiePrzyciski =
		{ "Tak", "Nie" };
		int wybor = JOptionPane.showOptionDialog(null, komunikat_contetnt,
				komunikat_naglowek, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, polskiePrzyciski,
				polskiePrzyciski[1]);
		return wybor;
	}

	public static boolean usunWiersz(String komunikat, ObiektWiersz wiersz)
	{
		String co_usuwamy = "\n\n";
		co_usuwamy += wiersz.wiersz[0] + ", " + wiersz.wiersz[1];
		if (dialogTakNie("Usuwanie z bazy", komunikat + co_usuwamy) == 1)
			return false;
		else
			return true;
	}

	public static ComboBoxModel<Object> odswiezDaneWCombo(
			boolean z_pustym_elementem, String[] dane) throws SQLException
	{
		JComboBox<Object> tmp;
		if (z_pustym_elementem)
		{
			String[] data = new String[dane.length + 1];
			for (int i = 0; i < dane.length; ++i)
				data[1 + i] = dane[i];
			data[0] = "";
			tmp = new JComboBox<Object>(data);
			return tmp.getModel();

		} else
		{
			tmp = new JComboBox<Object>(dane);
			return tmp.getModel();
		}
	}

	public static String poprawDate(String data)
	{
		if (data.length() == 23)
			return new String(0 + data);
		return data;
	}

	public static List<String[]> sortujPoDacie(List<String[]> lista)
	{
		for (int i = 0; i < lista.size(); i++)
		{
			for (int j = 0; j < lista.size() - 1 - i; j++)
			{
				Date el = parsujDate(lista.get(j)[1]);
				Date el2 = parsujDate(lista.get(j + 1)[1]);
				if (el.after(el2))
				{
					String[] tmp = lista.get(j + 1);
					lista.set(j + 1, lista.get(j));
					lista.set(i, tmp);
				}
			}
		}
		return lista;
	}
}