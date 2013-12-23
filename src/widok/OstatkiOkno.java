package widok;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import utils.SprawdzMagazyn;

public class OstatkiOkno extends JDialog
{

	private static final long serialVersionUID = 7519881534302364256L;
	private final JPanel contentPane;
	private final JTable tableOstatki;

	public OstatkiOkno(String[][] data) {
		setAlwaysOnTop(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Sprawdź ostatki");
		setSize(750, 600);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		DefaultTableModel model_ostatki = new DefaultTableModel(data,
				SprawdzMagazyn.kolumny);
		tableOstatki = new JTable();
		tableOstatki.setModel(model_ostatki);
		scrollPane.setViewportView(tableOstatki);
	}

}
