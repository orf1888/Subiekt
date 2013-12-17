package widok.faktura;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import kontroler.FakturaBaza;
import kontroler.KontrachentBaza;
import kontroler.ProduktBaza;
import kontroler.WalutaManager;
import model.Faktura;
import model.Kontrachent;
import model.ObiektWiersz;
import model.ObiektZId;
import model.Waluta;
import model.produkt.Produkt;
import model.produkt.ProduktWFakturze;
import net.sf.nachocalendar.components.DatePanel;
import utils.MojeUtils;
import utils.UserShowException;
import widok.MagazynPanel;
import widok.abstrakt.PanelEdytujDodajObiekt;
import widok.abstrakt.PanelOgolnyTabela.FunktorDwuklikTabela;

public class FakturyPanelEdytujDodaj extends PanelEdytujDodajObiekt
{

	private static final long serialVersionUID = 7011981897503862573L;

	DatePanel panelDatyWystawFaktury;

	DatePanel panelTerminPlatnosci;

	JComboBox<Object> comboKontrachenci;

	JComboBox<Object> comboWaluta;

	int rodzajFaktury;

	public List<ProduktWFakturze> listaProduktow;

	protected Faktura faktura;

	FakturyPanelListaWybranychAbstrakt listaWybranychPanel;

	MagazynPanel panelMagazynu;

	JTextField numerField;

	private ButtonPokazywaniaPaneluListener terminPlatnosciButtonListener;

	private ButtonPokazywaniaPaneluListener dataWystawieniaFakturyButtonListener;

	JSplitPane splitPane;

	boolean tmpNieZmieniaKontrachenta = false;

	class ButtonPokazywaniaPaneluListener implements ActionListener
	{
		boolean pokazane = false;

		private final JButton button;

		String textUkryj;

		String textPokaz;

		DatePanel panel;

		ButtonPokazywaniaPaneluListener(DatePanel panel, JButton b,
				String textUkryj, String textPokaz) {
			this.panel = panel;
			this.button = b;
			this.textUkryj = textUkryj;
			this.textPokaz = textPokaz;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!pokazane)
			{
				panel.show();
				button.setText(textUkryj);
			} else
			{
				panel.hide();
				button.setText(textPokaz);
			}
			pokazane = !pokazane;
		}

