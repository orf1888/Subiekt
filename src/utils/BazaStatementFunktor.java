package utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BazaStatementFunktor
{
	public abstract Object operacja(ResultSet result) throws SQLException;
}