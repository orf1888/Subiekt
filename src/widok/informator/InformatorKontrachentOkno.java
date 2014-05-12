package widok.informator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import kontroler.FakturaBaza;
import model.Faktura;
import model.ObiektWiersz;
import utils.DataUtils;
import utils.Globals;
import utils.MojeUtils;
import widok.WyswietlPDFPanel;

public class InformatorKontrachentOkno extends JDialog
{
	private static final long serialVersionUID = -8610511076795289404L;
	private final JPanel contentPanel = new JPanel();
	private final PanelPrzyciskowInformator panelPrzyciskow;
	private final JPanel panelPDF;
	// tmp
	int click = 0;

	public InformatorKontrachentOkno(String nazwa, ObiektWiersz wiersz) {
		/* init settings */
		setModalityType(ModalityType.APPLICATION_MODAL);
		setIconImage(Globals.IkonaAplikacji.getImage());
		setTitle("Informator - " + nazwa);
		setSize(910, 600);
		setLocationRelativeTo(null);
		setResizable(false);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panelPrzyciskow = new PanelPrzyciskowInformator(
				tworzPokazFakturyZaOkres(wiersz), false, true);
		panelPDF = new JPanel();
		contentPanel.add(panelPDF, BorderLayout.CENTER);
		contentPanel.add(panelPrzyciskow, BorderLayout.EAST);
		add(contentPanel);
	}

	public static void init(String nazwa, ObiektWiersz wiersz)
	{
		InformatorKontrachentOkno informator = new InformatorKontrachentOkno(
				nazwa, wiersz);
		informator.setVisible(true);
	}

	private ActionListener tworzPokazFakturyZaOkres(final ObiektWiersz wiersz)
	{
		return new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				czyscFormatke();
				WyswietlPDFPanel panel = new WyswietlPDFPanel();
				String dataOd = DataUtils.stringToDate_format
						.format(panelPrzyciskow.dataOd.getDate());
				String dataDo = DataUtils.stringToDate_format
						.format(panelPrzyciskow.dataDo.getDate());
				String[] daty = new String[]
				{ dataOd.substring(0, dataOd.length() - 6),
						dataDo.substring(0, dataDo.length() - 6) };
				try
				{
					contentPanel
							.add(panel.uzupelnijtworzFakturyKontrachentowZaOkres(
									(ArrayList<Faktura>) FakturaBaza.pobierzFakturyZaOkres(
											Integer.parseInt(wiersz.wiersz[3]),
											panelPrzyciskow.isChxSelected(),
											daty), daty), BorderLayout.CENTER);
					invalidate();
					validate();
					repaint();
				} catch (Exception e1)
				{
					invalidate();
					validate();
					repaint();

					if (!e1.getMessage().equals("Brak faktur"))
						MojeUtils.showError("Błąd utworzenia wykazu faktur!");
					MojeUtils.showMsg("Brak faktur dla " + wiersz.wiersz[0]
							+ " za ("
							+ dataOd.substring(0, dataOd.length() - 6) + " - "
							+ dataDo.substring(0, dataDo.length() - 6) + ")");
				}
			}
		};
	}

	private void czyscFormatke()
	{
		contentPanel.removeAll();
		contentPanel.add(panelPrzyciskow, BorderLayout.EAST);
	}
}
