package widok.abstrakt;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import kontroler.FakturaBaza;
import kontroler.KontrachentBaza;
import kontroler.ObiektWyszukanieWarunki;
import kontroler.ProduktBaza;
import kontroler.TransportRozliczenieBaza;
import model.Faktura;
import model.Kontrachent;
import model.ObiektWiersz;
import model.ObiektZId;
import model.TransportRozliczenie;
import model.Wplata;
import net.sf.nachocalendar.components.DatePanel;
import utils.DataUtils;
import utils.Loger;
import utils.Loger.LogerNazwa;
import utils.MojeUtils;
import utils.UserShowException;
import widok.InformatorOkno;
import widok.WyswietlPDFPanel;

public class PanelOgolnyPrzyciski extends PanelOgolnyTabela
{
	public PanelOgolnyPrzyciski(boolean s) {
		super(s);
	}

	private static final long serialVersionUID = -5671145856190517918L;

	PanelEdytujDodajObiekt panelDodaj;

	public PanelEdytujDodajObiekt panelEdytuj;

	DialogEdytujDodaj oknoModalneEdytujDodaj;

	protected ActionListener edytujListener;

	protected ActionListener dodajListener;

	protected ActionListener usunListener;

	protected ActionListener rozliczListener;

	protected ActionListener pokazDlugiListener;

	protected static ActionListener informatorListener;

	protected JPanel panel_przycikow;

	protected JTextField pole_wyszukiwania;

	private DatePanel okresOd;

	private DatePanel okresDo;

	public int sizeX;

	public int sizeY;

	@Override
	public void init(PanelOgolnyParametry params, boolean isRozlicz)
			throws Exception
	{
		MojeUtils.println("Inicjuje " + this.getClass().getName());

		// super.init(params, false);

		// this.obiektBazaManager = params.obiektBazaManager;
		this.panelDodaj = params.panelDodaj;
		this.panelEdytuj = params.panelEdytuj;
		if (panelDodaj != null)
			if (dodajListener == null)
				dodajListener = tworzDodajListener();
		if (panelEdytuj != null)
			if (edytujListener == null)
				edytujListener = tworzEdytujListener();
		sizeX = params.sizeX;
		sizeY = params.sizeY;
		if (panelDodaj != null)
			oknoModalneEdytujDodaj = new DialogEdytujDodaj(
					tworzDodajOKListener(), anulujListener, panelDodaj,
					params.sizeX, params.sizeY);
		if (params.pokazBtnUsun)
			usunListener = tworzUsunListener();
		else
			usunListener = null;
		if (isRozlicz)
			rozliczListener = tworzRozliczListener();

		funktorDwuklikTabela = new FunktorDwuklikTabelaAkcja(this);
		super.init(params, false);
		initPrzyciski(isRozlicz);
		initWyszukiwarka();
	}

