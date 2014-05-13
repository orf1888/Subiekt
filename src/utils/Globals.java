package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.ImageIcon;

public class Globals
{
	static
	{
		Properties prop = new Properties();
		try (InputStream in = Globals.class.getResourceAsStream("version"))
		{
			prop.load(in);
		} catch (IOException e)
		{
			throw new RuntimeException(e);

		}
		WersjaAplikacji = "Subiekt GTC v." + prop.getProperty("vesion");
	}

	public final static boolean Widoczny = true;

	public final static boolean Niewidoczny = false;

	public final static Boolean WidocznyINiewidoczny = null;

	public final static boolean SzukajLike = true;

	public final static boolean SzukajRownaSie = false;

	public final static String WersjaAplikacji;

	public final static ImageIcon IkonaAplikacji = new ImageIcon(Globals.class
			.getClassLoader().getResource("sGTC.gif"));
}
