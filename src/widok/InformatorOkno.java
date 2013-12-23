package widok;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

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
import utils.MojeUtils;

public class InformatorOkno extends JDialog
{

	private static final long serialVersionUID = -6195767829553617471L;
	private final JPanel contentPane;
	private final JTable tableWysylki;
	private final JTable tableSprzedaz;
	private final JPanel panelIloscSprzedazy;
	private final JScrollPane scrollPaneIloscSprzedazy;
	private final JTable tableIloscSprzedazy;
	private final PanelPrzyciskowInformator panelPrzyciskowSprzedazIlosc;
	private final PanelPrzyciskowInformator panelPrzyciskowWysylki;
	private final PanelPrzyciskowInformator panelPrzyciskowSprzedaz;
	private final JPanel panelWysylki;
	private final JPanel panelSprzedazy;
	private final JScrollPane scrollPaneWysylki;
	private final JScrollPane scrollPaneSprzedazy;

	public ObiektWiersz wiersz;

	public InformatorOkno(String nazwa, ObiektWiersz wiersz) {
		this.wiersz = wiersz;

		setModalityType(ModalityType.APPLICATION_MODAL);
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
		tabbedPane.addTab("Sprzedaż", null, panelSprzedazy, null);
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
		tabbedPane.addTab("Ilość sprzedaży", null, panelIloscSprzedazy, null);
		panelIloscSprzedazy.setLayout(new BorderLayout(0, 0));
		scrollPaneIloscSprzedazy = new JScrollPane();
		panelIloscSprzedazy.add(scrollPaneIloscSprzedazy, BorderLayout.CENTER);
		tableIloscSprzedazy = new JTable();
		scrollPaneIloscSprzedazy.setViewportView(tableIloscSprzedazy);
		panelPrzyciskowSprzedazIlosc = new PanelPrzyciskowInformator(
				pokazSprzedazIloscListener);
		panelIloscSprzedazy.add(panelPrzyciskowSprzedazIlosc,
				BorderLayout.NORTH);
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
								(ProduktBaza.getIdFromWiersz(wiersz)));
				/* Ustaw model */
				DefaultTableModel model_wysylki = new DefaultTableModel(data,
						InformatorBaza.kolumny_sprzedaz_ilosc);
				tableWysylki.setModel(model_wysylki);
			} catch (SQLException e1)
			{
				MojeUtils.showError("Coś nie tak.");
				e1.printStackTrace();
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
								(ProduktBaza.getIdFromWiersz(wiersz)));
				/* Ustaw model */
				DefaultTableModel model_sprzedaz = new DefaultTableModel(data,
						InformatorBaza.kolumny_sprzedaz_ilosc);
				tableSprzedaz.setModel(model_sprzedaz);
			} catch (SQLException e1)
			{
				MojeUtils.showError("Coś nie tak.");
				e1.printStackTrace();
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
				@SuppressWarnings("deprecation")
				String[][] data = InformatorBaza.instance()
						.pobierzIloscSprzedazZBazy(
								(ProduktBaza.getIdFromWiersz(wiersz)),
								panelPrzyciskowSprzedazIlosc.dataOd.getDate()
										.toGMTString(),
								panelPrzyciskowSprzedazIlosc.dataDo.getDate()
										.toGMTString());
				/* Ustaw model */
				DefaultTableModel model_sprzedaz_ilosc = new DefaultTableModel(
						data, InformatorBaza.kolumny_sprzedaz_ilosc);
				tableIloscSprzedazy.setModel(model_sprzedaz_ilosc);
			} catch (SQLException e1)
			{
				MojeUtils.showError("Coś nie tak.");
				e1.printStackTrace();
			}
		}
	};

	public static void init(String nazwa, ObiektWiersz wiersz)
	{
		InformatorOkno informator = new InformatorOkno(nazwa, wiersz);
		informator.setVisible(true);
	}
}