package kontroler;

import java.sql.ResultSet;

import model.Podmiot;
import utils.BazaDanych;
import utils.BazaStatementFunktor;
import utils.MojeUtils;
import utils.SqlUtils;

public class PodmiotBaza
{
	public static Podmiot pobierzPodmiot()
	{
		try
		{
			return (Podmiot) BazaDanych.getInstance().zapytanie(
					"SELECT nazwa, ulica, kod_pocztowy, miasto, nip FROM podmiot"
							+ " WHERE id= " + "1", new BazaStatementFunktor()
					{

						@Override
						public Object operacja(ResultSet result)
								throws Exception
						{
							return new Podmiot(result.getString(1), result
									.getString(2), result.getString(3), result
									.getString(4), result.getString(5));
						}
					});
		} catch (Exception e)
		{
			MojeUtils.error(e);
			return null;
		}
	}

	public static void edytuj(Podmiot p) throws Exception
	{

		BazaDanych.getInstance().aktualizacja(
				"UPDATE podmiot set nazwa= '" + SqlUtils.popraw(p.nazwa)
						+ "', ulica= '" + SqlUtils.popraw(p.ulica)
						+ "', kod_pocztowy= '"
						+ SqlUtils.popraw(p.kod_pocztowy) + "', miasto= '"
						+ SqlUtils.popraw(p.miasto) + "', nip= '"
						+ SqlUtils.popraw(p.nip) + "' WHERE id= 1");
	}
}
