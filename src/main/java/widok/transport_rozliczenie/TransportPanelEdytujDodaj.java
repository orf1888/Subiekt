package widok.transport_rozliczenie;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import kontroler.WalutaManager;
import model.ObiektZId;
import model.TransportRozliczenie;
import utils.MojeUtils;
import utils.UserShowException;
import widok.abstrakt.PanelEdytujDodajObiekt;

public class TransportPanelEdytujDodaj extends PanelEdytujDodajObiekt
{
	private static final long serialVersionUID = 44838357144894418L;
	private final JTextField textWartosc;
	private final JComboBox<Object> comboWaluta;
	private final JTextField textNrOryginaluFaktury;
	private final JLabel lblNumerOryginauFaktury;
	private Date data_rachunku;
	private int id_fakt;

	public TransportPanelEdytujDodaj() {
		setBorder(new TitledBorder(null, "Edytuj rachunek transportowy",
				TitledBorder.CENTER, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]
		{ 0, 0, 0 };
		gridBagLayout.rowHeights = new int[]
		{ 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[]
		{ 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[]
		{ 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblWarto = new JLabel("Wartość:");
		GridBagConstraints gbc_lblWarto = new GridBagConstraints();
		gbc_lblWarto.anchor = GridBagConstraints.WEST;
		gbc_lblWarto.insets = new Insets(0, 0, 5, 5);
		gbc_lblWarto.gridx = 0;
		gbc_lblWarto.gridy = 0;
		add(lblWarto, gbc_lblWarto);

		textWartosc = new JTextField();
		GridBagConstraints gbc_textWartosc = new GridBagConstraints();
		gbc_textWartosc.insets = new Insets(0, 0, 5, 0);
		gbc_textWartosc.fill = GridBagConstraints.HORIZONTAL;
		gbc_textWartosc.gridx = 1;
		gbc_textWartosc.gridy = 0;
		add(textWartosc, gbc_textWartosc);
		textWartosc.setColumns(10);

		JLabel lblWaluta = new JLabel("Waluta:");
		GridBagConstraints gbc_lblWaluta = new GridBagConstraints();
		gbc_lblWaluta.anchor = GridBagConstraints.WEST;
		gbc_lblWaluta.insets = new Insets(0, 0, 5, 5);
		gbc_lblWaluta.gridx = 0;
		gbc_lblWaluta.gridy = 1;
		add(lblWaluta, gbc_lblWaluta);

		comboWaluta = new JComboBox<Object>();
		GridBagConstraints gbc_comboWaluta = new GridBagConstraints();
		gbc_comboWaluta.insets = new Insets(0, 0, 5, 0);
		gbc_comboWaluta.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboWaluta.gridx = 1;
		gbc_comboWaluta.gridy = 1;
		add(comboWaluta, gbc_comboWaluta);

		lblNumerOryginauFaktury = new JLabel("Numer oryginału faktury:");
		GridBagConstraints gbc_lblNumerOryginauFaktury = new GridBagConstraints();
		gbc_lblNumerOryginauFaktury.insets = new Insets(0, 0, 0, 5);
		gbc_lblNumerOryginauFaktury.anchor = GridBagConstraints.WEST;
		gbc_lblNumerOryginauFaktury.gridx = 0;
		gbc_lblNumerOryginauFaktury.gridy = 2;
		add(lblNumerOryginauFaktury, gbc_lblNumerOryginauFaktury);

		textNrOryginaluFaktury = new JTextField();
		GridBagConstraints gbc_textNrOryginaluFaktury = new GridBagConstraints();
		gbc_textNrOryginaluFaktury.fill = GridBagConstraints.HORIZONTAL;
		gbc_textNrOryginaluFaktury.gridx = 1;
		gbc_textNrOryginaluFaktury.gridy = 2;
		add(textNrOryginaluFaktury, gbc_textNrOryginaluFaktury);
		textNrOryginaluFaktury.setColumns(10);

	}

	@Override
	public void uzupelnijFormatke(Object obiekt, JDialog parent)
	{
		/* init comboWaluta */
		try
		{
			comboWaluta.setModel(MojeUtils
					.odswiezDaneWCombo(true, WalutaManager
							.pobierzWszystkieZBazy("slownikWaluta", false)));
			/* Usuń USD, UAH */
			comboWaluta.removeItem("UAH");
			comboWaluta.removeItem("USD");
		} catch (SQLException e)
		{
			MojeUtils.showError("Błąd inicjalizacji waluty!");
		}
		/* uzupełnij formatkę */
		TransportRozliczenie r_transportowy = (TransportRozliczenie) obiekt;
		if (r_transportowy.wartosc > 0)
			textWartosc.setText(MojeUtils
					.utworzWartoscZlotowki(r_transportowy.wartosc));
		else
			textWartosc.setText("" + 0);
		try
		{
			comboWaluta.setSelectedIndex(r_transportowy.waluta.intValue());
		} catch (Exception e)
		{
			comboWaluta.setSelectedIndex(0);
		}
		if (r_transportowy.nr_faktury != null)
			textNrOryginaluFaktury.setText(r_transportowy.nr_faktury);
		else
			textNrOryginaluFaktury.setText("");
		/* pobierz datę rachunku */
		if (r_transportowy.data_ksiegowania != null)
		{
			data_rachunku = r_transportowy.data_ksiegowania;
		} else
		{
			data_rachunku = new Date();
		}
		id_fakt = r_transportowy.id_faktury_odpowiadajacej;
	}

	@Override
	public ObiektZId pobierzZFormatki(int id) throws Exception
	{
		int wartosc = MojeUtils.utworzWartoscGrosze(textWartosc.getText(),
				"Wartość");
		String numer_faktury = textNrOryginaluFaktury.getText();
		Long waluta = new Long(comboWaluta.getSelectedIndex());
		if (waluta <= 0)
			throw new UserShowException(
					"Nie wybrano waluty rachunku transprtowego!");
		return new TransportRozliczenie(id, data_rachunku, numer_faktury,
				wartosc, waluta, id_fakt);
	}

	/* narazie nie używana */
	@Override
	public void czysc()
	{
		// TODO Auto-generated method stub

	}

}
