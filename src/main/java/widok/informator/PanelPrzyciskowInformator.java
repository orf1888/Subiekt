package widok.informator;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
	private JCheckBox chx;

	/**
	 * 
	 * @param listener
	 *            - ActionListaener odpowiadający za akcje przycisku "Pokaż".
	 * @param isPoziomaOrientacja
	 *            - boolaen odpowiadający za orientacje ulożenia przycików.
	 * @param isExtraWidgets
	 *            - boolean odpowiadający za dodanie widgetów parametryzacji.
	 */
	public PanelPrzyciskowInformator(ActionListener listener,
			boolean isPoziomaOrientacja, boolean isExtraWidgets) {

		GridBagLayout gbl_panelPrzyciskow = new GridBagLayout();

		if (isPoziomaOrientacja)
		{
			gbl_panelPrzyciskow.columnWidths = new int[]
			{ 0, 0, 0, 0, 0, 0 };
			gbl_panelPrzyciskow.rowHeights = new int[]
			{ 0, 0 };
			gbl_panelPrzyciskow.columnWeights = new double[]
			{ 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			gbl_panelPrzyciskow.rowWeights = new double[]
			{ 0.0, Double.MIN_VALUE };
		} else
		{

			gbl_panelPrzyciskow.columnWidths = new int[]
			{ 0, 0 };
			gbl_panelPrzyciskow.rowHeights = new int[]
			{ 0, 0, 0, 0, 0, 0 };
			gbl_panelPrzyciskow.columnWeights = new double[]
			{ 0.0, Double.MIN_VALUE };
			gbl_panelPrzyciskow.rowWeights = new double[]
			{ 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };

		}
		setLayout(gbl_panelPrzyciskow);

		lblDataOd = new JLabel("Data od: ");
		add(lblDataOd, tworzGBC(isPoziomaOrientacja, 0, 0));

		dataOd = new DatePanel();
		add(dataOd, tworzGBC(isPoziomaOrientacja, 1, 0));

		lblDataDo = new JLabel("Data do: ");
		add(lblDataDo, tworzGBC(isPoziomaOrientacja, 2, 0));

		dataDo = new DatePanel();
		add(dataDo, tworzGBC(isPoziomaOrientacja, 3, 0));

		if (isExtraWidgets)
		{

			chx = new JCheckBox("Uwzględniaj tylko faktury niezapłacone");
			add(chx, tworzGBC(isPoziomaOrientacja, 4, 0));

			btnPoka = new JButton("Pokaż");
			GridBagConstraints gbc_btn = tworzGBC(isPoziomaOrientacja, 5, 0);
			gbc_btn.anchor = GridBagConstraints.NORTH;
			gbc_btn.fill = GridBagConstraints.HORIZONTAL;
			add(btnPoka, gbc_btn);
			btnPoka.addActionListener(listener);
		} else
		{
			btnPoka = new JButton("Pokaż");
			GridBagConstraints gbc_btn = tworzGBC(isPoziomaOrientacja, 4, 0);
			gbc_btn.anchor = GridBagConstraints.NORTH;
			gbc_btn.fill = GridBagConstraints.HORIZONTAL;
			add(btnPoka, gbc_btn);
			btnPoka.addActionListener(listener);
		}
	}

	/**
	 * Metoda zwraca GBC. Jesli orientacja jest pionowa zamieniane sa gridx i
	 * gridy.
	 * 
	 * @param isPoziomaOrientacja
	 * @param gridx
	 * @param gridy
	 * @return
	 */
	private GridBagConstraints tworzGBC(boolean isPoziomaOrientacja, int gridx,
			int gridy)
	{
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 0, 5);
		if (isPoziomaOrientacja)
		{
			gbc.gridx = gridx;
			gbc.gridy = gridy;
		} else
		{
			gbc.gridx = gridy;
			gbc.gridy = gridx;
			gbc.fill = GridBagConstraints.HORIZONTAL;
		}
		return gbc;
	}

	public boolean isChxSelected()
	{
		return chx.isSelected();
	}
}
