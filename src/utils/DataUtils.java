package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataUtils
{
	public static String pierwszy_dzien_roku = "-01-01";
	public static String ostatni_dzien_roku = "-01-31";

	public static DateFormat stringToDate_format = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm", Locale.US);

	static DateFormat stringToDate_formatEnglish = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

	public static DateFormat dateToString_format = new SimpleDateFormat(
			"dd-mm-yyyy", Locale.US);

	public static String formatujDate(String string)
	{
		try
		{
			return dateToString_format.format(dateToString_format
					.parse(getParsableDate(string)));
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

	public static String[][] foratujDate(String[][] data)
	{
		for (int i = 0; i < data.length; i++)
		{
			data[i][1] = formatujDate(data[i][1]);
		}
		return data;
	}

	public static Date parsujDate(String string)
	{
		DateFormat format = new SimpleDateFormat("dd-mm-yyyy");
		Date d = null;
		try
		{
			d = format.parse(getParsableDate(string));
		} catch (ParseException e)
		{
			MojeUtils.showError(e);
		}
		return d;
	}

	private static String getParsableDate(String string)
	{
		/* 2014-01-25 12:01 */
		String result = new String();
		result += getDay(string);
		result += "-";
		result += getMonth(string);
		result += "-";
		result += getYear(string);
		return result;
	}

	private static String getDay(String string)
	{
		return string.substring(8, 10);
	}

	private static String getMonth(String string)
	{
		return string.substring(5, 7);
	}

	public static String getYear(String string)
	{
		return string.substring(0, 4);
	}

	public static List<String[]> sortujPoDacie(List<String[]> lista)
	{
		/* bubble sort */
		String[][] tmp = lista.toArray(new String[lista.size()][]);
		ArrayList<String[]> result = new ArrayList<>();
		int size = tmp.length - 1;
		for (int i = 0; i < tmp.length - 1; i++)
		{
			for (int j = 0; j < size; j++)
			{
				Date el = parsujDate(tmp[j][1]);
				Date el2 = parsujDate(tmp[j + 1][1]);
				if (el.after(el2))
				{
					String[] temp = new String[3];
					temp[0] = tmp[j][0];
					temp[1] = tmp[j][1];
					temp[2] = tmp[j][2];
					/**/
					tmp[j][0] = tmp[j + 1][0];
					tmp[j][1] = tmp[j + 1][1];
					tmp[j][2] = tmp[j + 1][2];
					/**/
					tmp[j + 1][0] = temp[0];
					tmp[j + 1][1] = temp[1];
					tmp[j + 1][2] = temp[2];
				}
			}
			size--;
		}
		result.addAll(Arrays.asList(tmp));
		return result;
	}

	public static String pierwszyDzienRoku(String data)
	{
		return getYear(data) + pierwszy_dzien_roku;
	}

	public static String ostatniDzienRoku(String data)
	{
		return getYear(data) + ostatni_dzien_roku;
	}

	public static String pobierzAktualnaDate()
	{
		Date d = new Date(new Date().getTime());
		return new SimpleDateFormat("dd-MM-yyyy").format(d);
	}

}