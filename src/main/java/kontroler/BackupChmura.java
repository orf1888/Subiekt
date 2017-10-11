package kontroler;

import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import model.Chmura;
import utils.DataUtils;
import utils.MojeUtils;
import widok.WidokGlowny;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BackupChmura {

    private static DbxAuthFinish authFinish;
    private static DbxClientV2 client;
    private static final String CLOUD_LOCATION = "db//oksana_subiekt_gtc.db";

    public static void archiwizuj(Chmura chmura) throws Exception {
        autoryzuj(chmura);
        zapiszPlikWChmurze();
    }

    private static void autoryzuj(Chmura chmura) throws DbxException, Exception {
        MojeUtils.showMsg("Za chwilę włączy się przeglądarka.\nPobierz wygenerowany kod i wklej w nowe okno!");
        String authorizeUrl = chmura.getWebAuth().start();
        try {
            wlaczPrzegladarke(authorizeUrl);
        } catch (Exception e) {
            throw new Exception("Nieudało się właczyć przeglądarki!");
        }
        String code = null;
        try {
            code = pobierzKod();
        } catch (Exception e) {
            throw new Exception("Anulowano");
        }
        try {
            authFinish = chmura.getWebAuth().finish(code);
        } catch (Exception e) {
            throw new Exception("Wpisany kod jest niepoprawny!");
        }
        client = new DbxClientV2(chmura.getConfig(), authFinish.getAccessToken());
    }

    private static void zapiszPlikWChmurze() throws Exception {
        File bazaTmp = new File(CLOUD_LOCATION);
        FileInputStream inputStream = new FileInputStream(bazaTmp);
        try {
            FileMetadata fileMetadata = client.files()
                    .uploadBuilder("/" + bazaTmp.getName() + "_" + DataUtils.pobierzAktualnaDate())
                    .uploadAndFinish(inputStream);
            MojeUtils.showMsg(fileMetadata.getName() + " zapisano pomyślnie.");
        } finally {
            inputStream.close();
        }
    }

    private static String pobierzKod() throws Exception {
        Object[] polskiePrzyciski = { "OK", "Anuluj" };
        JTextField kod = new JTextField();
        Object[] params_chmura = { "Wpisz kod z przeglądarki", kod };
        int wybor = JOptionPane
                .showOptionDialog(WidokGlowny.frame, params_chmura, "Wpisz kod", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, polskiePrzyciski, polskiePrzyciski[1]);
        if (wybor == 0)
            return kod.getText().trim();
        else
            throw new Exception("Anulowano");
    }

    private static void wlaczPrzegladarke(String authorizeUrl) throws IOException, InterruptedException {
        Desktop.getDesktop().browse(java.net.URI.create(authorizeUrl));
    }
}