package widok;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import kontroler.SlownikManager;
import model.ObiektZId;
import model.SlownikElement;
import model.produkt.Produkt;
import utils.MojeUtils;
import utils.UserShowException;
import utils.Walidator;
import widok.abstrakt.DialogEdytujDodaj;
import widok.abstrakt.PanelEdytujDodajObiekt;

public class ProduktPanelEdytujDodaj
	extends PanelEdytujDodajObiekt
{
	private static final long serialVersionUID = 1L;

	private final JTextField textKod;

	private final JTextField textNazwa;

	private final JLabel lblProducent;

	private final JComboBox<String> comboProducent;

	private final JLabel lblDomylnaCenaZakupu;

	private final JTextField textDomyslnaCenaZakupu;

	private final JLabel lblIlo;

	private final JTextField textIlosc;

	private final JPanel panel;

	private final JButton btnDodaj;

	private final JButton btnEdytuj;

	protected static DialogEdytujDodaj oknoModalne;

	/**
	 * Create the panel.
	 */
	public ProduktPanelEdytujDodaj( String tytul, boolean editable )
	{
		setBorder( new TitledBorder( null, tytul + " produkt", TitledBorder.CENTER, TitledBorder.TOP,
				null, null ) );
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		setLayout( gridBagLayout );

		JLabel lblKod = new JLabel( "Kod:" );
		GridBagConstraints gbc_lblKod = new GridBagConstraints();
		gbc_lblKod.anchor = GridBagConstraints.WEST;
		gbc_lblKod.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblKod.gridx = 0;
		gbc_lblKod.gridy = 0;
		add( lblKod, gbc_lblKod );

		textKod = new JTextField();
		GridBagConstraints gbc_textKod = new GridBagConstraints();
		gbc_textKod.insets = new Insets( 0, 0, 5, 0 );
		gbc_textKod.fill = GridBagConstraints.HORIZONTAL;
		gbc_textKod.gridx = 1;
		gbc_textKod.gridy = 0;
		add( textKod, gbc_textKod );
		textKod.setColumns( 10 );
		textKod.setEditable( editable );

		JLabel lblNazwa = new JLabel( "Nazwa:" );
		GridBagConstraints gbc_lblNazwa = new GridBagConstraints();
		gbc_lblNazwa.anchor = GridBagConstraints.WEST;
		gbc_lblNazwa.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblNazwa.gridx = 0;
		gbc_lblNazwa.gridy = 1;
		add( lblNazwa, gbc_lblNazwa );

		textNazwa = new JTextField();
		GridBagConstraints gbc_textNazwa = new GridBagConstraints();
		gbc_textNazwa.insets = new Insets( 0, 0, 5, 0 );
		gbc_textNazwa.fill = GridBagConstraints.HORIZONTAL;
		gbc_textNazwa.gridx = 1;
		gbc_textNazwa.gridy = 1;
		add( textNazwa, gbc_textNazwa );
		textNazwa.setColumns( 10 );
		textNazwa.setEditable( editable );

		lblProducent = new JLabel( "Producent:" );
		GridBagConstraints gbc_lblProducent = new GridBagConstraints();
		gbc_lblProducent.anchor = GridBagConstraints.WEST;
		gbc_lblProducent.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblProducent.gridx = 0;
		gbc_lblProducent.gridy = 2;
		add( lblProducent, gbc_lblProducent );

		String[] producenci =
			SlownikManager.pobierzWszystkieZBazy( SlownikElement.tabelaProducent, false );
		comboProducent = new JComboBox<String>( producenci );
		GridBagConstraints gbc_comboProducent = new GridBagConstraints();
		gbc_comboProducent.insets = new Insets( 0, 0, 5, 0 );
		gbc_comboProducent.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboProducent.gridx = 1;
		gbc_comboProducent.gridy = 2;
		add( comboProducent, gbc_comboProducent );

		lblDomylnaCenaZakupu = new JLabel( "Domyślna cena zakupu:" );
		GridBagConstraints gbc_lblDomylnaCenaZakupu = new GridBagConstraints();
		gbc_lblDomylnaCenaZakupu.anchor = GridBagConstraints.WEST;
		gbc_lblDomylnaCenaZakupu.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblDomylnaCenaZakupu.gridx = 0;
		gbc_lblDomylnaCenaZakupu.gridy = 3;
		add( lblDomylnaCenaZakupu, gbc_lblDomylnaCenaZakupu );

		textDomyslnaCenaZakupu = new JTextField();
		textDomyslnaCenaZakupu.setEditable( editable );
		GridBagConstraints gbc_textDomyslnaCenaZakupu = new GridBagConstraints();
		gbc_textDomyslnaCenaZakupu.insets = new Insets( 0, 0, 5, 0 );
		gbc_textDomyslnaCenaZakupu.fill = GridBagConstraints.HORIZONTAL;
		gbc_textDomyslnaCenaZakupu.gridx = 1;
		gbc_textDomyslnaCenaZakupu.gridy = 3;
		add( textDomyslnaCenaZakupu, gbc_textDomyslnaCenaZakupu );
		textDomyslnaCenaZakupu.setColumns( 10 );

		lblIlo = new JLabel( "Ilość:" );
		GridBagConstraints gbc_lblIlo = new GridBagConstraints();
		gbc_lblIlo.anchor = GridBagConstraints.WEST;
		gbc_lblIlo.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblIlo.gridx = 0;
		gbc_lblIlo.gridy = 4;
		add( lblIlo, gbc_lblIlo );

		textIlosc = new JTextField();
		textIlosc.setText( "0" );
		textIlosc.setEditable( false );
		GridBagConstraints gbc_textIlosc = new GridBagConstraints();
		gbc_textIlosc.insets = new Insets( 0, 0, 5, 0 );
		gbc_textIlosc.fill = GridBagConstraints.HORIZONTAL;
		gbc_textIlosc.gridx = 1;
		gbc_textIlosc.gridy = 4;
		add( textIlosc, gbc_textIlosc );
		textIlosc.setColumns( 10 );

		panel = new JPanel();
		panel.setBorder( new TitledBorder( null, "Opcje producenta", TitledBorder.CENTER,
				TitledBorder.TOP, null, null ) );
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 5;
		add( panel, gbc_panel );
		panel.setLayout( new FlowLayout( FlowLayout.CENTER, 5, 5 ) );

		btnDodaj = new JButton( "Dodaj" );
		btnDodaj.addActionListener( dodajListener );
		btnDodaj.setAlignmentX( Component.CENTER_ALIGNMENT );
		panel.add( btnDodaj );

		btnEdytuj = new JButton( "Edytuj" );
		btnEdytuj.addActionListener( edytujListener );
		panel.add( btnEdytuj );

	}

	@Override
	public void uzupelnijFormatke( Object obiekt, JDialog parent )
	{
		Produkt produkt = (Produkt) obiekt;
		textKod.setText( produkt.kod );
		textNazwa.setText( produkt.nazwa );
		textDomyslnaCenaZakupu.setText( "" + ( produkt.cena_zakupu / 100 ) + ","
			+ ( produkt.cena_zakupu % 100 ) );
		textIlosc.setText( "" + produkt.ilosc );
		//dafuq
		comboProducent.setSelectedItem( ( (SlownikElement) SlownikManager.pobierzZBazy(
			produkt.id_producenta, SlownikElement.tabelaProducent ) ).wartosc );

	}

	@Override
	public ObiektZId pobierzZFormatki( int id ) throws Exception
	{
		//pobierz id_producenta z nazwy w comboB
		//Pobieramy cenę z formatki, jęsli jest bez "," to *100

		int cenaZakupu = MojeUtils.utworzWartoscGrosze( textDomyslnaCenaZakupu.getText(), "Cena zakupu" );
		/*walidacja liczb z pola cena*/
		//        try {
		//            MojeUtils.utworzWartosc( textDomyslnaCenaZakupu.getText() );
		//        }
		//        catch ( Exception e ) {
		//            throw new Exception( "Podano niepoprawną cenę!(Cena powinna być liczbą!)" );
		//        }
		if ( cenaZakupu <= 0 )
			throw new UserShowException( "Podano niepoprawną cenę!\n(Cena musi być dodatnia!)" );
		/*walidacja wartości z pól: kod, nazwa*/
		if ( textKod.getText().replaceAll( " ", "" ).isEmpty() || textKod.getText().length() < 3 )
			throw new UserShowException( "Kod powinien składać się z conajmniej 3 znaków!" );
		if ( textNazwa.getText().replaceAll( " ", "" ).isEmpty() )
			throw new UserShowException( "Nazwa nie może być pusta!" );

		if ( textIlosc.getText().isEmpty() )
			textIlosc.setText( "0" );

		int id_producenta =
			SlownikManager.pobierzIdZBazy( comboProducent.getSelectedItem().toString(),
				SlownikElement.tabelaProducent ).intValue();

		int ilosc = Walidator.parsujInteger( textIlosc.getText(), "Ilość" );

		return new Produkt( textKod.getText(), textNazwa.getText(), ilosc, true, id_producenta,
				cenaZakupu, id );

	}

	static void ukryjModalneOkno()
	{
		if ( oknoModalne == null || !oknoModalne.isVisible() )
			return;
		oknoModalne.dispose();
	}

	/*Listenery okna doadaj/edytu producenta*/

	final ActionListener anulujListener = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent arg0 )
		{
			ukryjModalneOkno();
		}
	};

	//
	// DODAJ -------------------PRODUCENTA---------------------------------------
	//
	protected ActionListener dodajListener = new ActionListener() {
		public ActionListener okListener = new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent a )
			{
				try {
					/*dodaj producenta*/
					if ( SlownikManager.dodaj( ProducentPanelEdytujDodaj.textNazwaProducenta.getText() ) ) {
						ukryjModalneOkno();
						/*odśwież widok w panelch magazy i edytujdodajprodukti ustaw go dodanego producenta jako wyświetlanego*/
						comboProducent.addItem( ProducentPanelEdytujDodaj.textNazwaProducenta.getText() );
						comboProducent.setSelectedItem( ProducentPanelEdytujDodaj.textNazwaProducenta.getText() );
						MagazynPanel.dodajComboBoxFiltrProducenta( ProducentPanelEdytujDodaj.textNazwaProducenta.getText() );
					}
				}
				catch ( Exception e ) {
					MojeUtils.showPrintError( e );
				}
			}
		};

		@Override
		public void actionPerformed( ActionEvent arg0 )
		{
			//  wywoluje sie puste onko modalne z pnelem producent
			oknoModalne =
				new DialogEdytujDodaj( okListener, anulujListener, new ProducentPanelEdytujDodaj(), 550,
						425 );
			oknoModalne.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
			oknoModalne.setSize( 450, 124 );
			oknoModalne.setVisible( true );
			// reszta dzieje sie w buttonie OK

		}
	};

	//
	// EDYTUJ -------------------RODUCENTA-------------------------------------------
	//
	protected ActionListener edytujListener = new ActionListener() {

		ActionListener okListener = new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent arg0 )
			{
				try {
					/*edytuj producenta*/
					String nazwaTmp = (String) comboProducent.getSelectedItem();
					if ( SlownikManager.edytuj( nazwaTmp,
						ProducentPanelEdytujDodaj.textNazwaProducenta.getText() ) ) {
						ukryjModalneOkno();
						/*odswież widoki ustaw jako wyświtlany zedytowany obiekt*/
						comboProducent.removeItem( nazwaTmp );
						comboProducent.addItem( ProducentPanelEdytujDodaj.textNazwaProducenta.getText() );
						comboProducent.setSelectedItem( ProducentPanelEdytujDodaj.textNazwaProducenta.getText() );
						MagazynPanel.usunComboBoxFiltrProducenta( nazwaTmp );
						MagazynPanel.dodajComboBoxFiltrProducenta( ProducentPanelEdytujDodaj.textNazwaProducenta.getText() );
					}

				}
				catch ( Exception e ) {
					MojeUtils.showPrintError( e );
				}
			}
		};

		@Override
		public void actionPerformed( ActionEvent arg0 )
		{
			try {
				oknoModalne =
					new DialogEdytujDodaj( okListener, anulujListener, new ProducentPanelEdytujDodaj(),
							550, 425 );
				ProducentPanelEdytujDodaj.uzupelnijProducenta( (String) comboProducent.getSelectedItem() );
				oknoModalne.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
				oknoModalne.setSize( 450, 124 );
				oknoModalne.setVisible( true );
			}
			catch ( Exception e ) {
				MojeUtils.showPrintError( e );
			}
		}
	};

	@Override
	public void czysc()
	{
		textKod.setText( "" );
		textNazwa.setText( "" );
		textDomyslnaCenaZakupu.setText( "" );
		textIlosc.setText( "" );
	}

}
