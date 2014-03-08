package widok;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import kontroler.InformatorBaza;
import kontroler.ProduktBaza;
import model.ObiektWiersz;
import utils.DataUtils;
import utils.Globals;
import utils.MojeUtils;

public class InformatorOkno extends JDialog
{

	private static final long serialVersionUID = -6195767829553617471L;
	private final JPanel contentPane;
	/* Tabele */
	private final JTable tableWysylki;
	private final JTable tableSprzedaz;
	private final JTable tableIloscWyslanych;
	private final JTable tableIloscSprzedazy;
	private final JTable tableRuchTowaru;
	/* Panele przyciskow */
	private final PanelPrzyciskowInformator panelPrzyciskowSprzedazIlosc;
	private final PanelPrzyciskowInformator panelPrzyciskowWysylki;
	private final PanelPrzyciskowInformator panelPrzyciskowSprzedaz;
	private final PanelPrzyciskowInformator panelPrzyciskowWyslanychIlosc;
	private final PanelPrzyciskowInformator panelPrzyciskowRuchTowaru;
	/* Panele glowne */
	private final JPanel panelIloscSprzedazy;
	private final JPanel panelWysylki;
	private final JPanel panelSprzedazy;
	private final JPanel panelIloscWyslanych;
	private final JPanel panelRuchTowaru;
	/* ScrollPane'y */
	private final JScrollPane scrollPaneIloscSprzedazy;
	private final JScrollPane scrollPaneWysylki;
	private final JScrollPane scrollPaneSprzedazy;
	private final JScrollPane scrollPaneIloscWyslanych;
	private final JScrollPane scrollPaneRuchTowaru;

	public ObiektWiersz wiersz;

	public InformatorOkno(String nazwa, ObiektWiersz wiersz) {
		this.wiersz = wiersz;

		setModalityType(ModalityType.APPLICATION_MODAL);
		setIconImage(Globals.IkonaAplikacji.getImage());
		setTitle("Informator - " + nazwa);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(750, 600);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		/* Wysyłki */
		panelWysylki = new JPanel();
		tabbedPane.addTab("Wysyłki za okres", null, panelWysylki, null);
		panelWysylki.setLayout(new BorderLayout(0, 0));
		scrollPaneWysylki = new JScrollPane();
		panelWysylki.add(scrollPaneWysylki, BorderLayout.CENTER);
		tableWysylki = new JTable();
		scrollPaneWysylki.setViewportView(tableWysylki);
		panelPrzyciskowWysylki = new PanelPrzyciskowInformator(
				pokazWysylkiListener);
		panelWysylki.add(panelPrzyciskowWysylki, BorderLayout.NORTH);
		/* Sprzedaż */
		panelSprzedazy = new JPanel();
		tabbedPane.addTab("Sprzedaż za okres", null, panelSprzedazy, null);
		panelSprzedazy.setLayout(new BorderLayout(0, 0));
		scrollPaneSprzedazy = new JScrollPane();
		panelSprzedazy.add(scrollPaneSprzedazy, BorderLayout.CENTER);
		tableSprzedaz = new JTable();
		scrollPaneSprzedazy.setViewportView(tableSprzedaz);
		panelPrzyciskowSprzedaz = new PanelPrzyciskowInformator(
				pokazSprzedazListener);
		panelSprzedazy.add(panelPrzyciskowSprzedaz, BorderLayout.NORTH);

		/* Ilosc sprzedaży */
		panelIloscSprzedazy = new JPanel();
		tabbedPane.addTab("Ilość sprzedaży za okres", null,
				panelIloscSprzedazy, null);
		panelIloscSprzedazy.setLayout(new BorderLayout(0, 0));
		scrollPaneIloscSprzedazy = new JScrollPane();
		panelIloscSprzedazy.add(scrollPaneIloscSprzedazy, BorderLayout.CENTER);
		tableIloscSprzedazy = new JTable();
		scrollPaneIloscSprzedazy.setViewportView(tableIloscSprzedazy);
		panelPrzyciskowSprzedazIlosc = new PanelPrzyciskowInformator(
				pokazSprzedazIloscListener);
		panelIloscSprzedazy.add(panelPrzyciskowSprzedazIlosc,
				BorderLayout.NORTH);

		/* Ilosc wysłanych */
		panelIloscWyslanych = new JPanel();
		tabbedPane.addTab("Ilość wysłanych za okres", null,
				panelIloscWyslanych, null);
		panelIloscWyslanych.setLayout(new BorderLayout(0, 0));
		scrollPaneIloscWyslanych = new JScrollPane();
		panelIloscWyslanych.add(scrollPaneIloscWyslanych, BorderLayout.CENTER);
		tableIloscWyslanych = new JTable();
		scrollPaneIloscWyslanych.setViewportView(tableIloscWyslanych);
		panelPrzyciskowWyslanychIlosc = new PanelPrzyciskowInformator(
				pokazWyslanychIloscListener);
		panelIloscWyslanych.add(panelPrzyciskowWyslanychIlosc,
				BorderLayout.NORTH);

		/* Ruch towaru */
		panelRuchTowaru = new JPanel();
		tabbedPane.addTab("Ruch towaru za okres", null, panelRuchTowaru, null);
		panelRuchTowaru.setLayout(new BorderLayout(0, 0));
		scrollPaneRuchTowaru = new JScrollPane();
		panelRuchTowaru.add(scrollPaneRuchTowaru, BorderLayout.CENTER);
		tableRuchTowaru = new JTable();
		scrollPaneRuchTowaru.setViewportView(tableRuchTowaru);
		panelPrzyciskowRuchTowaru = new PanelPrzyciskowInformator(
				pokazRuchTowaruListener);
		panelRuchTowaru.add(panelPrzyciskowRuchTowaru, BorderLayout.NORTH);

	}

