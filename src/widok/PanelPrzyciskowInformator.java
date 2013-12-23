package widok;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.nachocalendar.components.DatePanel;

public class PanelPrzyciskowInformator extends JPanel
{
	private static final long serialVersionUID = 978002880294496246L;
	private final JLabel lblDataOd;
	public final DatePanel dataOd;
	private final JLabel lblDataDo;
	public final DatePanel dataDo;
	private final JButton btnPoka;

	public PanelPrzyciskowInformator(ActionListener listener) {
		GridBagLayout gbl_panelPrzyciskow = new GridBagLayout();
		gbl_panelPrzyciskow.columnWidths = new int[]
		{ 0, 0, 0, 0, 0, 0 };
		gbl_panelPrzyciskow.rowHeights = new int[]
		{ 0, 0 };
		gbl_panelPrzyciskow.columnWeights = new double[]
		{ 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelPrzyciskow.rowWeights = new double[]
		{ 0.0, Double.MIN_VALUE };
		setLayout(gbl_panelPrzyciskow);

		lblDataOd = new JLabel("Data od: ");
		GridBagConstraints gbc_lblDataOd = new GridBagConstraints();
		gbc_lblDataOd.insets = new Insets(0, 0, 0, 5);
		gbc_lblDataOd.gridx = 0;
		gbc_lblDataOd.gridy = 0;
		add(lblDataOd, gbc_lblDataOd);

		dataOd = new DatePanel();
		GridBagConstraints gbc_dataOd = new GridBagConstraints();
		gbc_dataOd.insets = new Insets(0, 0, 0, 5);
		gbc_dataOd.gridx = 1;
		gbc_dataOd.gridy = 0;
		add(dataOd, gbc_dataOd);

		lblDataDo = new JLabel("Data do: ");
		GridBagConstraints gbc_lblDataDo = new GridBagConstraints();
		gbc_lblDataDo.insets = new Insets(0, 0, 0, 5);
		gbc_lblDataDo.gridx = 2;
		gbc_lblDataDo.gridy = 0;
		add(lblDataDo, gbc_lblDataDo);

		dataDo = new DatePanel();
		GridBagConstraints gbc_dataDo = new GridBagConstraints();
		gbc_dataDo.insets = new Insets(0, 0, 0, 5);
		gbc_dataDo.gridx = 3;
		gbc_dataDo.gridy = 0;
		add(dataDo, gbc_dataDo);

		btnPoka = new JButton("Pokaż");
		GridBagConstraints gbc_btnPoka = new GridBagConstraints();
		gbc_btnPoka.anchor = GridBagConstraints.NORTH;
		gbc_btnPoka.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPoka.gridx = 4;
		gbc_btnPoka.gridy = 0;
		add(btnPoka, gbc_btnPoka);
		btnPoka.addActionListener(listener);
	}
}
