package widok;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalExclusionType;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import kontroler.InformatorBaza;

public class InformatorOkno extends JFrame
{

	private static final long serialVersionUID = -6195767829553617471L;
	private final JPanel contentPane;
	private final JTable tableWysylki;
	private final JTable tableSprzedaz;

	public InformatorOkno(String nazwa, String[][] data_sprzedaz,
			String[][] data_wysylki) {
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setTitle("Informator - " + nazwa);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JScrollPane scrollPaneWysylki = new JScrollPane();
		tabbedPane.addTab("Wysyłki", null, scrollPaneWysylki, null);

		tableWysylki = new JTable();
		scrollPaneWysylki.setViewportView(tableWysylki);
		/* Ustaw model tabeli wysyłki */
		DefaultTableModel model_wysylki = new DefaultTableModel(data_wysylki,
				InformatorBaza.kolumny_wysylki);
		tableWysylki.setModel(model_wysylki);

		JScrollPane scrollPaneSprzedaz = new JScrollPane();
		tabbedPane.addTab("Sprzedaż", null, scrollPaneSprzedaz, null);

		tableSprzedaz = new JTable();
		scrollPaneSprzedaz.setViewportView(tableSprzedaz);
		/* Ustaw model tabeli sprzedaż */
		DefaultTableModel model_sprzedaz = new DefaultTableModel(data_sprzedaz,
				InformatorBaza.kolumny_sprzedaz);
		tableSprzedaz.setModel(model_sprzedaz);
	}

	public static void init(String nazwa, String[][] data_sprzedaz,
			String[][] data_wysylki)
	{
		InformatorOkno informator = new InformatorOkno(nazwa, data_sprzedaz,
				data_wysylki);
		informator.setVisible(true);
	}
}
