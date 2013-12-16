package utils;

public class SqlUtils
{

	public static String popraw( String obj )
	{
		if ( obj == null )
			return "null";
		else
			return obj.replace( '\"', ' ' ).replace( '\'', ' ' ).replace( '\\', ' ' );
	}

	public static String popraw( int obj )
	{
		return "" + obj;
	}

	public static String popraw( long obj )
	{
		return "" + obj;
	}

	public static String popraw( Number obj )
	{
		return "" + obj;
	}

	public static String popraw( Boolean obj )
	{
		return popraw( Boolean.TRUE.equals( obj ) );
	}

	public static String popraw( boolean obj )
	{
		return obj ? "1"
				: "0";
	}

	/**
	 * Asterisk= gwiazdka (dowolny ciąg znaków)
	 */
	public static String poprawZAsteryskem( String obj )
	{
		return popraw( obj ).replace( '*', '%' );
	}

}