		@SuppressWarnings("deprecation")
		public void ukryj()
		{
			panel.hide();
			button.setText(textPokaz);
			pokazane = false;
		}
	};

	/**
	 * @param tytul
	 * @param rodzajFaktury
	 *            - Faktura.SPRZEDAZ lub ZAKUP
	 * @param editable
	 * @param popupMagazynWFaktury
	 *            - popup PrawegoKlikMyszy na tabeli Magazyn
	 * @param korekta
	 * @throws SQLException
	 */
	@SuppressWarnings("deprecation")
	public FakturyPanelEdytujDodaj(String tytul, int rodzajFaktury,
			boolean editable, JPopupMenu popupMagazynWFaktury, boolean korekta)
			throws SQLException {
		this.rodzajFaktury = rodzajFaktury;
		this.listaProduktow = new ArrayList<ProduktWFakturze>();
		setBorder(new TitledBorder(null, tytul, TitledBorder.CENTER,
				TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));

		JPanel tloPanel = new JPanel();
		add(tloPanel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]
		{ 0, 0, 0 };
		gbl_panel.rowHeights = new int[]
		{ 0, 0 };
		gbl_panel.columnWeights = new double[]
		{ 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[]
		{ 0.0, Double.MIN_VALUE };
		tloPanel.setLayout(gbl_panel);

		int _gridx = 0;
		{
			comboKontrachenci = new JComboBox<Object>();
			comboKontrachenci.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					zmienKontrachent();
				}
			});
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.anchor = GridBagConstraints.NORTHWEST;
			gbc_comboBox.insets = new Insets(0, 0, 0, 5);
			gbc_comboBox.gridx = _gridx;
			gbc_comboBox.gridy = 0;
			tloPanel.add(comboKontrachenci, gbc_comboBox);
		}
		/* separator */
		{
			_gridx++;
			Component horizontalStrut = Box.createHorizontalStrut(40);
			GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
			gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
			gbc_horizontalStrut.gridx = _gridx;
			gbc_horizontalStrut.gridy = 0;
			tloPanel.add(horizontalStrut, gbc_horizontalStrut);
		}
		/* waluta */
		{
			// //
			String[] waluta = WalutaManager.pobierzWszystkieZBazy(
					Waluta.tabelaWaluta, true);

			comboWaluta = new JComboBox<Object>(waluta);
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.anchor = GridBagConstraints.NORTHWEST;
			gbc_comboBox.insets = new Insets(0, 0, 0, 5);
			gbc_comboBox.gridx = _gridx;
			gbc_comboBox.gridy = 0;
			tloPanel.add(comboWaluta, gbc_comboBox);
		}
		/* separator */
		{
			_gridx++;
			Component horizontalStrut = Box.createHorizontalStrut(40);
			GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
			gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
			gbc_horizontalStrut.gridx = _gridx;
			gbc_horizontalStrut.gridy = 0;
			tloPanel.add(horizontalStrut, gbc_horizontalStrut);
		}
		/* nrFaktury */
		if (rodzajFaktury == Faktura.ZAKUP)
		{
			{
				_gridx++;
				JLabel lblNumer = new JLabel("Numer:");
				GridBagConstraints gbc_lblNumer = new GridBagConstraints();
				gbc_lblNumer.anchor = GridBagConstraints.WEST;
				gbc_lblNumer.insets = new Insets(0, 0, 5, 5);
				gbc_lblNumer.gridx = _gridx;
				gbc_lblNumer.gridy = 0;
				tloPanel.add(lblNumer, gbc_lblNumer);
			}
			{
				_gridx++;
				numerField = new JTextField();
				numerField.setColumns(20);
				GridBagConstraints gbc_numer = new GridBagConstraints();
				gbc_numer.insets = new Insets(0, 0, 5, 5);
				gbc_numer.fill = GridBagConstraints.HORIZONTAL;
				gbc_numer.gridx = _gridx;
				gbc_numer.gridy = 0;
				tloPanel.add(numerField, gbc_numer);
			}
			/* separator */
			{
				_gridx++;
				Component horizontalStrut = Box.createHorizontalStrut(40);
				GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
				gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
				gbc_horizontalStrut.gridx = _gridx;
				gbc_horizontalStrut.gridy = 0;
				tloPanel.add(horizontalStrut, gbc_horizontalStrut);
			}
		}
		/* data wystawienia faktury */
		{
			_gridx++;
			panelDatyWystawFaktury = new DatePanel();
			final JButton dataWystawieniaFakturyButton = new JButton(
					"Pokaż datę faktury");
			dataWystawieniaFakturyButton
					.addActionListener(dataWystawieniaFakturyButtonListener = new ButtonPokazywaniaPaneluListener(
							panelDatyWystawFaktury,
							dataWystawieniaFakturyButton, "Ukryj datę faktury",
							"Pokaż datę faktury"));
			GridBagConstraints gbc_dataWystawieniaFaktury = new GridBagConstraints();
			gbc_dataWystawieniaFaktury.gridx = _gridx;
			gbc_dataWystawieniaFaktury.gridy = 0;
			tloPanel.add(dataWystawieniaFakturyButton,
					gbc_dataWystawieniaFaktury);

			GridBagConstraints gbc_panelDatyWystawFaktury = new GridBagConstraints();
			gbc_panelDatyWystawFaktury.gridx = _gridx;
			gbc_panelDatyWystawFaktury.gridy = 1;
			tloPanel.add(panelDatyWystawFaktury, gbc_panelDatyWystawFaktury);
			panelDatyWystawFaktury.hide();
		}
		/* separator */
		{
			_gridx++;
			Component horizontalStrut = Box.createHorizontalStrut(40);
			GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
			gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
			gbc_horizontalStrut.gridx = _gridx;
			gbc_horizontalStrut.gridy = 0;
			tloPanel.add(horizontalStrut, gbc_horizontalStrut);
		}
		/* termin platnosci */
		{
			_gridx++;
			panelTerminPlatnosci = new DatePanel();
			final JButton terminPlatnosciButton = new JButton(
					"Pokaż termin płatności");
			terminPlatnosciButton
					.addActionListener(terminPlatnosciButtonListener = new ButtonPokazywaniaPaneluListener(
							panelTerminPlatnosci, terminPlatnosciButton,
							"Ukryj termin płatności", "Pokaż termin płatności"));
			GridBagConstraints gbc_terminPlatnosci = new GridBagConstraints();
			gbc_terminPlatnosci.gridx = _gridx;
			gbc_terminPlatnosci.gridy = 0;
			tloPanel.add(terminPlatnosciButton, gbc_terminPlatnosci);

			GridBagConstraints gbc_panelTerminPlatnosci = new GridBagConstraints();
			gbc_panelTerminPlatnosci.gridx = _gridx;
			gbc_panelTerminPlatnosci.gridy = 1;
			tloPanel.add(panelTerminPlatnosci, gbc_panelTerminPlatnosci);
			panelTerminPlatnosci.hide();
		}

		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		add(splitPane);

		/* PO LEWEJ STRONIE MAGAZYNEK */
		if (!korekta)
		{
			/* A PO PRAWEJ LISTA WYBRAŃCÓW */
			listaWybranychPanel = new FakturyPanelListaWybranych(this);
			splitPane.setRightComponent(listaWybranychPanel);

			panelMagazynu = new MagazynPanel( /* wyswietlButtony */false,
					popupMagazynWFaktury);
			panelMagazynu.zmienDwuklikFunktor(new FunktorDwuklikAkcjaFaktura(
					listaWybranychPanel, listaProduktow, this));
			splitPane.setLeftComponent(panelMagazynu);
		}
	}

	protected void zmienKontrachent()
	{
		if (tmpNieZmieniaKontrachenta)
			return;
		Kontrachent k = pobierzKontrachenta();

		if (k != null)
		{
			listaWybranychPanel.wyczyscTabele();

			for (ProduktWFakturze _produkt : listaProduktow)
			{
				int mnoznik = 100;
				mnoznik += k._cena_mnoznik;
				_produkt._cena_jednostkowa = ((_produkt.produkt.cena_zakupu)
						* mnoznik / 100);
				listaWybranychPanel.tabelaDodajWiersz(_produkt.pisz());
			}
		} else
			return;
	}

	public static class FunktorDwuklikAkcjaFaktura extends FunktorDwuklikTabela
	{
		FakturyPanelListaWybranychAbstrakt listaWybranych;

		FakturyPanelEdytujDodaj parent;

		List<ProduktWFakturze> listaProduktow;

		public FunktorDwuklikAkcjaFaktura(
				FakturyPanelListaWybranychAbstrakt listaWybranych,
				List<ProduktWFakturze> listaProduktow,
				FakturyPanelEdytujDodaj parent) {
			this.listaWybranych = listaWybranych;
			this.listaProduktow = listaProduktow;
			this.parent = parent;
		}

		@Override
		public void run(ObiektWiersz wiersz, int rowIndex)
		{
			/* Funktor Dwuklik Akcja */
			int ilosc = 1;
			String id_produktu = wiersz.wiersz[3];
			if (czyProduktJestNaLiscie(Integer.parseInt(id_produktu)))
				return;
			/* usuwanie ilosci */
			try
			{
				parent.magazynZmienIloscProduktow(rowIndex, -ilosc,
						parent.rodzajFaktury);
			} catch (Exception e)
			{
				MojeUtils.showError(e);
				return;
			}
			Produkt produktSlownik = (Produkt) ProduktBaza
					.pobierzObiektZBazy(id_produktu);
			int cena = produktSlownik.cena_zakupu;
			int lp = listaWybranych.zwiekszLP();
			int mnoznik = 100;
			if (parent.pobierzKontrachenta() != null)
				mnoznik += parent.pobierzKontrachenta()._cena_mnoznik;
			cena = ((cena) * mnoznik / 100);
			listaWybranych
					.tabelaDodajWiersz(new String[]
					{ "" + lp, wiersz.wiersz[1],
							MojeUtils.utworzWartoscZlotowki(cena), ilosc + "",
							MojeUtils.utworzWartoscZlotowki(ilosc * cena) });

			listaProduktow.add(new ProduktWFakturze(lp, cena, ilosc,
					produktSlownik, false, 0));
		}

		public boolean czyProduktJestNaLiscie(int id)
		{
			for (ProduktWFakturze _produkt : listaProduktow)
				if (_produkt.produkt.id_produkt == id)
					return true;
			return false;
		}
	}

	@Override
	public void uzupelnijFormatke(Object obiekt, JDialog parent)
	{
		init();
		faktura = (Faktura) obiekt;
		if (listaWybranychPanel instanceof FakturyPanelListaWybranych)
			((FakturyPanelListaWybranych) listaWybranychPanel)
					.zaladujListeFaktury();
		else
			((FakturyKorektaPanelListaWybranych) listaWybranychPanel)
					.zaladujListeFaktury();
		listaProduktow.addAll(faktura.getProdukty_copy());
		if (faktura.isKorekta && faktura.produktyKorekta != null
				&& !faktura.produktyKorekta.isEmpty())
		{
			((FakturyPanelKorekta) listaWybranychPanel.fakturyPanel).listaProduktowKorekta
					.addAll(faktura.getProduktyKorekta_copy());
		}
		{
			/*
			 * Przypadek w którym wczytano fakturę z pliku .xls - nie występuje
			 * w niej kontrahent
			 */
			if (faktura.kontrahent == null)
			{
				comboKontrachenci.setSelectedIndex(-1);
			} else
			{
				tmpNieZmieniaKontrachenta = true;
				comboKontrachenci.setSelectedItem(faktura.kontrahent.nazwa);
				tmpNieZmieniaKontrachenta = false;
			}
		}
		{
			/* long to int (jebana JAVA) */
			int tmp = Integer.parseInt(faktura.waluta.toString());
			System.err.println(tmp - 1);
			comboWaluta.setSelectedIndex(tmp - 1);
		}
		panelDatyWystawFaktury.setDate(faktura.data_wystawienia);
		panelTerminPlatnosci.setDate(faktura.termin_platnosci);
		if (rodzajFaktury == Faktura.ZAKUP)
		{
			numerField.setText("" + faktura.numer);
		}
	}

	protected void init()
	{
		try
		{
			panelMagazynu.wczytajTabele();
			terminPlatnosciButtonListener.ukryj();
			dataWystawieniaFakturyButtonListener.ukryj();
			listaProduktow.clear();
			comboKontrachenci.setModel(MojeUtils.odswiezCombo(false));
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void czysc()
	{
		try
		{
			init();
			((FakturyPanelListaWybranych) listaWybranychPanel).czysc();
			// tutaj 2
			comboKontrachenci.setSelectedIndex(-1);
			if (rodzajFaktury == Faktura.ZAKUP)
				numerField.setText("");
			panelDatyWystawFaktury.setDate(new Date());
			panelTerminPlatnosci.setDate(new Date());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public Faktura getFaktura()
	{
		return faktura;
	}

	@Override
	public ObiektZId pobierzZFormatki(int id) throws Exception
	{
		Kontrachent kontrahent = pobierzKontrachenta();

		if (kontrahent == null)
		{
			throw new UserShowException("Nie wybrano kontrachenta");
		}

		Long waluta = pobierzWalute();

		if (waluta < 0)
		{
			throw new UserShowException("Nie wybrano waluty dla faktury!");
		}

		if (listaProduktow.isEmpty())
			throw new UserShowException("Lista produktów nie może być pusta!");
		if (id == 0)
		{
			/* id = 0 gdy nowe, inaczej edytujemy istniejace */
			/* walidacja */
			int numer;
			if (rodzajFaktury == Faktura.ZAKUP)
			{
				if (!MojeUtils.isNumer(numerField.getText()))
				{
					throw new UserShowException("Podano niepoprawny numer");
				}
				numer = Integer.parseInt(numerField.getText());
			} else
			{
				numer = FakturaBaza.pobierzNrFaktury(rodzajFaktury) + 1;
			}
			Date data_wystawienia = panelDatyWystawFaktury.getDate();
			Date termin_platnosci = panelTerminPlatnosci.getDate();
			if (data_wystawienia.after(termin_platnosci))
				throw new UserShowException(
						"Data wystawienia nie może być po terminie płatności!");

			int wartosc = 0;
			for (ProduktWFakturze p : listaProduktow)
				wartosc += p.liczWartoscZNarzutem();

			List<ProduktWFakturze> copy_lista = new ArrayList<ProduktWFakturze>();
			copy_lista.addAll(listaProduktow);
			boolean zaplacona = false;
			return new Faktura(0, numer, data_wystawienia, termin_platnosci,
					zaplacona, rodzajFaktury, kontrahent, wartosc, true, false,
					copy_lista, null, waluta);
		} else
		{
			int wartosc = 0;
			for (ProduktWFakturze p : listaProduktow)
				wartosc += p.liczWartoscZNarzutem();
			List<ProduktWFakturze> copy_lista = new ArrayList<ProduktWFakturze>();
			copy_lista.addAll(listaProduktow);

			int numer;
			if (rodzajFaktury == Faktura.ZAKUP)
			{
				if (!MojeUtils.isNumer(numerField.getText()))
				{
					throw new UserShowException("Podano niepoprawny numer");
				}
				numer = Integer.parseInt(numerField.getText());
			} else
			{
				numer = faktura.numer;
			}
			return new Faktura(faktura.id_faktura, numer,
					panelDatyWystawFaktury.getDate(),
					panelTerminPlatnosci.getDate(), false, rodzajFaktury,
					kontrahent, wartosc, true, faktura.isKorekta, copy_lista,
					null, waluta);
		}
	}

	public Kontrachent pobierzKontrachenta()
	{
		if (comboKontrachenci.getSelectedIndex() < 0)
			return null;
		return (Kontrachent) KontrachentBaza
				.pobierzObiektZBazy(comboKontrachenci.getSelectedItem()
						.toString());
	}

	public Long pobierzWalute()
	{
		if (comboWaluta.getSelectedIndex() < 0)
			return -1L;
		System.err.println("Zmieniłem");
		return WalutaManager.pobierzIdZBazy(comboWaluta.getSelectedItem()
				.toString(), Waluta.tabelaWaluta);

	}

	public ProduktWFakturze pobierzProduktZListy(int lp)
	{
		for (ProduktWFakturze _produkt : listaProduktow)
			if (_produkt.lp == lp)
				return _produkt;
		return null;
	}

	public void usunProduktZListy(int lp)
	{
		for (ProduktWFakturze _produkt : listaProduktow)
			if (_produkt.lp == lp)
			{
				listaProduktow.remove(_produkt);
				int index = szukajIndexProduktuWModelu(_produkt);
				if (index >= 0)
				{
					int ilosc = Integer.parseInt((String) panelMagazynu
							.getModelValueAt(index, 2));
					if (rodzajFaktury == Faktura.SPRZEDAZ)
						ilosc += _produkt.ilosc_produktu;
					else
						ilosc -= _produkt.ilosc_produktu;
					panelMagazynu.setModelValueAt("" + ilosc, index, 2);
				}
				return;
			}
	}

	int szukajIndexProduktuWModelu(ProduktWFakturze _produkt)
	{
		for (int i = 0; i < panelMagazynu.getModelRows(); ++i)
		{
			String s = (String) panelMagazynu.getModelValueAt(i, 1);
			if (s.equals(_produkt.produkt.nazwa))
				return i;
		}
		return -1;
	}

	public void aktualizujProdukty_LP(int usuwanyProduktLP)
	{
		for (ProduktWFakturze _produkt : listaProduktow)
			if (_produkt.lp > usuwanyProduktLP)
			{
				--_produkt.lp;
			}
	}

	/**
	 * @throws w
	 *             przypadku odjecia zbyt duzej ilosci rzuca bledem
	 */
	public void magazynZmienIloscProduktow(int rowIndex, int wartoscZmiany,
			int rodzaj) throws Exception
	{
		int columnIndex = 2;
		int _ilosc_magazyn = Integer.parseInt((String) panelMagazynu
				.getModelValueAt(rowIndex, columnIndex));
		int nowa_wartosc;
		if (rodzaj == Faktura.SPRZEDAZ)
		{
			if (_ilosc_magazyn + wartoscZmiany < 0)
				throw new UserShowException(
						"Nie można odjąć tyle produktów z magazynu!");
			nowa_wartosc = (_ilosc_magazyn + wartoscZmiany);
		} else
		{
			if (_ilosc_magazyn - wartoscZmiany < 0)
				throw new UserShowException(
						"Nie można odjąć tyle produktów z magazynu!");
			nowa_wartosc = (_ilosc_magazyn - wartoscZmiany);
		}
		panelMagazynu.setModelValueAt("" + nowa_wartosc, rowIndex, columnIndex);
	}

	/**
	 * @throws w
	 *             przypadku odjecia zbyt duzej ilosci rzuca bledem
	 */
	public void aktualizujIloscProduktow_panelMagazyn(ProduktWFakturze produkt,
			int nowa_wartosc, int rodzaj) throws Exception
	{
		int ilosc_pobrana = produkt.ilosc_produktu - nowa_wartosc;
		if (ilosc_pobrana == 0)
			return;
		int row = szukajIndexProduktuWModelu(produkt);
		magazynZmienIloscProduktow(row, ilosc_pobrana, rodzaj);
	}
}