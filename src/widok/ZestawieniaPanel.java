package widok;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import kontroler.FakturaBaza;
import kontroler.KontrachentBaza;
import kontroler.ObiektWyszukanieWarunki;
import kontroler.WplataBaza;
import model.Faktura;
import model.Kontrachent;
import model.Wplata;
import utils.MojeUtils;

public class ZestawieniaPanel extends JPanel
{

	private static final long serialVersionUID = -2897868125272003380L;

	final static FakturaBaza fakturaBaza = new FakturaBaza();

	private JTable table;

	/* na potrzeby formatki dluznicy */
	JComboBox<Object> comboBoxKontrachentDluznicy;

	JComboBox<Object> comboBoxKontrachentDlugi;

	JComboBox<Object> comboBoxRaportDluznika;

	/* na potrzeby formatki dluznicy */
	Object warunekKontrachent = null;

	private final String tytulPanelu = "Zestawienie";

	private JPanel panelTabeli;

	enum RodzajZestawienia
	{
		ZestawienieDluznicy, RaportDluguWalutowego, ZestawienieDlugi;
	}

	class ZestawienieActionListener implements ActionListener
	{

		RodzajZestawienia rodzajZestawienia;

		ZestawienieActionListener(RodzajZestawienia rodzajZestawienia) {
			this.rodzajZestawienia = rodzajZestawienia;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				switch (rodzajZestawienia)
				{
				case ZestawienieDluznicy:
					dluznicy();
					break;
				case RaportDluguWalutowego:
					raportDlugi((Kontrachent) KontrachentBaza
							.pobierzObiektZBazy(comboBoxRaportDluznika
									.getSelectedIndex() + 1));
					break;
				case ZestawienieDlugi:
					dlugi();
					break;
				default:
					;
				}
			} catch (Exception e1)
			{
				MojeUtils.showError(e1);
			}
		}
	}

	/* Zestawienie pokazujące dłużników */
	public void dluznicy() throws Exception
	{
		ObiektWyszukanieWarunki warunki = ObiektWyszukanieWarunki
				.TworzWarunekFaktura();
		warunki.dodajWarunek("0", "zaplacona");
		warunki.dodajWarunek(Faktura.SPRZEDAZ, "rodzaj");
		if (warunekKontrachent != null)
			warunki.dodajWarunek(warunekKontrachent, "id_kontrachent");

		String[][] data = fakturaBaza.pobierzWierszeZBazy(warunki);
		DefaultTableModel model = new DefaultTableModel(data,
				Faktura.opisKolumn.obiektKolumny);
		table.setModel(model);
		table.removeColumn(table.getColumnModel().getColumn(5));
		panelTabeli.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), tytulPanelu
				+ " faktur niezapłaconych przez kontrachentów",
				TitledBorder.CENTER, TitledBorder.TOP, null, null));
	}

	public void dluznicyZmienKontrachent() throws Exception
	{
		if (comboBoxKontrachentDluznicy.getSelectedIndex() < 0)
			return;
		if (comboBoxKontrachentDluznicy.getSelectedItem().toString().isEmpty())
		{
			warunekKontrachent = null;
		} else
		{
			warunekKontrachent = ((Kontrachent) KontrachentBaza
					.pobierzObiektZBazy(comboBoxKontrachentDluznicy
							.getSelectedItem().toString())).getId();
		}
		dluznicy();
		comboBoxKontrachentDluznicy.setModel(MojeUtils.odswiezDaneWCombo(true,
				KontrachentBaza.pobierzWszystkieNazwyZBazy()));
	}

	// //

	/* Zestawienie pokazujące sprzedaż w określonym czasie */
	public void raportDlugiZmienKontrachent() throws Exception
	{

		/*
		 * if (comboBoxRaportDluznika.getSelectedIndex() < 0) return; if
		 * (comboBoxRaportDluznika.getSelectedItem().toString().isEmpty())
		 * warunekKontrachent = null; else { warunekKontrachent = ((Kontrachent)
		 * KontrachentBaza .pobierzObiektZBazy(comboBoxRaportDluznika
		 * .getSelectedItem().toString())).getId(); }
		 */
		/*
		 * comboBoxRaportDluznika.setModel(MojeUtils.odswiezDaneWCombo(false,
		 * KontrachentBaza.pobierzWszystkieNazwyZBazy()));
		 */
		/* Pobierz kontrachenta */
		if (comboBoxRaportDluznika.getSelectedIndex() < 0)
			return;
		Kontrachent kontrachent = (Kontrachent) KontrachentBaza
				.pobierzObiektZBazy(comboBoxRaportDluznika.getSelectedIndex() + 1);
		/* Odśwież dane w combo jeśli dodaliśmy nowego kontrachenta */
		comboBoxRaportDluznika.setModel(MojeUtils.odswiezDaneWCombo(false,
				KontrachentBaza.pobierzWszystkieNazwyZBazy()));
		raportDlugi(kontrachent);
	}

	public void dlugiZmienKontrachent() throws Exception
	{

		if (comboBoxKontrachentDlugi.getSelectedIndex() < 0)
			return;
		if (comboBoxKontrachentDlugi.getSelectedItem().toString().isEmpty())
			warunekKontrachent = null;
		else
		{
			warunekKontrachent = ((Kontrachent) KontrachentBaza
					.pobierzObiektZBazy(comboBoxKontrachentDlugi
							.getSelectedItem().toString())).getId();
		}

		dlugi();
		comboBoxKontrachentDlugi.setModel(MojeUtils.odswiezDaneWCombo(true,
				KontrachentBaza.pobierzWszystkieNazwyZBazy()));
	}

	public void raportDlugi(Kontrachent kontrachent) throws Exception
	{
		/* Pobierz faktury niezapłacone kontrachenta w walucie */
		ArrayList<Faktura> f_pln = pobierzListeFakturNiezaplaconcyh(
				kontrachent.id_kontrachent, 1);
		ArrayList<Faktura> f_eur = pobierzListeFakturNiezaplaconcyh(
				kontrachent.id_kontrachent, 2);
		ArrayList<Faktura> f_usd = pobierzListeFakturNiezaplaconcyh(
				kontrachent.id_kontrachent, 3);
		ArrayList<Faktura> f_uah = pobierzListeFakturNiezaplaconcyh(
				kontrachent.id_kontrachent, 4);

		/* Pobierz wpłaty kontrachenta w walucie */
		ArrayList<Wplata> w_pln = pobierzWplatyWaluta(
				kontrachent.id_kontrachent, 1);
		ArrayList<Wplata> w_eur = pobierzWplatyWaluta(
				kontrachent.id_kontrachent, 2);
		ArrayList<Wplata> w_usd = pobierzWplatyWaluta(
				kontrachent.id_kontrachent, 3);
		ArrayList<Wplata> w_uah = pobierzWplatyWaluta(
				kontrachent.id_kontrachent, 4);

		/* poierz listę faktur w PLN - 1 */
		/*
		 * ObiektWyszukanieWarunki warunkiDlugPLN = ObiektWyszukanieWarunki
		 * .TworzWarunekFaktura(); warunkiDlugPLN.dodajWarunek(Faktura.SPRZEDAZ,
		 * "rodzaj"); warunkiDlugPLN.dodajWarunek("1", "waluta"); // waluta PLN
		 * warunkiDlugPLN.dodajWarunek("0", "zaplacona"); // nie zapłacona if
		 * (warunekKontrachent != null)
		 * warunkiDlugPLN.dodajWarunek(warunekKontrachent, "id_kontrachent");
		 * warunkiDlugPLN.generujWarunekWhere();
		 */

		// Faktura[] faktury

		/*
		 * String[][] dataDlugkontrachentaPLN = fakturaBaza
		 * .pobierzWierszeZBazy(warunkiDlugPLN); poierz listę faktur w EUR - 2
		 * ObiektWyszukanieWarunki warunkiDlugEUR = ObiektWyszukanieWarunki
		 * .TworzWarunekFaktura(); warunkiDlugEUR.dodajWarunek(Faktura.SPRZEDAZ,
		 * "rodzaj"); warunkiDlugEUR.dodajWarunek("2", "waluta"); // waluta EUR
		 * warunkiDlugEUR.dodajWarunek("0", "zaplacona"); // nie zapłacona if
		 * (warunekKontrachent != null)
		 * warunkiDlugEUR.dodajWarunek(warunekKontrachent, "id_kontrachent");
		 * warunkiDlugEUR.generujWarunekWhere();
		 * 
		 * String[][] dataDlugkontrachentaEUR = fakturaBaza
		 * .pobierzWierszeZBazy(warunkiDlugEUR); poierz listę faktur w USD - 3
		 * ObiektWyszukanieWarunki warunkiDlugUSD = ObiektWyszukanieWarunki
		 * .TworzWarunekFaktura(); warunkiDlugUSD.dodajWarunek(Faktura.SPRZEDAZ,
		 * "rodzaj"); warunkiDlugUSD.dodajWarunek("3", "waluta"); // waluta USD
		 * warunkiDlugUSD.dodajWarunek("0", "zaplacona"); // nie zapłacona if
		 * (warunekKontrachent != null)
		 * warunkiDlugUSD.dodajWarunek(warunekKontrachent, "id_kontrachent");
		 * warunkiDlugUSD.generujWarunekWhere();
		 * 
		 * String[][] dataDlugkontrachentaUSD = fakturaBaza
		 * .pobierzWierszeZBazy(warunkiDlugUSD); listę faktur w EUR - 4
		 * ObiektWyszukanieWarunki warunkiDlugUAH = ObiektWyszukanieWarunki
		 * .TworzWarunekFaktura(); warunkiDlugUAH.dodajWarunek(Faktura.SPRZEDAZ,
		 * "rodzaj"); warunkiDlugUAH.dodajWarunek("4", "waluta"); // waluta UAH
		 * warunkiDlugUAH.dodajWarunek("0", "zaplacona"); // nie zapłacona if
		 * (warunekKontrachent != null)
		 * warunkiDlugUAH.dodajWarunek(warunekKontrachent, "id_kontrachent");
		 * warunkiDlugUAH.generujWarunekWhere();
		 * 
		 * String[][] dataDlugkontrachentaUAH = fakturaBaza
		 * .pobierzWierszeZBazy(warunkiDlugUAH);
		 */
		WyswietlPDFPanel panel = new WyswietlPDFPanel();
		panel.uzupelnijRaport(f_pln, f_eur, f_usd, f_uah, w_pln, w_eur, w_usd,
				w_uah, kontrachent, new JDialog());
	}

	private ArrayList<Faktura> pobierzListeFakturNiezaplaconcyh(
			int id_kontrachent, int waluta) throws Exception
	{
		ArrayList<Faktura> f_waluta = new ArrayList<>();
		ObiektWyszukanieWarunki warunkiDlugWaluta = ObiektWyszukanieWarunki
				.TworzWarunekFaktura();
		warunkiDlugWaluta.dodajWarunek(id_kontrachent, "id_kontrachent");
		warunkiDlugWaluta.dodajWarunek(Faktura.SPRZEDAZ, "rodzaj");
		warunkiDlugWaluta.dodajWarunek(waluta, "waluta"); // waluta PLN
		warunkiDlugWaluta.dodajWarunek("0", "zaplacona"); // nie zapłacona
		warunkiDlugWaluta.generujWarunekWhere();
		Integer[] id_faktury = FakturaBaza.pobierzIdZBazy(warunkiDlugWaluta);

		for (int j = 0; j < id_faktury.length; j++)
		{
			f_waluta.add(((Faktura) FakturaBaza
					.pobierzObiektZBazy(id_faktury[j])));
		}

		return f_waluta;
	}

	private ArrayList<Wplata> pobierzWplatyWaluta(int id_kontrachent, int waluta)
			throws Exception
	{
		return ((ArrayList<Wplata>) WplataBaza.pobierzObiektyZBazy(
				id_kontrachent, waluta));
	}

	public void dlugi() throws Exception
	{
		ObiektWyszukanieWarunki warunki = ObiektWyszukanieWarunki
				.TworzWarunekFaktura();
		warunki.dodajWarunek("0", "zaplacona");
		warunki.dodajWarunek(Faktura.ZAKUP, "rodzaj");
		if (warunekKontrachent != null)
			warunki.dodajWarunek(warunekKontrachent, "id_kontrachent");

		String[][] data = fakturaBaza.pobierzWierszeZBazy(warunki);
		DefaultTableModel model = new DefaultTableModel(data,
				Faktura.opisKolumn.obiektKolumny);

		table.setModel(model);
		table.removeColumn(table.getColumnModel().getColumn(5));
		panelTabeli.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), tytulPanelu
				+ " faktur niezapłaconych kontrachentom", TitledBorder.CENTER,
				TitledBorder.TOP, null, null));
	}

	public ZestawieniaPanel() {
		try
		{
			setLayout(new BorderLayout(0, 0));

			JPanel panel = new JPanel();
			add(panel, BorderLayout.NORTH);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[]
			{ 0, 0, 0, 0 };
			gbl_panel.rowHeights = new int[]
			{ 0, 0, 0 };
			gbl_panel.columnWeights = new double[]
			{ 1.0, 1.0, 1.0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[]
			{ 0.0, 0.0, Double.MIN_VALUE };
			panel.setLayout(gbl_panel);

			{
				JLabel btnDunicy = new JLabel("Długi kontrachentów");
				GridBagConstraints gbc_btnDunicy = new GridBagConstraints();
				gbc_btnDunicy.insets = new Insets(0, 0, 5, 5);
				gbc_btnDunicy.gridx = 1;
				gbc_btnDunicy.gridy = 0;
				panel.add(btnDunicy, gbc_btnDunicy);
			}
			{

				JLabel btnDlugi = new JLabel("Nasze należności kontrachentom");
				GridBagConstraints gbc_btnDlugi = new GridBagConstraints();
				gbc_btnDlugi.insets = new Insets(0, 0, 5, 5);
				gbc_btnDlugi.gridx = 0;
				gbc_btnDlugi.gridy = 0;
				panel.add(btnDlugi, gbc_btnDlugi);
			}

			{
				comboBoxKontrachentDlugi = new JComboBox<Object>(
						MojeUtils.odswiezDaneWCombo(true,
								KontrachentBaza.pobierzWszystkieNazwyZBazy()));
				GridBagConstraints gbc_comboBox = new GridBagConstraints();
				gbc_comboBox.insets = new Insets(0, 0, 0, 5);
				gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBox.gridx = 0;
				gbc_comboBox.gridy = 1;
				panel.add(comboBoxKontrachentDlugi, gbc_comboBox);
				comboBoxKontrachentDlugi.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						try
						{
							dlugiZmienKontrachent();
						} catch (Exception e1)
						{
							MojeUtils.showPrintError(e1);
						}
					}
				});
			}
			{
				comboBoxKontrachentDluznicy = new JComboBox<Object>(
						MojeUtils.odswiezDaneWCombo(true,
								KontrachentBaza.pobierzWszystkieNazwyZBazy()));
				GridBagConstraints gbc_comboBoxKontrachent_1 = new GridBagConstraints();
				gbc_comboBoxKontrachent_1.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBoxKontrachent_1.insets = new Insets(0, 0, 0, 5);
				gbc_comboBoxKontrachent_1.gridx = 1;
				gbc_comboBoxKontrachent_1.gridy = 1;
				panel.add(comboBoxKontrachentDluznicy,
						gbc_comboBoxKontrachent_1);
				comboBoxKontrachentDluznicy
						.addActionListener(new ActionListener()
						{
							@Override
							public void actionPerformed(ActionEvent e)
							{
								try
								{
									dluznicyZmienKontrachent();
								} catch (Exception e1)
								{
									MojeUtils.showPrintError(e1);
								}
							}
						});
			}
			{
				comboBoxRaportDluznika = new JComboBox<Object>(
						MojeUtils.odswiezDaneWCombo(false,
								KontrachentBaza.pobierzWszystkieNazwyZBazy()));
				GridBagConstraints gbc_comboBoxRaportDluznika = new GridBagConstraints();
				gbc_comboBoxRaportDluznika.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBoxRaportDluznika.gridx = 2;
				gbc_comboBoxRaportDluznika.gridy = 1;
				panel.add(comboBoxRaportDluznika, gbc_comboBoxRaportDluznika);
				comboBoxRaportDluznika.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						try
						{
							raportDlugiZmienKontrachent();
						} catch (Exception e1)
						{
							MojeUtils.showPrintError(e1);
						}
					}
				});
			}
			{
				JLabel lblGenerujRaportO = new JLabel(
						"Generuj raport o długach");
				GridBagConstraints gbc_lblGenerujRaportO = new GridBagConstraints();
				gbc_lblGenerujRaportO.insets = new Insets(0, 0, 5, 0);
				gbc_lblGenerujRaportO.gridx = 2;
				gbc_lblGenerujRaportO.gridy = 0;
				panel.add(lblGenerujRaportO, gbc_lblGenerujRaportO);
			}
			{
				panelTabeli = new JPanel();
				panelTabeli.setBorder(new TitledBorder(UIManager
						.getBorder("TitledBorder.border"), tytulPanelu,
						TitledBorder.CENTER, TitledBorder.TOP, null, null));
				add(panelTabeli, BorderLayout.CENTER);
				panelTabeli.setLayout(new BorderLayout(0, 0));
				table = new JTable();
				{
					add(panelTabeli, BorderLayout.CENTER);
					panelTabeli.setLayout(new BorderLayout(0, 0));
					JScrollPane suwak_tabeli = new JScrollPane();
					panelTabeli.add(suwak_tabeli, BorderLayout.CENTER);
					suwak_tabeli.setViewportView(table);
				}
			}
		} catch (Exception e)
		{
			MojeUtils.showError(e);
		}
	}
}