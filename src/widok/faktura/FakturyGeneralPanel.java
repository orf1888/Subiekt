package widok.faktura;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import kontroler.FakturaBaza;
import kontroler.ObiektWyszukanieWarunki;
import kontroler.ProduktBaza;
import model.Faktura;
import model.ObiektWiersz;
import utils.MojeUtils;
import utils.UserShowException;
import widok.WyswietlPDFPanel;
import widok.abstrakt.DialogEdytujDodaj;
import widok.abstrakt.PanelEdytujDodajObiekt;
import widok.abstrakt.PanelOgolnyParametry;
import widok.abstrakt.PanelOgolnyPrzyciski;

public class FakturyGeneralPanel
	extends PanelOgolnyPrzyciski
{
	private static final long serialVersionUID = -1171279686468102750L;

	protected ActionListener korygujListener;

	private PanelEdytujDodajObiekt panelDodajKorekta;

	private PanelEdytujDodajObiekt panelEdytujKorekta;

	protected ActionListener tworzEdytujFakturaLubKorektaListener()
	{
		return new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent arg0 )
			{
				try {
					// zapamietaj ktory wiersz edytujemy
					numerEdytowanegoWiersza = pobierzNumerZaznaczonegoWiersza();
					if ( numerEdytowanegoWiersza < 0 )
						return;
					ObiektWiersz wiersz = new ObiektWiersz( pobierzZaznaczonyWiersz() );// pobierz wiersz z tabeli
					if ( wiersz.wiersz == null )
						return;

					staryObiekt = obiektBazaManager.pobierzObiektZBazy( wiersz );

					if ( ( (Faktura) staryObiekt ).isKorekta ) {
						// edycja korekty
						oknoModalne =
							new DialogEdytujDodaj( panelEdytujOkButtonListener, anulujListener,
									panelEdytujKorekta, sizeX, sizeY );
					}
					else {
						// edycja faktury
						oknoModalne =
							new DialogEdytujDodaj( panelEdytujOkButtonListener, anulujListener,
									panelEdytuj, sizeX, sizeY );
					}


					// wstaw dane do formatki
					oknoModalne.uzupelnijFormatke( staryObiekt );

					oknoModalne.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
					oknoModalne.setVisible( true );

					// reszta dzieje sie w buttonie OK
				}
				catch ( Exception e ) {
					MojeUtils.showPrintError( e );
				}
			}
		};
	}

	protected ActionListener tworzZaplaconaListener()
	{
		return new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent arg0 )
			{
				try {
					ObiektWiersz wiersz = new ObiektWiersz( pobierzZaznaczonyWiersz() );// pobierz wiersz z tabeli
					if ( wiersz.wiersz == null )
						return;
					Faktura faktura = (Faktura) obiektBazaManager.pobierzObiektZBazy( wiersz );
					Object[] polskiePrzyciski = { "Zapłacona", "Nie zapłacona" };
					//utworzyc funkcję
					int wybor =
						JOptionPane.showOptionDialog( null, "Faktura " + faktura.numer + " jest "
							+ ( faktura.zaplacona ? ""
									: "nie " ) + "zapłacona", "Płatności", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, polskiePrzyciski, polskiePrzyciski[1] );
					if ( wybor == 0 ) {

						zmienZaplacona( true );
						//MojeUtils.showMsg( "Faktura " + FakturaBaza.getNumerFromWiersz( wiersz.wiersz )+ " zapłacona" );
					}

					if ( wybor == 1 )
						zmienZaplacona( false );

				}
				catch ( Exception e ) {
					MojeUtils.showPrintError( e );
				}
			}

			void zmienZaplacona( boolean zaplacona ) throws SQLException
			{
				ObiektWiersz wiersz = new ObiektWiersz( pobierzZaznaczonyWiersz() );// pobierz wiersz z tabeli
				if ( wiersz.wiersz == null )
					return;

				FakturaBaza.ustawZaplacona( wiersz, zaplacona );
			}
		};
	}

	private JPopupMenu tworzMenuPopup()
	{
		JPopupMenu result = new JPopupMenu();
		JMenuItem itemEdytuj = new JMenuItem( "Edytuj", null );
		result.add( itemEdytuj );
		itemEdytuj.setHorizontalTextPosition( SwingConstants.RIGHT );
		edytujListener = tworzEdytujFakturaLubKorektaListener();
		itemEdytuj.addActionListener( edytujListener );

		JMenuItem itemKorekta = new JMenuItem( "Dodaj korektę", null );
		result.add( itemKorekta );
		itemKorekta.setHorizontalTextPosition( SwingConstants.RIGHT );
		korygujListener = tworzKorektaListener();
		itemKorekta.addActionListener( korygujListener );

		JMenuItem itemZaplacona = new JMenuItem( "Płatność", null );
		result.add( itemZaplacona );
		itemZaplacona.setHorizontalTextPosition( SwingConstants.RIGHT );
		ActionListener zaplaconaListener = tworzZaplaconaListener();
		itemZaplacona.addActionListener( zaplaconaListener );
		return result;
	}


	ActionListener penelKorektaOkButtonListener = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent arg0 )
		{
			try {
				//pobierz dane z formatki
				Faktura nowa = (Faktura) oknoModalne.pobierzZFormatki( staryObiekt.getId() );
				Faktura stara = (Faktura) staryObiekt;
				FakturaBaza baza = ( (FakturaBaza) obiektBazaManager );

				if ( stara.isKorekta ) {
					throw new UserShowException( "hahaha\neastereggvol3" );
					// powinno wykonac sie normalne Edytuj-Ok-Button z PanelPrzyciski
					// tu wykonuje sie tylko Dodaj-Korekte-Ok-Button
				}
				else {
					// nowa
					nowa.isKorekta = true;
					baza.dodaj_encje_faktura( nowa );

					stara.aktualna = false;
					baza.ustawNieaktualna( stara );
					// edytuj produkty (jak przy edycji)

					ProduktBaza.dodajListeProduktow( nowa.produkty, nowa.id_faktura );
					ProduktBaza.dodajListeProduktow( nowa.produktyKorekta, nowa.id_faktura );
					boolean dodac = ( nowa.rodzaj == Faktura.SPRZEDAZ ) ? false
							: true;
					//ProduktBaza.zmienIloscProduktyZMagazynu( nowa.produkty, !dodac );
					ProduktBaza.instance().zmienIloscProduktyZMagazynu( nowa.produktyKorekta, dodac );

					int wiersz = pobierzNumerZaznaczonegoWiersza();
					tabelaUsunWiersz( wiersz );
					tabelaDodajWiersz( nowa.piszWierszTabeli() );
					// tabela Dodaj wiersz + tabela Usun wiersz
				}

				ukryjModalneOkno();
			}
			catch ( UserShowException e ) {
				MojeUtils.showError( e );
			}
			catch ( Exception e ) {
				MojeUtils.showPrintError( e );
			}
		}
	};

	protected ActionListener tworzKorektaListener()
	{
		return new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent arg0 )
			{
				try {
					// zapamietaj ktory wiersz edytujemy
					numerEdytowanegoWiersza = pobierzNumerZaznaczonegoWiersza();
					if ( numerEdytowanegoWiersza < 0 )
						return;
					ObiektWiersz wiersz = new ObiektWiersz( pobierzZaznaczonyWiersz() );// pobierz wiersz z tabeli
					if ( wiersz.wiersz == null )
						return;

					staryObiekt = obiektBazaManager.pobierzObiektZBazy( wiersz );
					if ( ( (Faktura) staryObiekt ).isKorekta )
						throw new UserShowException( "Nie można dodać korekty do korekty!" );
					oknoModalne =
						new DialogEdytujDodaj( penelKorektaOkButtonListener, anulujListener,
								panelDodajKorekta, sizeX, sizeY );

					// wstaw dane do formatki
					oknoModalne.uzupelnijFormatke( staryObiekt );


					oknoModalne.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
					oknoModalne.setVisible( true );

					// reszta dzieje sie w buttonie OK
				}
				catch ( UserShowException e ) {
					MojeUtils.showError( e );
				}
				catch ( Exception e ) {
					MojeUtils.showPrintError( e );
				}
			}
		};
	}

	public FakturyGeneralPanel( JPopupMenu popupTabeliMagazynu, int fakturaRodzaj )
		throws SQLException
	{
		try {
			panelDodajKorekta =
				new FakturyPanelKorekta( "Dodaj korektę", fakturaRodzaj, true, popupTabeliMagazynu );
			panelEdytujKorekta =
				new FakturyPanelKorekta( "Edytuj korektę", fakturaRodzaj, true, popupTabeliMagazynu );
			warunki = new ObiektWyszukanieWarunki( new Faktura() );
			PanelEdytujDodajObiekt panelDwukliku =
				fakturaRodzaj == Faktura.SPRZEDAZ ? new WyswietlPDFPanel()
						: null;

			//this.setBounds( 0, 0, 800, 400 );

			PanelOgolnyParametry params =
				new PanelOgolnyParametry( tworzModelFuktor( fakturaRodzaj ),
						Faktura.kolumnyWyswietlane.length - 1, tworzMenuPopup(), new FakturaBaza(),
						new FakturyPanelEdytujDodaj( "Dodaj fakturę", fakturaRodzaj, true,
								popupTabeliMagazynu, false ), new FakturyPanelEdytujDodaj(
								"Edytuj fakturę", fakturaRodzaj, true, popupTabeliMagazynu, false ),
						panelDwukliku, false, Faktura.kolumnyWyswietlane );
			params.setBounds( 1150, 550 );
			init( params );
		}
		catch ( Exception e ) {
			MojeUtils.showPrintError( e );
		}
	}

	//private InitModelFunktor initModelFunktor;

	public InitModelFunktor tworzModelFuktor( final int fakturaRodzaj )
	{
		return new InitModelFunktor() {
			@Override
			public String[][] getBeginningData()
			{
				try {
					warunki.dodajWarunek( 1, "aktualna" );
					warunki.dodajWarunek( fakturaRodzaj, "rodzaj" );
					return obiektBazaManager.pobierzWierszeZBazy( warunki );
				}
				catch ( Exception e ) {
					MojeUtils.showPrintError( e );
					return null;
				}
			}
		};
	}
}