	private void initPrzyciski(boolean isRozlicz)
	{
		if (!isRozlicz)
		{
			panel_przycikow = new JPanel();
			{
				add(panel_przycikow, BorderLayout.EAST);
				GridBagLayout gbl_panel_przycikow = new GridBagLayout();
				gbl_panel_przycikow.columnWidths = new int[]
				{ 0, 0 };
				gbl_panel_przycikow.rowHeights = new int[]
				{ 0, 0, 0, 0 };
				gbl_panel_przycikow.columnWeights = new double[]
				{ 0.0, Double.MIN_VALUE };
				gbl_panel_przycikow.rowWeights = new double[]
				{ 0.0, 0.0, 0.0, Double.MIN_VALUE };
				panel_przycikow.setLayout(gbl_panel_przycikow);
			}
		} else
		{
			panel_przycikow = new JPanel();
			{
				add(panel_przycikow, BorderLayout.EAST);
				GridBagLayout gbl_panel_przycikow = new GridBagLayout();
				gbl_panel_przycikow.columnWidths = new int[]
				{ 0, 0 };
				gbl_panel_przycikow.rowHeights = new int[]
				{ 0, 0, 0, 0, 0, 0 };
				gbl_panel_przycikow.columnWeights = new double[]
				{ 0.0, Double.MIN_VALUE };
				gbl_panel_przycikow.rowWeights = new double[]
				{ 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
				panel_przycikow.setLayout(gbl_panel_przycikow);
			}
		}
		if (dodajListener != null)
		{
			JButton btnDodaj = new JButton("Dodaj");
			btnDodaj.addActionListener(dodajListener);
			GridBagConstraints gbc_btnDodaj = new GridBagConstraints();
			gbc_btnDodaj.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnDodaj.insets = new Insets(0, 0, 5, 0);
			gbc_btnDodaj.gridx = 0;
			gbc_btnDodaj.gridy = 0;
			panel_przycikow.add(btnDodaj, gbc_btnDodaj);
		}
		if (edytujListener != null)
		{
			JButton btnEdytuj = new JButton("Edytuj");
			btnEdytuj.addActionListener(edytujListener);
			GridBagConstraints gbc_btnEdytuj = new GridBagConstraints();
			gbc_btnEdytuj.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnEdytuj.insets = new Insets(0, 0, 5, 0);
			gbc_btnEdytuj.gridx = 0;
			gbc_btnEdytuj.gridy = 1;
			panel_przycikow.add(btnEdytuj, gbc_btnEdytuj);
		}
		if (usunListener != null)
		{
			JButton btnUsu = new JButton("Usuń");
			btnUsu.addActionListener(usunListener);
			GridBagConstraints gbc_btnUsu = new GridBagConstraints();
			gbc_btnUsu.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnUsu.gridx = 0;
			gbc_btnUsu.gridy = 2;
			panel_przycikow.add(btnUsu, gbc_btnUsu);
		}
		if (rozliczListener != null)
		{
			JButton btnRozlicz = new JButton("Rozlicz");
			btnRozlicz.addActionListener(rozliczListener);
			GridBagConstraints gbc_btnRozlicz = new GridBagConstraints();
			gbc_btnRozlicz.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnRozlicz.gridx = 0;
			gbc_btnRozlicz.gridy = 0;
			panel_przycikow.add(btnRozlicz, gbc_btnRozlicz);

			JLabel poczatek_okresu = new JLabel("Początek okresu");
			GridBagConstraints gbc_poczatek_okresu = new GridBagConstraints();
			gbc_poczatek_okresu.fill = GridBagConstraints.HORIZONTAL;
			gbc_poczatek_okresu.gridx = 0;
			gbc_poczatek_okresu.gridy = 1;
			panel_przycikow.add(poczatek_okresu, gbc_poczatek_okresu);

			okresOd = new DatePanel();
			GridBagConstraints gbc_okresOd = new GridBagConstraints();
			gbc_okresOd.fill = GridBagConstraints.HORIZONTAL;
			gbc_okresOd.gridx = 0;
			gbc_okresOd.gridy = 2;
			panel_przycikow.add(okresOd, gbc_okresOd);

			JLabel koniec_okresu = new JLabel("Koniec okresu");
			GridBagConstraints gbc_koniec_okresu = new GridBagConstraints();
			gbc_koniec_okresu.fill = GridBagConstraints.HORIZONTAL;
			gbc_koniec_okresu.gridx = 0;
			gbc_koniec_okresu.gridy = 3;
			panel_przycikow.add(koniec_okresu, gbc_koniec_okresu);

			okresDo = new DatePanel();
			GridBagConstraints gbc_okresDo = new GridBagConstraints();
			gbc_okresDo.fill = GridBagConstraints.HORIZONTAL;
			gbc_okresDo.gridx = 0;
			gbc_okresDo.gridy = 4;
			panel_przycikow.add(okresDo, gbc_okresDo);
		}
	}

	private void initWyszukiwarka()
	{
		JPanel panel_wyszukiwarki = new JPanel();
		{
			add(panel_wyszukiwarki, BorderLayout.SOUTH);
			GridBagLayout gbl_panel_wyszukiwarki = new GridBagLayout();
			gbl_panel_wyszukiwarki.columnWidths = new int[]
			{ 0, 0, 0 };
			gbl_panel_wyszukiwarki.rowHeights = new int[]
			{ 0, 0 };
			gbl_panel_wyszukiwarki.columnWeights = new double[]
			{ 1.0, 0.0, Double.MIN_VALUE };
			gbl_panel_wyszukiwarki.rowWeights = new double[]
			{ 0.0, Double.MIN_VALUE };
			panel_wyszukiwarki.setLayout(gbl_panel_wyszukiwarki);
		}
		{
			pole_wyszukiwania = new JTextField();
			GridBagConstraints gbc_pole_wyszukiwania = new GridBagConstraints();
			pole_wyszukiwania.addKeyListener(szukajEnterListener);
			gbc_pole_wyszukiwania.insets = new Insets(0, 0, 0, 5);
			gbc_pole_wyszukiwania.fill = GridBagConstraints.HORIZONTAL;
			gbc_pole_wyszukiwania.gridx = 0;
			gbc_pole_wyszukiwania.gridy = 0;
			panel_wyszukiwarki.add(pole_wyszukiwania, gbc_pole_wyszukiwania);
			pole_wyszukiwania.setColumns(15);
		}
		if (anulujSzukanieListener != null)
		{
			JButton btnAnulujSzukanie = new JButton("X");
			btnAnulujSzukanie.addActionListener(anulujSzukanieListener);
			GridBagConstraints gbc_btnAnulujSzukanie = new GridBagConstraints();
			gbc_btnAnulujSzukanie.gridx = 1;
			gbc_btnAnulujSzukanie.gridy = 0;
			panel_wyszukiwarki.add(btnAnulujSzukanie, gbc_btnAnulujSzukanie);
		}
		if (szukajListener != null)
		{
			JButton btnSzukaj = new JButton("Szukaj");
			btnSzukaj.addActionListener(szukajListener);
			GridBagConstraints gbc_btnSzukaj = new GridBagConstraints();
			gbc_btnSzukaj.gridx = 2;
			gbc_btnSzukaj.gridy = 0;
			panel_wyszukiwarki.add(btnSzukaj, gbc_btnSzukaj);
		}
	}

	/* DODAJ - BUTTON OK ----------------------------------------- */
	private ActionListener tworzDodajOKListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent a)
			{
				try
				{
					/*
					 * pobierz dane z formatki, id zostanie ustawione w funkcji
					 * dodaj(nowy)
					 */
					ObiektZId nowy = oknoModalne.pobierzZFormatki(0);

					if (nowy == null)
						return;
					if (nowy instanceof Faktura)
					{
						try
						{
							getObiektBazaManager().dodaj(nowy);
							tabelaDodajWiersz(nowy.piszWierszTabeli());

							FakturaBaza.zaplacFaktury(
									((Faktura) nowy).kontrahent.id_kontrachent,
									(((Faktura) nowy).waluta).intValue());

							ukryjModalneOkno();

							Loger.log(LogerNazwa.FinanseLog,
									Loger.tworzLog(nowy));

						} catch (Exception e)
						{
							MojeUtils
									.showError("Coś poszło nie tak z dodaniem długu.");
							MojeUtils.error(e);;
						}
					} else if (nowy instanceof Wplata)
					{
						try
						{
							getObiektBazaManager().dodaj(nowy);
							tabelaDodajWiersz(nowy.piszWierszTabeli());

							FakturaBaza.zaplacFaktury(
									((Wplata) nowy).id_kontrachent,
									((Wplata) nowy).waluta);

							ukryjModalneOkno();
							Loger.log(LogerNazwa.FinanseLog,
									Loger.tworzLog(nowy));
						} catch (Exception e)
						{
							MojeUtils
									.showError("Coś poszło nie tak z odejmowaniem długu.");
							MojeUtils.error(e);;
						}
					} else
					{
						getObiektBazaManager().dodaj(nowy);
						tabelaDodajWiersz(nowy.piszWierszTabeli());
						ukryjModalneOkno();
					}

				} catch (UserShowException e)
				{
					MojeUtils.showError(e);
				} catch (Exception e)
				{
					MojeUtils.showPrintError(e);
				}
			}
		};
	}

	/* DODAJ ------------------------------------------- */

	protected ActionListener tworzDodajListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				oknoModalne = oknoModalneEdytujDodaj;
				oknoModalne.czysc();
				oknoModalne
						.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				oknoModalne.setVisible(true);
				/* reszta dzieje sie w buttonie OK */
			}
		};
	}

	protected void wstawDaneDoFormatki(ObiektWiersz wiersz)
	{
		/* wstaw dane do formatki */
		Object obiekt = getObiektBazaManager().pobierzObiektZBazy(wiersz);
		oknoModalne.uzupelnijFormatke(obiekt);
	}

	public ObiektZId staryObiekt = null;

	protected int numerEdytowanegoWiersza;

	/* EDYTUJ - BUTTON OK--------------------------- */
	public ActionListener panelEdytujOkButtonListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			try
			{
				/* pobierz dane z formatki */
				ObiektZId nowy = oknoModalne.pobierzZFormatki(staryObiekt
						.getId());

				getObiektBazaManager().edytuj(staryObiekt, nowy);
				tabelaEdytujWiersz(numerEdytowanegoWiersza,
						nowy.piszWierszTabeli(), false);
				/* Doszła wpłata zmień dług walutowy kontrachenta */
				if (staryObiekt instanceof Wplata && nowy instanceof Wplata)
				{
					Wplata nowa_wplata = (Wplata) nowy;
					Wplata stara_wplata = (Wplata) staryObiekt;
					/*
					 * Kontrachent się zmienił dodaj dług do nowego odejmij dług
					 * od starego
					 */
					FakturaBaza.zaplacFaktury(stara_wplata.id_kontrachent,
							stara_wplata.waluta);
					FakturaBaza.zaplacFaktury(nowa_wplata.id_kontrachent,
							nowa_wplata.waluta);
				}
				ukryjModalneOkno();
			} catch (UserShowException e)
			{
				MojeUtils.showError(e);
			} catch (Exception e)
			{
				MojeUtils.showPrintError(e);
			}
		}
	};

	/* EDYTUJ ----------------------------------- */
	protected ActionListener tworzEdytujListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					/* zapamietaj ktory wiersz edytujemy */
					numerEdytowanegoWiersza = pobierzNumerZaznaczonegoWiersza();
					if (numerEdytowanegoWiersza < 0)
						return;
					ObiektWiersz wiersz = new ObiektWiersz(
					/* Pobierz wiersze z tabeli */
					pobierzZaznaczonyWiersz());
					if (wiersz.wiersz == null)
						return;

					oknoModalne = new DialogEdytujDodaj(
							panelEdytujOkButtonListener, anulujListener,
							panelEdytuj, sizeX, sizeY);

					/* wstaw dane do formatki */
					staryObiekt = getObiektBazaManager().pobierzObiektZBazy(
							wiersz);
					oknoModalne.uzupelnijFormatke(staryObiekt);

					oknoModalne
							.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					oknoModalne.setVisible(true);

					/* reszta dzieje sie w buttonie OK */
				} catch (Exception e)
				{
					MojeUtils.showPrintError(e);
				}
			}
		};
	}

	/* USUN---------------------------------------- */
	protected ActionListener tworzUsunListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					/* zapamietaj ktory wiersz edytujemy */
					int numerEdytowanegoWiersza = pobierzNumerZaznaczonegoWiersza();

					ObiektWiersz wiersz = new ObiektWiersz(
							pobierzZaznaczonyWiersz());
					if (wiersz.wiersz == null)
						return;
					/* Dialog przed usunięciem */
					if (MojeUtils.usunWiersz("Czy na peno chcesz usunąć?",
							wiersz))
					{
						getObiektBazaManager().zmienWidocznosc(wiersz, false);
						tabelaUsunWiersz(numerEdytowanegoWiersza);
						ukryjModalneOkno();
					}
				} catch (Exception e)
				{
					MojeUtils.showPrintError(e);
				}
			}
		};
	}

	/* POKAŻ DŁUGI-------------------------------- */
	protected ActionListener tworzDlugiListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					ObiektWiersz wiersz = new ObiektWiersz(
							pobierzZaznaczonyWiersz());
					if (wiersz.wiersz == null)
						return;
					Kontrachent k = (Kontrachent) KontrachentBaza
							.pobierzObiektZBazy(KontrachentBaza
									.getIdFromWiersz(wiersz));
					MojeUtils.showMsg("Długi: " + k.nazwa + "\nPLN: "
							+ MojeUtils.formatujWartosc(k.dlug_pln) + "\nEUR: "
							+ MojeUtils.formatujWartosc(k.dlug_eur) + "\nUSD: "
							+ MojeUtils.formatujWartosc(k.dlug_usd) + "\nUAH: "
							+ MojeUtils.formatujWartosc(k.dlug_uah));
				} catch (Exception e)
				{
					MojeUtils.showPrintError(e);
				}
			}
		};
	}

	/* INFORMATOR---------------------------------------- */
	protected ActionListener tworzInformatorListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					ObiektWiersz wiersz = new ObiektWiersz(
							pobierzZaznaczonyWiersz());
					InformatorOkno.init(ProduktBaza
							.getNazwaFromWiersz(pobierzZaznaczonyWiersz()),
							wiersz);
				} catch (Exception e)
				{
					MojeUtils.showPrintError(e);
				}
			}
		};
	}

	public static ActionListener setInformatorListener()
	{
		return informatorListener;
	}

	/* SZUKAJ-------------------------------------- */
	protected ActionListener szukajListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			String szukanieText = pobierzSzukanyText().trim();
			if (szukanieText.isEmpty())
				return;

			/* szuka i wyswietla wg nowych warunkow */
			try
			{
				warunki.ustawTylkoWidoczne();
				warunki.dodajWarunekDowolnaKolumna(szukanieText);
				String[][] data = getObiektBazaManager().pobierzWierszeZBazy(
						warunki);
				if (data.length == 0)
				{
					szukanieText += "*";
					warunki.dodajWarunekDowolnaKolumna(szukanieText);
					data = null;
				}
				przeladujTabele(data, editableFunktor, false);
			} catch (Exception e)
			{
				MojeUtils.showPrintError(e);
			}
		}
	};

	protected KeyListener szukajEnterListener = new KeyListener()
	{

		@Override
		public void keyTyped(KeyEvent e)
		{}

		@Override
		public void keyReleased(KeyEvent e)
		{}

		@Override
		public void keyPressed(KeyEvent e)
		{
			/* Naciśnięto ENTER */
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				String szukanieText = pobierzSzukanyText().trim();
				if (szukanieText.isEmpty())
					return;

				/* szuka i wyswietla wg nowych warunkow */
				try
				{
					warunki.ustawTylkoWidoczne();
					warunki.dodajWarunekDowolnaKolumna(szukanieText);
					String[][] data = getObiektBazaManager()
							.pobierzWierszeZBazy(warunki);
					if (data.length == 0)
					{
						szukanieText += "*";
						warunki.dodajWarunekDowolnaKolumna(szukanieText);
						data = null;
					}
					przeladujTabele(data, editableFunktor, false);
				} catch (Exception e1)
				{
					MojeUtils.showPrintError(e1);
				}
			} else
			{
				/* Naciśnięto ESC */
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					warunki.removeWarunekDowolnaKolumna();
					wyczyscSzukanyText();
					try
					{
						przeladujTabele(false);
					} catch (Exception e1)
					{
						MojeUtils.showPrintError(e1);
					}
				}
			}
		}
	};

	protected ActionListener anulujSzukanieListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			warunki.removeWarunekDowolnaKolumna();
			wyczyscSzukanyText();
			try
			{
				przeladujTabele(false);
			} catch (Exception e1)
			{
				MojeUtils.showPrintError(e1);
			}
		}
	};

	/* Rozlicz ----------------------------------- */
	protected ActionListener tworzRozliczListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					Date[] daty =
					{ okresOd.getDate(), okresDo.getDate() };
					if (daty[0].toString().equals(daty[1].toString()))
						MojeUtils.showMsg("Wybrane daty są identyczne");
					else if (daty[0].after(daty[1]))
						MojeUtils.showMsg("Okres do musi być za okresem od");
					else
					{
						String[] daty_rozliczenia =
						{ DataUtils.stringToDate_format.format(daty[0]),
								DataUtils.stringToDate_format.format(daty[1]) };
						ObiektWyszukanieWarunki warunki = new ObiektWyszukanieWarunki(
								null);
						warunki.dodajWarunekZData(
								daty_rozliczenia[0].substring(0,
										daty_rozliczenia[0].length() - 6),
								daty_rozliczenia[1].substring(0,
										daty_rozliczenia[1].length() - 6),
								"data_ksiegowania");
						String[][] rachunki_rozliczenia = TransportRozliczenieBaza
								.instance().pobierzWierszeZBazy(warunki);
						ArrayList<TransportRozliczenie> lista_rachunkow_rozliczenia = (ArrayList<TransportRozliczenie>) TransportRozliczenieBaza
								.rozliczZaOkres(rachunki_rozliczenia);
						WyswietlPDFPanel panel = new WyswietlPDFPanel();
						panel.uzupelnijTransportRozliczenie(
								lista_rachunkow_rozliczenia, new JDialog());
					}
				} catch (Exception e)
				{
					MojeUtils.showPrintError(e);
				}
			}
		};
	}

	public String pobierzSzukanyText()
	{
		return pole_wyszukiwania.getText();
	}

	public void wyczyscSzukanyText()
	{
		pole_wyszukiwania.setText("");
	}

	public static class FunktorDwuklikTabelaAkcja extends FunktorDwuklikTabela
	{
		protected PanelOgolnyPrzyciski parent;

		FunktorDwuklikTabelaAkcja(PanelOgolnyPrzyciski parent) {
			this.parent = parent;
		}

		@Override
		public void run(ObiektWiersz wiersz, int row)
		{
			if (parent.panelWyswietl == null)
				return;
			parent.oknoModalne = new DialogEdytujDodaj(parent.anulujListener,
					parent.anulujListener, parent.panelWyswietl, parent.sizeX,
					parent.sizeY);

			parent.wstawDaneDoFormatki(wiersz);

			parent.oknoModalne
					.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			parent.oknoModalne.setVisible(true);
		}
	}
}