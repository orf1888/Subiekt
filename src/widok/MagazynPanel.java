package widok;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import kontroler.ObiektWyszukanieWarunki;
import kontroler.ProduktBaza;
import kontroler.SlownikManager;
import model.SlownikElement;
import model.produkt.Produkt;
import utils.MojeUtils;
import widok.abstrakt.PanelEdytujDodajObiekt;
import widok.abstrakt.PanelOgolnyParametry;
import widok.abstrakt.PanelOgolnyPrzyciski;

public class MagazynPanel extends PanelOgolnyPrzyciski
{
	private static final long serialVersionUID = -7165280561344816269L;

	public static final String dowolnyProducent = "dowolny producent";

	private JComboBox<Object> comboBoxFiltrowanieProducent = null;

	@SuppressWarnings("rawtypes")
	private static List<JComboBox> listaComboBoxowFiltrProducenta = new ArrayList<JComboBox>();

	/**
	 * pop-up dla akcji PPM zakładki magazyn
	 */
	private JPopupMenu tworzMenuPopup()
	{
		JPopupMenu result = new JPopupMenu();
		/* Guzik edytuj */
		JMenuItem itemEdytuj = new JMenuItem("Edytuj", null);
		result.add(itemEdytuj);
		itemEdytuj.setHorizontalTextPosition(SwingConstants.RIGHT);
		edytujListener = tworzEdytujListener();
		itemEdytuj.addActionListener(edytujListener);
		/* Guzik usuń */
		JMenuItem itemCosTam = new JMenuItem("Usuń", null);
		result.add(itemCosTam);
		itemCosTam.setHorizontalTextPosition(SwingConstants.RIGHT);
		usunListener = tworzUsunListener();
		itemCosTam.addActionListener(usunListener);
		/* Guzik informator */
		JMenuItem itemInformator = new JMenuItem("Informator", null);
		result.add(itemInformator);
		itemInformator.setHorizontalTextPosition(SwingConstants.RIGHT);
		informatorListener = tworzInformatorListener();
		itemInformator.addActionListener(informatorListener);
		/*
		 * Tu powinna znajdować się reszta funkcjonalności menu kontekstowego
		 */
		return result;
	}

	private JPopupMenu tworzPopupFakturaWysylka()
	{
		JPopupMenu result = new JPopupMenu();
		/* Guzik informator */
		JMenuItem itemInformator = new JMenuItem("Informator", null);
		result.add(itemInformator);
		itemInformator.setHorizontalTextPosition(SwingConstants.RIGHT);
		informatorListener = tworzInformatorListener();
		itemInformator.addActionListener(informatorListener);
		return result;
	}

	/**
	 * Create the panel.
	 * 
	 * @param wyswietlButtony
	 * @param popupMenu
	 *            - mozna podac wlasny popup, jesli chcemy default to podajemy
	 *            null
	 */
	public MagazynPanel(boolean wyswietlButtony, JPopupMenu popupMenu, boolean s) {
		super(s);
		try
		{
			if (popupMenu == null)
				popupMenu = tworzMenuPopup();
			if (!popupMenu.isEnabled())
				popupMenu = tworzPopupFakturaWysylka();

			warunki = new ObiektWyszukanieWarunki(new Produkt());
			PanelEdytujDodajObiekt pWyswietl = null;
			PanelEdytujDodajObiekt pEdytuj = null;
			PanelEdytujDodajObiekt pDodaj = null;
			if (wyswietlButtony)
			{
				pDodaj = new ProduktPanelEdytujDodaj("Dodaj", true);
				pEdytuj = new ProduktPanelEdytujDodaj("Edytuj", true);
				pWyswietl = new ProduktPanelEdytujDodaj("Dane", false);
			}
			init(new PanelOgolnyParametry(initModelFunktor, /*
															 * dodajListener,
															 * edytujListener,
															 * usunListener,
															 * szukajListener,
															 * anulujSzukanieListener
															 * ,
															 */
			Produkt.kolumnyWyswietlane.length - 1, popupMenu,
					ProduktBaza.instance(), pDodaj, pEdytuj, pWyswietl,
					wyswietlButtony, Produkt.kolumnyWyswietlane), false);

			/* filtrowanie po producencie u góry tabelki */
			{
				JLabel lblFiltrowanieProducent = new JLabel("Producent:");
				GridBagConstraints gbc_lblFgffg = new GridBagConstraints();
				gbc_lblFgffg.insets = new Insets(0, 0, 0, 5);
				gbc_lblFgffg.gridx = 0;
				gbc_lblFgffg.gridy = 0;
				panel_naglowek.add(lblFiltrowanieProducent, gbc_lblFgffg);

				String[] producenci = SlownikManager.pobierzWszystkieZBazy(
						SlownikElement.tabelaProducent, true);

				comboBoxFiltrowanieProducent = new JComboBox<Object>(producenci);
				GridBagConstraints gbc_comboBox = new GridBagConstraints();
				gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBox.gridx = 1;
				gbc_comboBox.gridy = 0;
				panel_naglowek.add(comboBoxFiltrowanieProducent, gbc_comboBox);

				comboBoxFiltrowanieProducent
						.addActionListener(new ActionListener()
						{
							@Override
							public void actionPerformed(ActionEvent arg0)
							{
								String producent = (String) comboBoxFiltrowanieProducent
										.getSelectedItem();
								filtruj(producent);
							}
						});

				listaComboBoxowFiltrProducenta
						.add(comboBoxFiltrowanieProducent);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			MojeUtils.showPrintError(e);
		}
	}

	public static void dodajComboBoxFiltrProducenta(String strProducent)
	{
		for (JComboBox<Object> comboBox : listaComboBoxowFiltrProducenta)
			comboBox.addItem(strProducent);
	}

	public static void usunComboBoxFiltrProducenta(String strProducent)
	{
		for (JComboBox<Object> comboBox : listaComboBoxowFiltrProducenta)
			comboBox.removeItem(strProducent);
	}

	protected void filtruj(String producent)
	{
		try
		{
			if (producent.equals(dowolnyProducent))
			{
				/* usuwamy filtr po producencie */
				warunki.removeWarunek("id_producenta");
			} else
			{
				Long producent_id = SlownikManager.pobierzIdZBazy(producent,
						SlownikElement.tabelaProducent);
				if (producent_id == null)
					return;
				warunki.dodajWarunek(producent_id, "id_producenta");
			}
			przeladujTabele(false);
		} catch (Exception e)
		{
			MojeUtils.showPrintError(e);
		}
	}

	private final InitModelFunktor initModelFunktor = new InitModelFunktor()
	{
		@Override
		public String[][] getBeginningData()
		{
			try
			{
				warunki.dodajWarunek(1, "widoczny");
				return obiektBazaManager.pobierzWierszeZBazy(warunki);
			} catch (Exception e)
			{
				MojeUtils.showPrintError(e);
				return null;
			}
		}
	};
}