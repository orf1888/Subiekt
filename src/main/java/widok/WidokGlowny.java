package widok;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;

import kontroler.BackupChmura;
import kontroler.ProduktBaza;
import kontroler.UpdateProgressKontroler;
import model.Chmura;
import utils.BazaDanych;
import utils.FakturaZPliku;
import utils.Globals;
import utils.MojeUtils;
import utils.SprawdzMagazyn;
import widok.faktura.FakturySprzedazyPanel;
import widok.faktura.FakturyZakupuPanel;
import widok.kontrachent.KontrachentPanel;
import widok.podmiot.PodmiotOkno;
import widok.transport_rozliczenie.TransportRozliczeniePanel;
import widok.wplata.WplataPanel;
import widok.wysylka.WysylkaPanel;

public class WidokGlowny extends JFrame
{
	private static final long serialVersionUID = 1L;

	private final JPanel contentPane;

	public static WidokGlowny frame;

	public static void main(String[] args)
	{
		try
		{
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			{
				if ("Nimbus".equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e)
		{
			MojeUtils.showError(e);
		}
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					frame = new WidokGlowny();
					frame.setVisible(true);
				} catch (Exception e)
				{
					MojeUtils.error(e);
				}
			}
		});
	}

	public FakturySprzedazyPanel panelFakturSprz;
	public TransportRozliczeniePanel panelTransportRozliczenie;

	public WidokGlowny() throws Exception, ClassNotFoundException, IOException {
        /*Ustawmy wiekość czcionki dla "systemu opercyjnego" Window$ 8.1!!!!*/
        /*Kochany Windows!!!!!!!!*/
        setDefaultFontSize(20);
		/* Ustaw ikonę */
		setIconImage(Globals.IkonaAplikacji.getImage());
		/* Wersja aplikacji */
		setTitle(Globals.WersjaAplikacji);
		/* Zamukanie obsługuje odrębna metoda */
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		/* Ustaw rozmiar okna - nie full screen */
		setMinimumSize(new Dimension(800, 550));
		/* Ustaw full screen */
		setExtendedState(Frame.MAXIMIZED_BOTH);
		/* Menu bar */
		{
			JMenuBar menuBar = new JMenuBar();
			setJMenuBar(menuBar);
			{
				/* Plik */
				JMenu mnPlik = new JMenu("Plik");
				menuBar.add(mnPlik);

				JMenuItem mntmBackup = new JMenuItem(
						"Kopia zapasowa bazy danych");
				mnPlik.add(mntmBackup);
				mntmBackup.addActionListener(backupListener);

				JMenuItem mntmPodmiot = new JMenuItem("Edytuj dane podmiotu");
				mnPlik.add(mntmPodmiot);
				mntmPodmiot.addActionListener(edytujPodmiotListener);

				JMenuItem mntmWczytajTowary = new JMenuItem("Dodaj FS z pliku");
				mnPlik.add(mntmWczytajTowary);
				mntmWczytajTowary.addActionListener(dodajFSListener);

				JMenuItem mntmSprawdzOstatki = new JMenuItem("Sprawdź ostatki");
				mnPlik.add(mntmSprawdzOstatki);
				mntmSprawdzOstatki.addActionListener(sprawdzOstatkiListener);

				JMenuItem mntmZakocz = new JMenuItem("Zakończ");
				mnPlik.add(mntmZakocz);
				mntmZakocz.addActionListener(zakonczListener);
			}
			{
				/* Aktualizacje */
				JMenu mnAktualizacja = new JMenu("Aktualizacje");
				menuBar.add(mnAktualizacja);
				JMenuItem mntmAktualizuj = new JMenuItem("Aktualizuj");
				mnAktualizacja.add(mntmAktualizuj);
				mntmAktualizuj.addActionListener(aktualizujListener());
			}
			{
				/* O programie */
				JMenu mnInformacje = new JMenu("Informacje");
				menuBar.add(mnInformacje);
				JMenuItem mntmOProgramie = new JMenuItem("O programie");
				mnInformacje.add(mntmOProgramie);
				mntmOProgramie.addActionListener(oProgramieListener);
				JMenuItem mntmOAutrach = new JMenuItem("O autorach");
				mntmOAutrach.addActionListener(oAutorachListener);
				mnInformacje.add(mntmOAutrach);
			}
		}

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		BazaDanych.init();

		JTabbedPane tab = new JTabbedPane();
		tab.setTabPlacement(SwingConstants.LEFT);
		contentPane.add(tab, BorderLayout.CENTER);
		{
			MagazynPanel panelMagazynu = new MagazynPanel(
			/* wyswietlButtony */true, /* default popup */null, false);

			ProduktBaza.instance().setMagazynPanel(panelMagazynu);

			tab.addTab("Magazyn", null, panelMagazynu);
		}
		{
			KontrachentPanel panelkontrachentow = new KontrachentPanel(false);
			tab.addTab("Kontrachenci", null, panelkontrachentow);
		}
		{
			FakturyZakupuPanel panelFakturZakupu = new FakturyZakupuPanel();
			tab.addTab("Faktury zakupu", null, panelFakturZakupu);
		}
		{
			panelFakturSprz = new FakturySprzedazyPanel();
			tab.addTab("Faktury sprzedaży", null, panelFakturSprz);
		}
		{
			WysylkaPanel panelWysylka = new WysylkaPanel(true);
			tab.addTab("Wysyłki", null, panelWysylka);
		}
		{
			panelTransportRozliczenie = new TransportRozliczeniePanel(true);
			tab.addTab("Rachunki transportowe", null, panelTransportRozliczenie);
		}
		{
			WplataPanel panelWplata = new WplataPanel(true);
			tab.addTab("Wpłaty kontrachentów", null, panelWplata);
		}

		{
			JPanel panel_5 = new ZestawieniaPanel();
			tab.addTab("Zestawienia", null, panel_5);
		}
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				MojeUtils.zamknijSystem();
			}
		});

		/* easter ball's */
		InputMap input = contentPane
				.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "F1");
		ActionMap action = contentPane.getActionMap();
		action.put("F1", new AbstractAction()
		{

			private static final long serialVersionUID = 6216283626135394012L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				MojeUtils.tetris();
			}
		});

	}

	/* Listenery menu */
	private final ActionListener zakonczListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			MojeUtils.zamknijSystem();
		}

	};

	private final ActionListener backupListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (MojeUtils.dialogTakNie("Archiwizacja bazy danych",
					"Czy archiwizować w chmurze?") == 0)
				try
				{
					BackupChmura.archiwizuj(new Chmura());
				} catch (Exception e1)
				{
					if (e1.getMessage().equals("Anulowano"))
						return;
					else
						MojeUtils.showError(e1.getMessage()
								+ "\nArchiwizuj plik na dysku lokalnym!");
				}
			else
				MojeUtils.backup();
		}
	};

	private final ActionListener oProgramieListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			MojeUtils.oProgramie();
		}
	};

	private final ActionListener oAutorachListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			MojeUtils.oAutorach();
		}
	};

	private final ActionListener edytujPodmiotListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				PodmiotOkno ok = new PodmiotOkno();
				ok.setVisible(true);
			} catch (Exception e1)
			{
				MojeUtils.error(e1);
			}
		}
	};

	private final ActionListener dodajFSListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				if (FakturaZPliku.wczytajTowaryZFS())
					MojeUtils
							.showMsg("Faktura wczytana poprawnie. Pamiętaj o wstawieniu kontrachenta!");

			} catch (Exception e1)
			{
				e1.printStackTrace();
				MojeUtils
						.showError("Wybrany plik ma niekompatybilną strukturę wewnętrzną!\nNie można dodać faktury!");
			}
		}
	};

	private final ActionListener sprawdzOstatkiListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				OstatkiOkno okno = new OstatkiOkno(
						SprawdzMagazyn.stworzOstatki());
				okno.setVisible(true);
			} catch (Exception e1)
			{
				if (!e1.getMessage().equals("Anulowano"))
				{
					MojeUtils
							.showError("Wybrany plik ma niekompatybilną strukturę wewnętrzną!\nNie można utworzyć ostatków!");
				}
				/* else przemilcz;) */
			}
		}
	};

	private final ActionListener aktualizujListener()
	{
		return new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				UpdateProgressKontroler upk = new UpdateProgressKontroler(
						new UpdateProgress(WidokGlowny.this));
				upk.start();
			}
		};
	}

    /***
     * Metoda nadpisuje wielkość czcionki dla Win 8.1, póki co nie mam lepszego
     * pomysłu. Dla dużej rozdzielczości ekranu, czcionka jest za mała nawet dla
     * kogoś o sokolim wzroku...
     *
     * Sam nie wierzę, że to robię... Mamo, Tato przepraszam...
     * @param size
     */
    public static void setDefaultFontSize(int size)
    {
        Set<Object> keySet = UIManager.getLookAndFeelDefaults().keySet();
        Object[] keys = keySet.toArray(new Object[keySet.size()]);
        for (Object key : keys)
        {
            if (key != null && key.toString().toLowerCase().contains("font"))
            {
                Font font = UIManager.getDefaults().getFont(key);
                if (font != null) {
                    font = font.deriveFont((float)size);
                    UIManager.put(key, font);
                }
            }
        }

    }
}
