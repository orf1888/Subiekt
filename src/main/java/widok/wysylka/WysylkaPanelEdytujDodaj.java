package widok.wysylka;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import kontroler.ProduktBaza;
import model.ObiektWiersz;
import model.ObiektZId;
import model.Wysylka;
import model.produkt.Produkt;
import model.produkt.ProduktWWysylce;
import net.sf.nachocalendar.components.DatePanel;
import utils.DataUtils;
import utils.MojeUtils;
import utils.UserShowException;
import widok.MagazynPanel;
import widok.abstrakt.PanelEdytujDodajObiekt;
import widok.abstrakt.PanelOgolnyTabela.FunktorDwuklikTabela;

public class WysylkaPanelEdytujDodaj extends PanelEdytujDodajObiekt
{
	private static final long serialVersionUID = 7011981897503862573L;

	DatePanel panelData;

	public List<ProduktWWysylce> listaProduktow;

	private Wysylka wysylka;

	WysylkaPanelListaWybranych listaWybranychPanel;

	MagazynPanel panelMagazynu;

	JTextField numerField;

	private ButtonPokazywaniaPaneluListener dataButtonListener;

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
	 * @param editable
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public WysylkaPanelEdytujDodaj(String tytul, boolean editable,
			JPopupMenu popupMagazynWWysylce) throws Exception {
		this.listaProduktow = new ArrayList<ProduktWWysylce>();
		setBorder(new TitledBorder(null, tytul + " wysyłkę",
				TitledBorder.CENTER, TitledBorder.TOP, null, null));
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
		/* data */
		{
			_gridx++;
			panelData = new DatePanel();
			final JButton dataButton = new JButton("Pokaż datę");
			dataButton
					.addActionListener(dataButtonListener = new ButtonPokazywaniaPaneluListener(
							panelData, dataButton, "Ukryj datę", "Pokaż datę"));
			GridBagConstraints gbc_data = new GridBagConstraints();
			gbc_data.gridx = _gridx;
			gbc_data.gridy = 0;
			tloPanel.add(dataButton, gbc_data);

			GridBagConstraints gbc_panelData = new GridBagConstraints();
			gbc_panelData.gridx = _gridx;
			gbc_panelData.gridy = 1;
			tloPanel.add(panelData, gbc_panelData);
			panelData.hide();
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

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		add(splitPane);

		/* A PO PRAWEJ LISTA WYBRAŃCÓW */
		listaWybranychPanel = new WysylkaPanelListaWybranych(this, false);
		splitPane.setRightComponent(listaWybranychPanel);

		/* PO LEWEJ STRONIE MAGAZYNEK */
		{
			panelMagazynu = new MagazynPanel( /* wyswietlButtony */false,
					popupMagazynWWysylce, false);
			panelMagazynu.zmienDwuklikFunktor(new FunktorDwuklikAkcjaWysylka(
					listaWybranychPanel, listaProduktow, this));
			splitPane.setLeftComponent(panelMagazynu);
		}
	}

	public static class FunktorDwuklikAkcjaWysylka extends FunktorDwuklikTabela
	{
		WysylkaPanelListaWybranych listaWybranych;

		WysylkaPanelEdytujDodaj parent;

		List<ProduktWWysylce> listaProduktow;

		public FunktorDwuklikAkcjaWysylka(
				WysylkaPanelListaWybranych listaWybranych,
				List<ProduktWWysylce> listaProduktow,
				WysylkaPanelEdytujDodaj parent) {
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
			try
			{
				parent.magazynZmienIloscProduktow(rowIndex, -ilosc);
			} catch (Exception e)
			{
				MojeUtils.showError(e);
				return;
			}
			Produkt produktSlownik = (Produkt) ProduktBaza
					.pobierzObiektZBazy(id_produktu);
			int lp = listaWybranych.zwiekszLP();
			listaWybranych.tabelaDodajWiersz(new String[]
			{ "" + lp, wiersz.wiersz[0], wiersz.wiersz[1], ilosc + "" });

			listaProduktow.add(new ProduktWWysylce(lp, ilosc, produktSlownik));
		}

		public boolean czyProduktJestNaLiscie(int id)
		{
			for (ProduktWWysylce _produkt : listaProduktow)
				if (_produkt.produkt.id_produkt == id)
					return true;
			return false;
		}
	}

	@Override
	public void uzupelnijFormatke(Object obiekt, JDialog parent)
	{
		init();
		wysylka = (Wysylka) obiekt;
		listaWybranychPanel.zaladujListeWysylki();
		listaProduktow.addAll(wysylka.getProdukty_copy());
		try
		{
			DateFormat dateToString_format = new SimpleDateFormat("dd-MM-yyyy",
					Locale.US);
			Date data_wystawienia_wysylki = dateToString_format
					.parse(DataUtils.dateToString_format
							.format(wysylka.data_wysylki));
			panelData.setDate(data_wystawienia_wysylki);
		} catch (ParseException e)
		{
			MojeUtils.showError(e);
		}
	}

	private void init()
	{
		try
		{
			panelMagazynu.wczytajTabele(true);
			dataButtonListener.ukryj();
			listaProduktow.clear();
		} catch (Exception e)
		{
			MojeUtils.error(e);
		}
	}

	@Override
	public void czysc()
	{
		try
		{
			init();
			listaWybranychPanel.czysc();
			// numerField.setText( "" );
			panelData.setDate(new Date());
		} catch (Exception e)
		{
			MojeUtils.error(e);
		}
	}

	public Wysylka getWysylka()
	{
		return wysylka;
	}

	@Override
	public ObiektZId pobierzZFormatki(int id) throws Exception
	{
		if (listaProduktow.isEmpty())
			throw new UserShowException("Lista towarów nie może być pusta!");
		if (id == 0)
		{
			Date data = panelData.getDate();

			List<ProduktWWysylce> copy_lista = new ArrayList<ProduktWWysylce>();
			copy_lista.addAll(listaProduktow);
			return new Wysylka(0, data, copy_lista);
		} else
		{
			List<ProduktWWysylce> copy_lista = new ArrayList<ProduktWWysylce>();
			copy_lista.addAll(listaProduktow);
			return new Wysylka(wysylka.id, panelData.getDate(), copy_lista);
		}
	}

	public ProduktWWysylce pobierzProduktZListy(int lp)
	{
		for (ProduktWWysylce _produkt : listaProduktow)
			if (_produkt.lp == lp)
				return _produkt;
		return null;
	}

	public void usunProduktZListy(int lp)
	{
		for (ProduktWWysylce _produkt : listaProduktow)
			if (_produkt.lp == lp)
			{
				listaProduktow.remove(_produkt);
				int index = szukajIndexProduktuWModelu(_produkt);
				if (index >= 0)
				{
					int ilosc = Integer.parseInt((String) panelMagazynu
							.getModelValueAt(index, 2));
					ilosc -= _produkt.ilosc_produktu;
					panelMagazynu.setModelValueAt("" + ilosc, index, 2);
				}
				return;
			}
	}

	private int szukajIndexProduktuWModelu(ProduktWWysylce _produkt)
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
		for (ProduktWWysylce _produkt : listaProduktow)
			if (_produkt.lp > usuwanyProduktLP)
			{
				--_produkt.lp;
			}
	}

	/**
	 * @throws w
	 *             przypadku odjecia zbyt duzej ilosci rzuca bledem
	 */
	public void magazynZmienIloscProduktow(int rowIndex, int wartoscZmiany)
			throws Exception
	{
		int columnIndex = 2;
		int _ilosc_magazyn = Integer.parseInt((String) panelMagazynu
				.getModelValueAt(rowIndex, columnIndex));
		int nowa_wartosc = (_ilosc_magazyn - wartoscZmiany);
		panelMagazynu.setModelValueAt("" + nowa_wartosc, rowIndex, columnIndex);
	}

	/**
	 * @throws w
	 *             przypadku odjecia zbyt duzej ilosci rzuca bledem
	 */
	public void aktualizujIloscProduktow_panelMagazyn(ProduktWWysylce produkt,
			int nowa_wartosc) throws Exception
	{
		int ilosc_pobrana = produkt.ilosc_produktu - nowa_wartosc;
		if (ilosc_pobrana == 0)
			return;
		int row = szukajIndexProduktuWModelu(produkt);
		magazynZmienIloscProduktow(row, ilosc_pobrana);
	}
}