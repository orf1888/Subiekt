package widok;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import model.ObiektZId;
import widok.abstrakt.PanelEdytujDodajObiekt;

public class ProducentPanelEdytujDodaj
	extends PanelEdytujDodajObiekt
{

	private static final long serialVersionUID = -6198527462638994092L;

	static JTextField textNazwaProducenta;

	public ProducentPanelEdytujDodaj()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 94, 86, 0 };
		gridBagLayout.rowHeights = new int[] { 20, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		setLayout( gridBagLayout );

		JLabel lblNazwaProducenta = new JLabel( "Nazwa producenta:" );
		GridBagConstraints gbc_lblNazwaProducenta = new GridBagConstraints();
		gbc_lblNazwaProducenta.insets = new Insets( 0, 0, 0, 5 );
		gbc_lblNazwaProducenta.gridx = 0;
		gbc_lblNazwaProducenta.gridy = 0;
		add( lblNazwaProducenta, gbc_lblNazwaProducenta );

		textNazwaProducenta = new JTextField();
		GridBagConstraints gbc_textNazwaProducenta = new GridBagConstraints();
		gbc_textNazwaProducenta.fill = GridBagConstraints.HORIZONTAL;
		gbc_textNazwaProducenta.gridx = 1;
		gbc_textNazwaProducenta.gridy = 0;
		add( textNazwaProducenta, gbc_textNazwaProducenta );
		textNazwaProducenta.setColumns( 10 );

	}

	public static void uzupelnijProducenta( String producent )
	{
		textNazwaProducenta.setText( producent );

	}

	@Override
	public ObiektZId pobierzZFormatki( int id ) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void uzupelnijFormatke( Object obiekt, JDialog parent )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void czysc()
	{
		textNazwaProducenta.setText( "" );
	}
}
