package utils;

import java.sql.ResultSet;

public abstract class BazaStatementFunktor
{
	public abstract Object operacja(ResultSet result) throws Exception;
}