	ActionListener pokazWysylkiListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				String[][] data = InformatorBaza.instance()
						.pobierzWysylekZBazy(
								(ProduktBaza.getIdFromWiersz(wiersz)),
								DataUtils.stringToDate_format
										.format(panelPrzyciskowWysylki.dataOd
												.getDate()),
								DataUtils.stringToDate_format
										.format(panelPrzyciskowWysylki.dataDo
												.getDate()), true);
				/* Ustaw model */
				DefaultTableModel model_wysylki = new DefaultTableModel(data,
						InformatorBaza.kolumny_sprzedaz_ilosc);
				tableWysylki.setModel(model_wysylki);
			} catch (Exception e1)
			{
				MojeUtils.showError("Coś nie tak.");
				MojeUtils.error(e1);;
			}
		}
	};

	ActionListener pokazSprzedazListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				String[][] data = InformatorBaza.instance()
						.pobierzSrzedazZBazy(
								(ProduktBaza.getIdFromWiersz(wiersz)),
								DataUtils.stringToDate_format
										.format(panelPrzyciskowSprzedaz.dataOd
												.getDate()),
								DataUtils.stringToDate_format
										.format(panelPrzyciskowSprzedaz.dataDo
												.getDate()), true);
				/* Ustaw model */
				DefaultTableModel model_sprzedaz = new DefaultTableModel(data,
						InformatorBaza.kolumny_sprzedaz_ilosc);
				tableSprzedaz.setModel(model_sprzedaz);
			} catch (Exception e1)
			{
				MojeUtils.showError("Coś nie tak.");
				MojeUtils.error(e1);;
			}
		}
	};

	ActionListener pokazSprzedazIloscListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				String[][] data = InformatorBaza
						.instance()
						.pobierzIloscSprzedazZBazy(
								(ProduktBaza.getIdFromWiersz(wiersz)),
								DataUtils.stringToDate_format
										.format(panelPrzyciskowSprzedazIlosc.dataOd
												.getDate()),
								DataUtils.stringToDate_format
										.format(panelPrzyciskowSprzedazIlosc.dataDo
												.getDate()));
				/* Ustaw model */
				DefaultTableModel model_sprzedaz_ilosc = new DefaultTableModel(
						data, InformatorBaza.kolumny_sprzedaz_ilosc);
				tableIloscSprzedazy.setModel(model_sprzedaz_ilosc);
			} catch (Exception e1)
			{
				MojeUtils.showError("Coś nie tak.");
				MojeUtils.error(e1);;
			}
		}
	};

	ActionListener pokazWyslanychIloscListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				String[][] data = InformatorBaza
						.instance()
						.pobierzIloscWyslanychZBazy(
								(ProduktBaza.getIdFromWiersz(wiersz)),
								DataUtils.stringToDate_format
										.format(panelPrzyciskowWyslanychIlosc.dataOd
												.getDate()),
								DataUtils.stringToDate_format
										.format(panelPrzyciskowWyslanychIlosc.dataDo
												.getDate()));
				/* Ustaw model */
				DefaultTableModel model_sprzedaz_ilosc = new DefaultTableModel(
						data, InformatorBaza.kolumny_wyslane_ilosc);
				tableIloscWyslanych.setModel(model_sprzedaz_ilosc);
			} catch (Exception e1)
			{
				MojeUtils.showError("Coś nie tak.");
				MojeUtils.error(e1);;
			}
		}
	};

	ActionListener pokazRuchTowaruListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				String[][] data = InformatorBaza
						.instance()
						.pobierzRuchTowaruZBazy(
								(ProduktBaza.getIdFromWiersz(wiersz)),
								DataUtils.stringToDate_format
										.format(panelPrzyciskowRuchTowaru.dataOd
												.getDate()),
								DataUtils.stringToDate_format
										.format(panelPrzyciskowRuchTowaru.dataDo
												.getDate()));
				/* Ustaw model */
				DefaultTableModel model_ruch_towaru = new DefaultTableModel(
						data, InformatorBaza.kolumny_ruch_towaru);
				tableRuchTowaru.setModel(model_ruch_towaru);
			} catch (Exception e1)
			{
				MojeUtils.showError("Coś nie tak.");
				MojeUtils.error(e1);;
			}
		}
	};

	public static void init(String nazwa, ObiektWiersz wiersz)
	{
		InformatorOkno informator = new InformatorOkno(nazwa, wiersz);
		informator.setVisible(true);
	}
}