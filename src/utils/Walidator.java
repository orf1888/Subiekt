package utils;

public class Walidator
{
	public static class WalidatorAlgorytmy
	{
		static final int regon_wagi9[] =
		{ 8, 9, 2, 3, 4, 5, 6, 7 };

		static final int regon_wagi14[] =
		{ 2, 4, 8, 5, 0, 9, 7, 3, 6, 1, 2, 4, 8 };

		static final int nip_wagi[] =
		{ 6, 5, 7, 2, 3, 4, 5, 6, 7 };

		private static boolean isValidRegon9Or14(String regon, int length,
				int[] wagi)
		{
			int suma = 0;
			int cyfraKontrolna = Character.getNumericValue(regon
					.charAt(length - 1));

			for (int i = 0; i < length - 1; ++i)
			{
				int liczba = Character.getNumericValue(regon.charAt(i));
				suma += liczba * wagi[i];
			}

			int control = suma % 11;
			if (control == 10)
				control = 0;

			return (control == cyfraKontrolna);
		}

		public static boolean isValidRegon(String regon)
		{
			int size = regon.length();

			if (size == 9)
			{
				return isValidRegon9Or14(regon, 9, regon_wagi9);
			} else if (size == 14)
			{
				return isValidRegon9Or14(regon, 9, regon_wagi9)
						&& isValidRegon9Or14(regon, 14, regon_wagi14);
			}
			return false;
		}

		public static boolean isValidNIP(String nip)
		{
			int size = nip.length();
			if (size != 10)
				return false;

			int suma = 0;
			int cyfraKontrolna = Character
					.getNumericValue(nip.charAt(size - 1));

			for (int i = 0; i < size - 1; ++i)
			{
				int liczba = Character.getNumericValue(nip.charAt(i));
				suma += liczba * nip_wagi[i];
			}

			int control = suma % 11;
			return (control == cyfraKontrolna);
		}
	}

	public static String walidujNIP(String text) throws Exception
	{
		/* usuwa nie-cyfry */
		text = text.replaceAll("\\D*", "");

		if (!WalidatorAlgorytmy.isValidNIP(text))
			throw new UserShowException("Niepoprawny NIP!");

		return text;
	}

	public static String walidujRegon(String text) throws Exception
	{
		/* usuwa nie-cyfry */
		text = text.replaceAll("\\D*", "");

		if (!WalidatorAlgorytmy.isValidRegon(text))
			throw new UserShowException("Niepoprawny REGON!");

		return text;
	}

	public static void walidujKodPocztowy(String kodPocztowy) throws Exception
	{
		if (!kodPocztowy.matches("\\d{2}-\\d{3}"))
			throw new UserShowException(
					"Podano niepoprawny kod pocztowy (użyj wyłącznie cyfr w postaci XX-XXX)");
	}

	public static int parsujInteger(String text, String nazwaPola)
			throws Exception
	{
		/* zmieniamy nazwe wyjatku na czytelny odpowiednik */
		try
		{
			return Integer.parseInt(text);
		} catch (Exception e)
		{
			throw new UserShowException(nazwaPola
					+ " - podano niepoprawną liczbę");
		}
	}

	public static void walidujNrKonta(String numerKonta)
			throws UserShowException
	{
		if (!MojeUtils.isNumer(numerKonta))
			throw new UserShowException(
					"Nuemr konta powinien składać się wyłącznie z cyfr!");
		if (numerKonta.length() != 26)
			throw new UserShowException("Numer konta powinien mieć 26 cyfr!");
	}
}