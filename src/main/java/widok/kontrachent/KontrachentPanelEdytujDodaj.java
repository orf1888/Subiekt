package widok.kontrachent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import model.Kontrachent;
import model.ObiektZId;
import utils.MojeUtils;
import utils.UserShowException;
import utils.Walidator;
import widok.abstrakt.PanelEdytujDodajObiekt;

public class KontrachentPanelEdytujDodaj extends PanelEdytujDodajObiekt
{

	private static final long serialVersionUID = -4462107133750827904L;

	private final JTextField textNazwa;

	private final JTextField textUlica;

	private final JTextField textMiasto;

	private final JTextField textKodPocztowy;

	private final JTextField textNIP;

	private final JTextField textRegon;

	private final JTextField textNrKonta;

	private final JTextField textNarzut;

	private final JTextField textKredytKupiecki;

	/**
	 * @param tytul
	 *            - "Dodaj" lub "Edytuj"
	 * @param editable
	 */
	public KontrachentPanelEdytujDodaj(String tytul, boolean editable) {
		setBorder(new TitledBorder(null, tytul + " kontrachenta",
				TitledBorder.CENTER, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]
		{ 0, 0, 0 };
		gridBagLayout.rowHeights = new int[]
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[]
		{ 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[]
		{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
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
		textNazwa.setEditable(editable);

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
		textUlica.setEditable(editable);

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
		textMiasto.setEditable(editable);

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
		textKodPocztowy.setEditable(editable);

		JLabel lblNip = new JLabel("NIP:");
		GridBagConstraints gbc_lblNip = new GridBagConstraints();
		gbc_lblNip.anchor = GridBagConstraints.WEST;
		gbc_lblNip.insets = new Insets(0, 0, 5, 5);
		gbc_lblNip.gridx = 0;
		gbc_lblNip.gridy = 4;
		add(lblNip, gbc_lblNip);

		textNIP = new JTextField();
		GridBagConstraints gbc_textNIP = new GridBagConstraints();
		gbc_textNIP.insets = new Insets(0, 0, 5, 0);
		gbc_textNIP.fill = GridBagConstraints.HORIZONTAL;
		gbc_textNIP.gridx = 1;
		gbc_textNIP.gridy = 4;
		add(textNIP, gbc_textNIP);
		textNIP.setColumns(10);
		textNIP.setEditable(editable);

		JLabel lblRegon = new JLabel("Regon:");
		GridBagConstraints gbc_lblRegon = new GridBagConstraints();
		gbc_lblRegon.anchor = GridBagConstraints.WEST;
		gbc_lblRegon.insets = new Insets(0, 0, 5, 5);
		gbc_lblRegon.gridx = 0;
		gbc_lblRegon.gridy = 5;
		add(lblRegon, gbc_lblRegon);

		textRegon = new JTextField();
		GridBagConstraints gbc_textRegon = new GridBagConstraints();
		gbc_textRegon.insets = new Insets(0, 0, 5, 0);
		gbc_textRegon.fill = GridBagConstraints.HORIZONTAL;
		gbc_textRegon.gridx = 1;
		gbc_textRegon.gridy = 5;
		add(textRegon, gbc_textRegon);
		textRegon.setColumns(10);
		textRegon.setEditable(editable);

		JLabel lblNrKonta = new JLabel("Nr. konta:");
		GridBagConstraints gbc_lblNrKonta = new GridBagConstraints();
		gbc_lblNrKonta.anchor = GridBagConstraints.WEST;
		gbc_lblNrKonta.insets = new Insets(0, 0, 5, 5);
		gbc_lblNrKonta.gridx = 0;
		gbc_lblNrKonta.gridy = 6;
		add(lblNrKonta, gbc_lblNrKonta);

		textNrKonta = new JTextField();
		GridBagConstraints gbc_textNrKonta = new GridBagConstraints();
		gbc_textNrKonta.insets = new Insets(0, 0, 5, 0);
		gbc_textNrKonta.fill = GridBagConstraints.HORIZONTAL;
		gbc_textNrKonta.gridx = 1;
		gbc_textNrKonta.gridy = 6;
		add(textNrKonta, gbc_textNrKonta);
		textNrKonta.setColumns(10);
		textNrKonta.setEditable(editable);

		JLabel lblNarzut = new JLabel("Narzut [%]");
		GridBagConstraints gbc_lblNarzut = new GridBagConstraints();
		gbc_lblNarzut.anchor = GridBagConstraints.WEST;
		gbc_lblNarzut.insets = new Insets(0, 0, 5, 5);
		gbc_lblNarzut.gridx = 0;
		gbc_lblNarzut.gridy = 7;
		add(lblNarzut, gbc_lblNarzut);

		textNarzut = new JTextField();
		GridBagConstraints gbc_textMnoznik = new GridBagConstraints();
		gbc_textMnoznik.insets = new Insets(0, 0, 5, 0);
		gbc_textMnoznik.fill = GridBagConstraints.HORIZONTAL;
		gbc_textMnoznik.gridx = 1;
		gbc_textMnoznik.gridy = 7;
		add(textNarzut, gbc_textMnoznik);
		textNarzut.setColumns(10);
		textNarzut.setEditable(editable);

		JLabel lblRabatKupiecki = new JLabel("Kredyt kupiecki:");
		GridBagConstraints gbc_lblRabatKupiecki = new GridBagConstraints();
		gbc_lblRabatKupiecki.anchor = GridBagConstraints.EAST;
		gbc_lblRabatKupiecki.insets = new Insets(0, 0, 0, 5);
		gbc_lblRabatKupiecki.gridx = 0;
		gbc_lblRabatKupiecki.gridy = 8;
		add(lblRabatKupiecki, gbc_lblRabatKupiecki);

		textKredytKupiecki = new JTextField();
		GridBagConstraints gbc_textRabatKupiecki = new GridBagConstraints();
		gbc_textRabatKupiecki.fill = GridBagConstraints.HORIZONTAL;
		gbc_textRabatKupiecki.gridx = 1;
		gbc_textRabatKupiecki.gridy = 8;
		add(textKredytKupiecki, gbc_textRabatKupiecki);
		textKredytKupiecki.setColumns(10);
		textKredytKupiecki.setEditable(editable);

	}

	@Override
	public void uzupelnijFormatke(Object obiekt, JDialog parent)
	{
		Kontrachent kontrachent = (Kontrachent) obiekt;
		textNazwa.setText(kontrachent.nazwa);
		textUlica.setText(kontrachent.ulica);
		textMiasto.setText(kontrachent.miasto);
		textKodPocztowy.setText(kontrachent.kod_pocztowy);
		textNIP.setText("" + kontrachent.nip);
		textRegon.setText("" + kontrachent.regon);
		textNrKonta.setText("" + kontrachent.nrKonta);
		textNarzut.setText("" + kontrachent._cena_mnoznik);
		textKredytKupiecki.setText(MojeUtils
				.formatujWartosc(kontrachent.kredyt_kupiecki));
	}

	/**
	 * pobiera dane z formatki i tworzy z nich nowego kontrachenta. Musimy
	 * zapewnic "id", aby obiekt byl kompletny
	 * 
	 * @param id
	 *            - cache'owane gdzies
	 * @return
	 * @throws Exception
	 */
	@Override
	public ObiektZId pobierzZFormatki(int id) throws Exception
	{
		/* Walidacja */

		/* narzut */
		int narzut = Walidator.parsujInteger(textNarzut.getText(), "Narzut");

		/* kredyt kupiecki */
		int kredytKupiecki = MojeUtils.utworzWartoscGrosze(
				textKredytKupiecki.getText(), "Kredyt kupiecki");

		if (kredytKupiecki < 1000 * 100)
			throw new UserShowException(
					"Podano niepoprawny kredyt kupiecki (minimum 1000zł)");
		/* nip */
		String nip = Walidator.walidujNIP(textNIP.getText());
		/* regon */
		String regon = Walidator.walidujRegon(textRegon.getText());

		/* numer konta bankowego */
		Walidator.walidujNrKonta(textNrKonta.getText());
		/* kod pocztowy */
		Walidator.walidujKodPocztowy(textKodPocztowy.getText());
		return new Kontrachent(textNazwa.getText(), textMiasto.getText(),
				textKodPocztowy.getText(), textUlica.getText(), narzut, true,
				nip, regon, textNrKonta.getText(), kredytKupiecki, id,
				/* długi walutowe */null, null, null, null,
				/* środki nierozliczone */null, null, null, null);
	}

	@Override
	public void czysc()
	{
		textNazwa.setText("");
		textUlica.setText("");
		textMiasto.setText("");
		textKodPocztowy.setText("");
		textNIP.setText("");
		textRegon.setText("");
		textNrKonta.setText("");
		textNarzut.setText("");
		textKredytKupiecki.setText("");
	}
}