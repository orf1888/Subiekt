package widok;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import kontroler.FakturaBaza;
import kontroler.KontrachentBaza;
import kontroler.ObiektWyszukanieWarunki;
import model.Faktura;
import model.Kontrachent;
import utils.MojeUtils;

public class ZestawieniaPanel
	extends JPanel
{

	private static final long serialVersionUID = -2897868125272003380L;

	final static FakturaBaza fakturaBaza = new FakturaBaza();

	private JTable table;

	/* na potrzeby formatki dluznicy */
	JComboBox<?> comboBoxKontrachentDluznicy;

	JComboBox<?> comboBoxKontrachentDlugi;

	JComboBox<?> comboBoxRaportDluznika;

	/* na potrzeby formatki dluznicy */
	Object warunekKontrachent = null;

	private final String tytulPanelu = "Zestawienie";

	private JPanel panelTabeli;

	/*
	 * 
	 */

	enum RodzajZestawienia
	{
		ZestawienieDluznicy, RaportDluguWalutowego, ZestawienieDlugi;
	}

	class ZestawienieActionListener
		implements ActionListener
	{

		RodzajZestawienia rodzajZestawienia;

		ZestawienieActionListener( RodzajZestawienia rodzajZestawienia )
		{
			this.rodzajZestawienia = rodzajZestawienia;
		}

		@Override
		public void actionPerformed( ActionEvent e )
		{
			try {
				switch ( rodzajZestawienia )
				{
					case ZestawienieDluznicy:
						dluznicy();
						break;
					case RaportDluguWalutowego:
						raportDlugi();
						break;
					case ZestawienieDlugi:
						dlugi();
						break;
					default:
						;
				}
			}
			catch ( Exception e1 ) {
				MojeUtils.showError( e1 );
			}
		}
	}

	/*Zestawienie pokazujące dłużników*/
	public void dluznicy() throws SQLException
	{
		ObiektWyszukanieWarunki warunki = new ObiektWyszukanieWarunki( new Faktura() );
		warunki.dodajWarunek( "0", "zaplacona" );
		warunki.dodajWarunek( "1", "aktualna" );
		warunki.dodajWarunek( Faktura.SPRZEDAZ, "rodzaj" );
		if ( warunekKontrachent != null )
			warunki.dodajWarunek( warunekKontrachent, "id_kontrachent" );


		String[][] data = fakturaBaza.pobierzWierszeZBazy( warunki );
		DefaultTableModel model = new DefaultTableModel( data, Faktura.kolumnyWyswietlane );
		table.setModel( model );
		table.removeColumn( table.getColumnModel().getColumn( 5 ) );
		panelTabeli.setBorder( new TitledBorder( UIManager.getBorder( "TitledBorder.border" ),
				tytulPanelu + " faktur niezapłaconych przez kontrachentów", TitledBorder.CENTER,
				TitledBorder.TOP, null, null ) );
	}

	public void dluznicyZmienKontrachent() throws SQLException
	{
		if ( comboBoxKontrachentDluznicy.getSelectedIndex() < 0 )
			return;
		if ( comboBoxKontrachentDluznicy.getSelectedItem().toString().isEmpty() )
			warunekKontrachent = null;
		else
			warunekKontrachent =
				( (Kontrachent) KontrachentBaza.pobierzObiektZBazy( comboBoxKontrachentDluznicy.getSelectedItem()
																								.toString() ) ).getId();
		dluznicy();
	}

	////

	/*Zestawienie pokazujące sprzedaż w określonym czasie*/
	public void raportDlugiZmienKontrachent() throws SQLException
	{
		if ( comboBoxRaportDluznika.getSelectedIndex() < 0 )
			return;
		if ( comboBoxRaportDluznika.getSelectedItem().toString().isEmpty() )
			warunekKontrachent = null;
		else
			warunekKontrachent =
				( (Kontrachent) KontrachentBaza.pobierzObiektZBazy( comboBoxRaportDluznika.getSelectedItem()
																							.toString() ) ).getId();
		raportDlugi();
	}

	public void raportDlugi() throws SQLException
	{
		//poierz listę faktur w PLN - 1
		ObiektWyszukanieWarunki warunkiDlugPLN = new ObiektWyszukanieWarunki( new Faktura() );
		warunkiDlugPLN.dodajWarunek( "1", "aktualna" );
		warunkiDlugPLN.dodajWarunek( Faktura.SPRZEDAZ, "rodzaj" );
		warunkiDlugPLN.dodajWarunek( "1", "waluta" ); // waluta PLN
		warunkiDlugPLN.dodajWarunek( "0", "zaplacona" ); // nie zapłacona
		if ( warunekKontrachent != null )
			warunkiDlugPLN.dodajWarunek( warunekKontrachent, "id_kontrachent" );
		warunkiDlugPLN.generujWarunekWhere();

		String[][] dataDlugKontrahentaPLN = fakturaBaza.pobierzWierszeZBazy( warunkiDlugPLN );
		/*		for ( int i = 0; i < dataDlugKontrahentaPLN.length; i++ ) {
					System.err.println();
					for ( int j = 0; j < 5; j++ ) {
						System.err.print( dataDlugKontrahentaPLN[i][j] + " " );
					}
				}
		*///poierz listę faktur w EUR - 2
		ObiektWyszukanieWarunki warunkiDlugEUR = new ObiektWyszukanieWarunki( new Faktura() );
		warunkiDlugEUR.dodajWarunek( "1", "aktualna" );
		warunkiDlugEUR.dodajWarunek( Faktura.SPRZEDAZ, "rodzaj" );
		warunkiDlugEUR.dodajWarunek( "2", "waluta" ); // waluta EUR
		warunkiDlugEUR.dodajWarunek( "0", "zaplacona" ); // nie zapłacona
		if ( warunekKontrachent != null )
			warunkiDlugEUR.dodajWarunek( warunekKontrachent, "id_kontrachent" );
		warunkiDlugEUR.generujWarunekWhere();

		String[][] dataDlugKontrahentaEUR = fakturaBaza.pobierzWierszeZBazy( warunkiDlugEUR );
		/*		for ( int i = 0; i < dataDlugKontrahentaEUR.length; i++ ) {
					System.err.println();
					for ( int j = 0; j < 5; j++ ) {
						System.err.print( dataDlugKontrahentaEUR[i][j] + " " );
					}
				}
		*///poierz listę faktur w USD - 3
		ObiektWyszukanieWarunki warunkiDlugUSD = new ObiektWyszukanieWarunki( new Faktura() );
		warunkiDlugUSD.dodajWarunek( "1", "aktualna" );
		warunkiDlugUSD.dodajWarunek( Faktura.SPRZEDAZ, "rodzaj" );
		warunkiDlugUSD.dodajWarunek( "3", "waluta" ); // waluta USD
		warunkiDlugUSD.dodajWarunek( "0", "zaplacona" ); // nie zapłacona
		if ( warunekKontrachent != null )
			warunkiDlugUSD.dodajWarunek( warunekKontrachent, "id_kontrachent" );
		warunkiDlugUSD.generujWarunekWhere();

		String[][] dataDlugKontrahentaUSD = fakturaBaza.pobierzWierszeZBazy( warunkiDlugUSD );
		/*		for ( int i = 0; i < dataDlugKontrahentaUSD.length; i++ ) {
					System.err.println();
					for ( int j = 0; j < 5; j++ ) {
						System.err.print( dataDlugKontrahentaUSD[i][j] + " " );
					}
				}		//poierz listę faktur w EUR - 4
		*/ObiektWyszukanieWarunki warunkiDlugUAH = new ObiektWyszukanieWarunki( new Faktura() );
		warunkiDlugUAH.dodajWarunek( "1", "aktualna" );
		warunkiDlugUAH.dodajWarunek( Faktura.SPRZEDAZ, "rodzaj" );
		warunkiDlugUAH.dodajWarunek( "4", "waluta" ); // waluta UAH
		warunkiDlugUAH.dodajWarunek( "0", "zaplacona" ); // nie zapłacona
		if ( warunekKontrachent != null )
			warunkiDlugUAH.dodajWarunek( warunekKontrachent, "id_kontrachent" );
		warunkiDlugUAH.generujWarunekWhere();

		String[][] dataDlugKontrahentaUAH = fakturaBaza.pobierzWierszeZBazy( warunkiDlugUAH );
		/*		for ( int i = 0; i < dataDlugKontrahentaUAH.length; i++ ) {
					System.err.println();
					for ( int j = 0; j < 5; j++ ) {
						System.err.print( dataDlugKontrahentaUAH[i][j] + " " );					}
				}
		*/
		WyswietlPDFPanel panel = new WyswietlPDFPanel();
		panel.uzupelnijRaport( dataDlugKontrahentaPLN, dataDlugKontrahentaEUR, dataDlugKontrahentaUSD,
			dataDlugKontrahentaUAH, new JDialog() );
	}

	public void dlugi() throws SQLException
	{
		ObiektWyszukanieWarunki warunki = new ObiektWyszukanieWarunki( new Faktura() );
		warunki.dodajWarunek( "0", "zaplacona" );
		warunki.dodajWarunek( "1", "aktualna" );
		warunki.dodajWarunek( Faktura.ZAKUP, "rodzaj" );
		if ( warunekKontrachent != null )
			warunki.dodajWarunek( warunekKontrachent, "id_kontrachent" );


		String[][] data = fakturaBaza.pobierzWierszeZBazy( warunki );
		DefaultTableModel model = new DefaultTableModel( data, Faktura.kolumnyWyswietlane );

		table.setModel( model );
		table.removeColumn( table.getColumnModel().getColumn( 5 ) );
		panelTabeli.setBorder( new TitledBorder( UIManager.getBorder( "TitledBorder.border" ),
				tytulPanelu + " faktur niezapłaconych kontrachentom", TitledBorder.CENTER,
				TitledBorder.TOP, null, null ) );
	}

	public void dlugiZmienKontrachent() throws SQLException
	{
		if ( comboBoxKontrachentDlugi.getSelectedIndex() < 0 )
			return;
		if ( comboBoxKontrachentDlugi.getSelectedItem().toString().isEmpty() )
			warunekKontrachent = null;
		else
			warunekKontrachent =
				( (Kontrachent) KontrachentBaza.pobierzObiektZBazy( comboBoxKontrachentDlugi.getSelectedItem()
																							.toString() ) ).getId();

		dlugi();
	}

	public void jeszczenic2() throws SQLException
	{
		// like zmienKontrachent
		//dlugi();
	}

	private String[] initCombo( boolean zPustymElementem ) throws SQLException
	{
		if ( zPustymElementem ) {
			String[] tablica = KontrachentBaza.pobierzWszystkieNazwyZBazy();
			String[] tablica2 = new String[tablica.length + 1];
			for ( int i = 0; i < tablica.length; ++i )
				tablica2[1 + i] = tablica[i];
			tablica2[0] = "";
			return tablica2;
		}
		else {
			return KontrachentBaza.pobierzWszystkieNazwyZBazy();
		}
	}

	public ZestawieniaPanel()
	{
		try {
			setLayout( new BorderLayout( 0, 0 ) );

			JPanel panel = new JPanel();
			add( panel, BorderLayout.NORTH );
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 0, 0, 0, 0 };
			gbl_panel.rowHeights = new int[] { 0, 0, 0 };
			gbl_panel.columnWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
			panel.setLayout( gbl_panel );

			/*JButton btnTest = new JButton( "test" );
			GridBagConstraints gbc_btnTest = new GridBagConstraints();
			gbc_btnTest.insets = new Insets(0, 0, 5, 0);
			gbc_btnTest.gridx = 0;
			gbc_btnTest.gridy = 0;
			btnTest.addActionListener( new ZestawienieActionListener( RodzajZestawienia.ZestawienieTest ) );
			panel.add( btnTest, gbc_btnTest );
			*/
			{
				JLabel btnDunicy = new JLabel( "Długi kontrahentów" );
				GridBagConstraints gbc_btnDunicy = new GridBagConstraints();
				gbc_btnDunicy.insets = new Insets( 0, 0, 5, 5 );
				gbc_btnDunicy.gridx = 1;
				gbc_btnDunicy.gridy = 0;
				panel.add( btnDunicy, gbc_btnDunicy );
			}
			////////////////////////////////
			{

				JLabel btnDlugi = new JLabel( "Nasze należności kontrahentom" );
				GridBagConstraints gbc_btnDlugi = new GridBagConstraints();
				gbc_btnDlugi.insets = new Insets( 0, 0, 5, 5 );
				gbc_btnDlugi.gridx = 0;
				gbc_btnDlugi.gridy = 0;
				panel.add( btnDlugi, gbc_btnDlugi );
			}

			{
				comboBoxKontrachentDlugi = new JComboBox<Object>( initCombo( true ) );
				GridBagConstraints gbc_comboBox = new GridBagConstraints();
				gbc_comboBox.insets = new Insets( 0, 0, 0, 5 );
				gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBox.gridx = 0;
				gbc_comboBox.gridy = 1;
				panel.add( comboBoxKontrachentDlugi, gbc_comboBox );
				comboBoxKontrachentDlugi.addActionListener( new ActionListener() {
					@Override
					public void actionPerformed( ActionEvent e )
					{
						try {
							dlugiZmienKontrachent();
						}
						catch ( SQLException e1 ) {
							MojeUtils.showPrintError( e1 );
						}
					}
				} );
			}
			/*////////////////////////////////
			{
				JButton btnWyliczObrot = new JButton( "Wylicz obrót" );
				GridBagConstraints gbc_btnWyliczObrot = new GridBagConstraints();
				gbc_btnWyliczObrot.insets = new Insets( 0, 0, 5, 0 );
				gbc_btnWyliczObrot.gridx = 2;
				gbc_btnWyliczObrot.gridy = 0;
				panel.add( btnWyliczObrot, gbc_btnWyliczObrot );
				//btnWyliczObrot.addActionListener( new ZestawienieActionListener( RodzajZestawienia.Obrot ) );
			}*/
			/*{
				JComboBox comboBox = new JComboBox();
				GridBagConstraints gbc_comboBox = new GridBagConstraints();
				gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBox.gridx = 2;
				gbc_comboBox.gridy = 1;
				panel.add( comboBox, gbc_comboBox );
				comboBox.addActionListener( new ActionListener() {
					@Override
					public void actionPerformed( ActionEvent e )
					{
						jeszczenic2();
					}
				} );
			}
			*
			*
			*/

			////////////////////////////////
			{
				comboBoxKontrachentDluznicy = new JComboBox<Object>( initCombo( true ) );
				GridBagConstraints gbc_comboBoxKontrachent_1 = new GridBagConstraints();
				gbc_comboBoxKontrachent_1.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBoxKontrachent_1.insets = new Insets( 0, 0, 0, 5 );
				gbc_comboBoxKontrachent_1.gridx = 1;
				gbc_comboBoxKontrachent_1.gridy = 1;
				panel.add( comboBoxKontrachentDluznicy, gbc_comboBoxKontrachent_1 );
				comboBoxKontrachentDluznicy.addActionListener( new ActionListener() {
					@Override
					public void actionPerformed( ActionEvent e )
					{
						try {
							dluznicyZmienKontrachent();
						}
						catch ( SQLException e1 ) {
							MojeUtils.showPrintError( e1 );
						}
					}
				} );
			}
			{
				comboBoxRaportDluznika = new JComboBox<Object>( initCombo( false ) );
				GridBagConstraints gbc_comboBoxRaportDluznika = new GridBagConstraints();
				gbc_comboBoxRaportDluznika.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBoxRaportDluznika.gridx = 2;
				gbc_comboBoxRaportDluznika.gridy = 1;
				panel.add( comboBoxRaportDluznika, gbc_comboBoxRaportDluznika );
				comboBoxRaportDluznika.addActionListener( new ActionListener() {
					@Override
					public void actionPerformed( ActionEvent e )
					{
						try {
							raportDlugiZmienKontrachent();
						}
						catch ( SQLException e1 ) {
							MojeUtils.showPrintError( e1 );
						}
					}
				} );
			}
			{
				JLabel lblGenerujRaportO = new JLabel( "Generuj raport o długach" );
				GridBagConstraints gbc_lblGenerujRaportO = new GridBagConstraints();
				gbc_lblGenerujRaportO.insets = new Insets( 0, 0, 5, 0 );
				gbc_lblGenerujRaportO.gridx = 2;
				gbc_lblGenerujRaportO.gridy = 0;
				panel.add( lblGenerujRaportO, gbc_lblGenerujRaportO );
			}

			//setLayout( new BorderLayout( 0, 0 ) );

			{
				panelTabeli = new JPanel();
				panelTabeli.setBorder( new TitledBorder( UIManager.getBorder( "TitledBorder.border" ),
						tytulPanelu, TitledBorder.CENTER, TitledBorder.TOP, null, null ) );
				add( panelTabeli, BorderLayout.CENTER );
				panelTabeli.setLayout( new BorderLayout( 0, 0 ) );
				table = new JTable();
				{
					add( panelTabeli, BorderLayout.CENTER );
					panelTabeli.setLayout( new BorderLayout( 0, 0 ) );
					JScrollPane suwak_tabeli = new JScrollPane();
					panelTabeli.add( suwak_tabeli, BorderLayout.CENTER );
					suwak_tabeli.setViewportView( table );
				}
			}
		}
		catch ( Exception e ) {
			MojeUtils.showError( e );
		}
	}
}
