package widok.wplata;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import kontroler.KontrachentBaza;
import kontroler.WalutaManager;
import model.Kontrachent;
import model.ObiektZId;
import model.Waluta;
import model.Wplata;
import utils.MojeUtils;
import utils.UserShowException;
import widok.abstrakt.PanelEdytujDodajObiekt;

public class WplataPanelEdytujDodaj extends PanelEdytujDodajObiekt
{
	private static final long serialVersionUID = -119688707719525474L;

	private final JTextField textWartoscWplaty;
	private final JComboBox<Object> comboKontrachent;
	private final JComboBox<Object> comboWaluta;

	public WplataPanelEdytujDodaj(String tytul, boolean editable) {
		setBorder(new TitledBorder(null, tytul + " wpłatę",
				TitledBorder.CENTER, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]
		{ 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[]
		{ 0, 0, 0 };
		gridBagLayout.columnWeights = new double[]
		{ 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[]
		{ 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblWartoPaty = new JLabel("Warto\u015B\u0107 p\u0142aty");
		GridBagConstraints gbc_lblWartoPaty = new GridBagConstraints();
		gbc_lblWartoPaty.fill = GridBagConstraints.VERTICAL;
		gbc_lblWartoPaty.insets = new Insets(0, 0, 5, 5);
		gbc_lblWartoPaty.gridx = 0;
		gbc_lblWartoPaty.gridy = 0;
		add(lblWartoPaty, gbc_lblWartoPaty);

		JLabel lblKontrachent = new JLabel("Kontrachent");
		GridBagConstraints gbc_lblKontrachent = new GridBagConstraints();
		gbc_lblKontrachent.fill = GridBagConstraints.VERTICAL;
		gbc_lblKontrachent.insets = new Insets(0, 0, 5, 5);
		gbc_lblKontrachent.gridx = 1;
		gbc_lblKontrachent.gridy = 0;
		add(lblKontrachent, gbc_lblKontrachent);

		JLabel lblWaluta = new JLabel("Waluta");
		GridBagConstraints gbc_lblWaluta = new GridBagConstraints();
		gbc_lblWaluta.fill = GridBagConstraints.VERTICAL;
		gbc_lblWaluta.insets = new Insets(0, 0, 5, 0);
		gbc_lblWaluta.gridx = 2;
		gbc_lblWaluta.gridy = 0;
		add(lblWaluta, gbc_lblWaluta);

		textWartoscWplaty = new JTextField();
		GridBagConstraints gbc_textWartoscPlaty = new GridBagConstraints();
		gbc_textWartoscPlaty.insets = new Insets(0, 0, 0, 5);
		gbc_textWartoscPlaty.fill = GridBagConstraints.BOTH;
		gbc_textWartoscPlaty.gridx = 0;
		gbc_textWartoscPlaty.gridy = 1;
		add(textWartoscWplaty, gbc_textWartoscPlaty);
		textWartoscWplaty.setColumns(5);

		comboKontrachent = new JComboBox<Object>();
		GridBagConstraints gbc_comboKontrachent = new GridBagConstraints();
		gbc_comboKontrachent.insets = new Insets(0, 0, 0, 5);
		gbc_comboKontrachent.fill = GridBagConstraints.BOTH;
		gbc_comboKontrachent.gridx = 1;
		gbc_comboKontrachent.gridy = 1;
		add(comboKontrachent, gbc_comboKontrachent);

		String[] waluta = WalutaManager.pobierzWszystkieZBazy(
				Waluta.tabelaWaluta, true);
		comboWaluta = new JComboBox<Object>(waluta);
		GridBagConstraints gbc_comboWaluta = new GridBagConstraints();
		gbc_comboWaluta.fill = GridBagConstraints.BOTH;
		gbc_comboWaluta.gridx = 2;
		gbc_comboWaluta.gridy = 1;
		add(comboWaluta, gbc_comboWaluta);
	}

	@Override
	public void uzupelnijFormatke(Object obiekt, JDialog parent)
	{
		/* rzuca nullem oraz dokończyć */
		Wplata wplata = (Wplata) obiekt;
		String nazwa_kontrachenta = null;
		try
		{
			init(false);
			nazwa_kontrachenta = KontrachentBaza
					.pobierzNazweZBazy(wplata.id_kontrachent);
		} catch (Exception e)
		{
			MojeUtils.showError("Błąd pobierania wpłaty!");
			MojeUtils.error(e);
		}
		textWartoscWplaty.setText(MojeUtils
				.utworzWartoscZlotowki(wplata.wartosc));
		comboKontrachent.setSelectedItem(nazwa_kontrachenta);
		comboWaluta.setSelectedIndex(wplata.waluta - 1);
	}

	@Override
	public ObiektZId pobierzZFormatki(int id) throws Exception
	{
		int wartosc = MojeUtils.utworzWartoscGrosze(
				textWartoscWplaty.getText(), "Wartość wpłaty");

		if (wartosc <= 0)
			throw new UserShowException(
					"Wartość wpłaty musi być większa niż 0!");

		Kontrachent tmp_kontrachent = (Kontrachent) KontrachentBaza
				.pobierzObiektZBazy(comboKontrachent.getSelectedItem()
						.toString());
		if (tmp_kontrachent == null)
			throw new UserShowException("Nie wybrano kontrachenta!");
		int id_kontrachent = tmp_kontrachent.id_kontrachent;
		int id_waluta = comboWaluta.getSelectedIndex() + 1;
		if (id_waluta < 0)
			throw new UserShowException("Nie wybrano waluty!");
		return new Wplata(id, new Date(), id_kontrachent, wartosc, id_waluta);
	}

	@Override
	public void czysc()
	{
		try
		{
			init(true);
		} catch (Exception e)
		{
			MojeUtils.error(e);;
		}
		comboKontrachent.setSelectedIndex(0);
		comboWaluta.setSelectedIndex(0);
		textWartoscWplaty.setText("");
	}

	protected void init(boolean z_pustym_elementem) throws Exception
	{
		comboKontrachent.setModel(MojeUtils.odswiezDaneWCombo(
				z_pustym_elementem,
				KontrachentBaza.pobierzWszystkieNazwyZBazy()));
	}
}
