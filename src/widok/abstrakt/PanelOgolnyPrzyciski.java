package widok.abstrakt;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import kontroler.ProduktBaza;
import model.ObiektWiersz;
import model.ObiektZId;
import utils.MojeUtils;
import utils.UserShowException;
import widok.InformatorOkno;

public class PanelOgolnyPrzyciski extends PanelOgolnyTabela
{
	private static final long serialVersionUID = -5671145856190517918L;

	PanelEdytujDodajObiekt panelDodaj;

	public PanelEdytujDodajObiekt panelEdytuj;

	DialogEdytujDodaj oknoModalneEdytujDodaj;

	protected ActionListener edytujListener;

	protected ActionListener dodajListener;

	protected ActionListener usunListener;

	protected ActionListener informatorListener;

	protected JPanel panel_przycikow;

	protected JTextField pole_wyszukiwania;

	public int sizeX;

	public int sizeY;

	public void init(PanelOgolnyParametry params) throws Exception
	{
		MojeUtils.println("Inicjuje " + this.getClass().getName());

		this.obiektBazaManager = params.obiektBazaManager;
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

		funktorDwuklikTabela = new FunktorDwuklikTabelaAkcja(this);
		super.init(params, false);
		initPrzyciski();
		initWyszukiwarka();
	}

	private void initPrzyciski()
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

					obiektBazaManager.dodaj(nowy);
					tabelaDodajWiersz(nowy.piszWierszTabeli());
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
		Object obiekt = obiektBazaManager.pobierzObiektZBazy(wiersz);
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

				obiektBazaManager.edytuj(staryObiekt, nowy);
				tabelaEdytujWiersz(numerEdytowanegoWiersza,
						nowy.piszWierszTabeli(), false);
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
					staryObiekt = obiektBazaManager.pobierzObiektZBazy(wiersz);
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
						obiektBazaManager.zmienWidocznosc(wiersz, false);
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
				String[][] data = obiektBazaManager
						.pobierzWierszeZBazy(warunki);
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
					String[][] data = obiektBazaManager
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
					} catch (SQLException e1)
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
			} catch (SQLException e1)
			{
				MojeUtils.showPrintError(e1);
			}
		}
	};

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