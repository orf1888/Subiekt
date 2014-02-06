package kontroler;

import java.sql.ResultSet;
import java.sql.SQLException;

import model.Sorter;
import utils.BazaDanych;
import utils.BazaStatementFunktor;
import utils.SqlUtils;

public class SorterKontroler
{

	static BazaStatementFunktor pobieranieWierszaFunktor = new BazaStatementFunktor()
	{
		@Override
		public Object operacja(ResultSet result) throws SQLException
		{
			Sorter s = new Sorter(null, null, null);
			while (result.next())
			{
				s.sorterFS = result.getString(1);
				s.sorterFZ = result.getString(2);
				s.sorterW = result.getString(3);
			}
			return s;
		}
	};

	public static Sorter getSorter() throws SQLException
	{
		String querySql = "SELECT " + Sorter.kolumnyWBazie[0] + ", "
				+ Sorter.kolumnyWBazie[1] + ", " + Sorter.kolumnyWBazie[2]
				+ " FROM " + Sorter.tableName + " WHERE id= "
				+ SqlUtils.popraw(1);
		return (Sorter) BazaDanych.getInstance().zapytanie(querySql,
				pobieranieWierszaFunktor);
	}

	public static void edytuj(Sorter sorter) throws SQLException
	{
		String update = new String();
		if (sorter.sorterFS == null || sorter.sorterFZ == null
				|| sorter.sorterW == null)
			return;
		if (sorter.sorterFS != null)
			update += "UPDATE " + Sorter.tableName + " set "
					+ Sorter.kolumnyWBazie[0] + "= '"
					+ SqlUtils.popraw(sorter.sorterFS) + "', ";
		if (sorter.sorterFZ != null)
			update += " " + Sorter.kolumnyWBazie[1] + "= '"
					+ SqlUtils.popraw(sorter.sorterFZ) + "', ";
		if (sorter.sorterW != null)
			update += " " + Sorter.kolumnyWBazie[2] + "= '"
					+ SqlUtils.popraw(sorter.sorterW) + "' ";
		update += "WHERE id = " + SqlUtils.popraw(sorter.id);

		BazaDanych.getInstance().aktualizacja(update);
	}
}
