package kontroler;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import model.Chmura;
import utils.MojeUtils;
import widok.WidokGlowny;

import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

public class BackupChmura
{
	private static DbxAuthFinish authFinish;
	private static DbxClient client;

	public static void archiwizuj(Chmura chmura) throws Exception
	{
		autoryzuj(chmura);
		zapiszPlikWChmurze();
	}

	private static void autoryzuj(Chmura chmura) throws DbxException, Exception
	{
		MojeUtils
				.showMsg("Za chwilę włączy się przeglądarka.\nPobierz wygenerowany kod i wklej w nowe okno!");
		String authorizeUrl = chmura.getWebAuth().start();
		try
		{
			wlaczPrzegladarke(authorizeUrl);
		} catch (Exception e)
		{
			throw new Exception("Nieudało się właczyć przeglądarki!");
		}
		String code = null;
		try
		{
			code = pobierzKod();
		} catch (Exception e)
		{
			throw new Exception("Anulowano");
		}
		try
		{
			authFinish = chmura.getWebAuth().finish(code);
		} catch (Exception e)
		{
			throw new Exception("Wpisany kod jest niepoprawny!");
		}
		client = new DbxClient(chmura.getConfig(), authFinish.accessToken);
	}

	private static void zapiszPlikWChmurze() throws Exception
	{
		File bazaTmp = new File("db//oksana_subiekt_gtc.db");
		FileInputStream inputStream = new FileInputStream(bazaTmp);
		DbxEntry.File uploadedFile;
		try
		{
			uploadedFile = client.uploadFile("/" + bazaTmp.getName(),
					DbxWriteMode.add(), bazaTmp.length(), inputStream);
		} finally
		{
			inputStream.close();
		}
		MojeUtils.showMsg(uploadedFile.name + " zapisano pomyślnie.");
	}

	private static String pobierzKod() throws Exception
	{
		Object[] polskiePrzyciski =
		{ "OK", "Anuluj" };
		JTextField kod = new JTextField();
		Object[] params_chmura =
		{ "Wpisz kod z przeglądarki", kod };
		int wybor = JOptionPane.showOptionDialog(WidokGlowny.frame,
				params_chmura, "Wpisz kod", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, polskiePrzyciski,
				polskiePrzyciski[1]);
		if (wybor == 0)
			return kod.getText().trim();
		else
			throw new Exception("Anulowano");
	}

	private static void wlaczPrzegladarke(String authorizeUrl)
			throws IOException, InterruptedException
	{
		Desktop.getDesktop().browse(java.net.URI.create(authorizeUrl));
	}
}