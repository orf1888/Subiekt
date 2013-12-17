package widok;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import kontroler.PodmiotBaza;
import model.Podmiot;
import utils.UserShowException;
import utils.Walidator;

public class PodmiotPanel extends JPanel
{

	private static final long serialVersionUID = -4462107133750827904L;

	private final JTextField textNazwa;

	private final JTextField textUlica;

	private final JTextField textMiasto;

	private final JTextField textKodPocztowy;

	private final JTextField textNIP;

	public PodmiotPanel() throws SQLException {
		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				"Dane podmiotu", TitledBorder.CENTER, TitledBorder.TOP, null,
				null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]
		{ 0, 0, 0 };
		gridBagLayout.rowHeights = new int[]
		{ 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[]
		{ 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[]
		{ 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblNazwa = new JLabel("Nazwa:");
		GridBagConstraints gbc_lblNazwa = new GridBagConstraints();
		gbc_lblNazwa.anchor = GridBagConstraints.WEST;
		gbc_lblNazwa.insets = new Insets(0, 0, 5, 5);
		gbc_lblNazwa.gridx = 0;
		gbc_lblNazwa.gridy = 0;
		add(lblNazwa, gbc_lblNazwa);

		textNazwa = new JTextField();
		GridBagConstraints gbc_textNazwa = new GridBagConstraints();
		gbc_textNazwa.insets = new Insets(0, 0, 5, 0);
		gbc_textNazwa.fill = GridBagConstraints.HORIZONTAL;
		gbc_textNazwa.gridx = 1;
		gbc_textNazwa.gridy = 0;
		add(textNazwa, gbc_textNazwa);
		textNazwa.setColumns(10);

		JLabel lblUlica = new JLabel("Ulica:");
		GridBagConstraints gbc_lblUlica = new GridBagConstraints();
		gbc_lblUlica.anchor = GridBagConstraints.WEST;
		gbc_lblUlica.insets = new Insets(0, 0, 5, 5);
		gbc_lblUlica.gridx = 0;
		gbc_lblUlica.gridy = 1;
		add(lblUlica, gbc_lblUlica);

		textUlica = new JTextField();
		GridBagConstraints gbc_textUlica = new GridBagConstraints();
		gbc_textUlica.insets = new Insets(0, 0, 5, 0);
		gbc_textUlica.fill = GridBagConstraints.HORIZONTAL;
		gbc_textUlica.gridx = 1;
		gbc_textUlica.gridy = 1;
		add(textUlica, gbc_textUlica);
		textUlica.setColumns(10);

		JLabel lblMiasto = new JLabel("Miasto:");
		GridBagConstraints gbc_lblMiasto = new GridBagConstraints();
		gbc_lblMiasto.anchor = GridBagConstraints.WEST;
		gbc_lblMiasto.insets = new Insets(0, 0, 5, 5);
		gbc_lblMiasto.gridx = 0;
		gbc_lblMiasto.gridy = 2;
		add(lblMiasto, gbc_lblMiasto);

		textMiasto = new JTextField();
		GridBagConstraints gbc_textMiasto = new GridBagConstraints();
		gbc_textMiasto.insets = new Insets(0, 0, 5, 0);
		gbc_textMiasto.fill = GridBagConstraints.HORIZONTAL;
		gbc_textMiasto.gridx = 1;
		gbc_textMiasto.gridy = 2;
		add(textMiasto, gbc_textMiasto);
		textMiasto.setColumns(10);

		JLabel lblKodPocztowy = new JLabel("Kod pocztowy:");
		GridBagConstraints gbc_lblKodPocztowy = new GridBagConstraints();
		gbc_lblKodPocztowy.anchor = GridBagConstraints.WEST;
		gbc_lblKodPocztowy.insets = new Insets(0, 0, 5, 5);
		gbc_lblKodPocztowy.gridx = 0;
		gbc_lblKodPocztowy.gridy = 3;
		add(lblKodPocztowy, gbc_lblKodPocztowy);

		textKodPocztowy = new JTextField();
		GridBagConstraints gbc_textKodPocztowy = new GridBagConstraints();
		gbc_textKodPocztowy.insets = new Insets(0, 0, 5, 0);
		gbc_textKodPocztowy.fill = GridBagConstraints.HORIZONTAL;
		gbc_textKodPocztowy.gridx = 1;
		gbc_textKodPocztowy.gridy = 3;
		add(textKodPocztowy, gbc_textKodPocztowy);
		textKodPocztowy.setColumns(10);

		JLabel lblNip = new JLabel("NIP:");
		GridBagConstraints gbc_lblNip = new GridBagConstraints();
		gbc_lblNip.anchor = GridBagConstraints.WEST;
		gbc_lblNip.insets = new Insets(0, 0, 0, 5);
		gbc_lblNip.gridx = 0;
		gbc_lblNip.gridy = 4;
		add(lblNip, gbc_lblNip);

		textNIP = new JTextField();
		GridBagConstraints gbc_textNIP = new GridBagConstraints();
		gbc_textNIP.fill = GridBagConstraints.HORIZONTAL;
		gbc_textNIP.gridx = 1;
		gbc_textNIP.gridy = 4;
		add(textNIP, gbc_textNIP);
		textNIP.setColumns(10);
		uzupelnijFormatke();
	}

	public void uzupelnijFormatke() throws SQLException
	{
		Podmiot podmiot = PodmiotBaza.pobierzPodmiot();
		textNazwa.setText(podmiot.nazwa);
		textUlica.setText(podmiot.ulica);
		textMiasto.setText(podmiot.miasto);
		textKodPocztowy.setText(podmiot.kod_pocztowy);
		textNIP.setText(podmiot.nip);
	}

	public Podmiot pobierzZFormatki() throws Exception
	{
		/* walidacja pól */
		if (textNazwa.getText().trim().isEmpty())
			throw new UserShowException("Nazwa podmiotu nie może być pusta!");
		if (textUlica.getText().trim().isEmpty())
			throw new UserShowException("Ulica nie może być pusta!");
		Walidator.walidujKodPocztowy(textKodPocztowy.getText());
		if (textMiasto.getText().trim().isEmpty())
			throw new UserShowException("Miasto niee może być puste!");
		Walidator.walidujNIP(textNIP.getText());

		return new Podmiot(textNazwa.getText(), textUlica.getText(),
				textKodPocztowy.getText(), textMiasto.getText(),
				textNIP.getText());
	}
}