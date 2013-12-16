package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BazaDanych
{
	private static final int MaxQueryTimeout = 300;

	static Connection connection;

	static BazaDanych baza_danych;


	public static void init() throws ClassNotFoundException, SQLException
	{
		baza_danych = new BazaDanych();
		Class.forName( "org.sqlite.JDBC" );
		connection = DriverManager.getConnection( "jdbc:sqlite:db//oksana_subiekt_gtc.db" );
	}

	public Object zapytanie( String dane, BazaStatementFunktor bazaStatementFunktor ) throws SQLException
	{
		try {
			Statement statement = connection.createStatement();
			statement.setQueryTimeout( 30 );
			ResultSet result = statement.executeQuery( dane );
			return bazaStatementFunktor.operacja( result );
		}
		catch ( SQLException e ) {
			MojeUtils.println( "zapytanie Query= " + dane );
			throw e;
		}
	}

	public void aktualizacja( String dane ) throws SQLException
	{
		MojeUtils.println( "aktualizacja Query= " + dane );
		try {
			Statement statement = connection.createStatement();
			statement.setQueryTimeout( 30 );
			statement.executeUpdate( dane );
		}
		catch ( SQLException e ) {
			System.err.println( "aktualizacja Query= " + dane );
			throw e;
		}
	}

	/**
	 * wykonuje podane zapytanie typu INSERT
	 * 
	 * @return wygenerowany ID dla inserta
	 * @throws Exception 
	 */
	public int wstaw( String dane ) throws Exception
	{
		MojeUtils.println( "WSTAW = " + dane );
		try {
			Statement statement = connection.createStatement();
			statement.setQueryTimeout( MaxQueryTimeout );
			statement.execute( dane );//, Statement.RETURN_GENERATED_KEYS );
			ResultSet keyset = statement.getGeneratedKeys();
			return keyset.getInt( 1 );
		}
		catch ( Exception e ) {
			MojeUtils.println( "wstaw Query= " + dane );
			throw e;
		}
	}

	public static BazaDanych getInstance()
	{
		return baza_danych;
	}
}
