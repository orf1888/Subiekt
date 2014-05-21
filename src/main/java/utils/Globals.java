package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.ImageIcon;

public class Globals
{
	static
	{
		Properties prop = new Properties();
		try (InputStream in = Globals.class.getClassLoader()
				.getResourceAsStream("version"))
		{
			prop.load(in);
		} catch (IOException e)
		{
			throw new RuntimeException(e);

		}
		WersjaAplikacji = "Subiekt GTC v." + prop.getProperty("version");
	}

	public final static boolean Widoczny = true;

	public final static boolean Niewidoczny = false;

	public final static Boolean WidocznyINiewidoczny = null;

	public final static boolean SzukajLike = true;

	public final static boolean SzukajRownaSie = false;

	public final static String WersjaAplikacji;

	public final static ImageIcon IkonaAplikacji = new ImageIcon(Globals.class
			.getClassLoader().getResource("sGTC.gif"));

	public static final Properties properties = new Properties();

	static
	{
		try
		{
			properties.load(new FileInputStream(".properties"));
		} catch (IOException e)
		{
			throw new UserShowException("Wyjątek krytyczny!", e);
		}
	}

	public final static String UpdateBat = properties.getProperty("bat");

}
