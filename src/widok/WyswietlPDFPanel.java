package widok;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import model.Faktura;
import model.ObiektZId;
import model.Wysylka;

import org.jpedal.PdfDecoder;
import org.jpedal.fonts.FontMappings;

import utils.Drukarz;
import utils.MojeUtils;
import widok.abstrakt.PanelEdytujDodajObiekt;

public class WyswietlPDFPanel
	extends PanelEdytujDodajObiekt
{
	private static final long serialVersionUID = -1816285170367599312L;

	JDialog frame;

	private PdfDecoder pdfDecoder;

	private String currentFile = null;

	private int currentPage = 1;

	private final JLabel pageCounter1 = new JLabel( "  Strona " );

	private final JTextField pageCounter2 = new JTextField( 4 );

	private final JLabel pageCounter3 = new JLabel( "z" );

	private File plik;

	public void initFormatki( String name )
	{
		pdfDecoder = new PdfDecoder( true );
		FontMappings.setFontReplacements();
		currentFile = name;

		try {
			pdfDecoder.openPdfFile( currentFile );
			pdfDecoder.decodePage( currentPage );
			pdfDecoder.setPageParameters( 1, 1 );
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		initializeViewer();
		pageCounter2.setText( String.valueOf( currentPage ) );
		pageCounter3.setText( "z " + pdfDecoder.getPageCount() + "  " );
	}

	public void JPanelDemo()
	{
		pdfDecoder = new PdfDecoder( true );

		FontMappings.setFontReplacements();

		initializeViewer();
	}


	private void initializeViewer()
	{
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		Container cPane = frame.getContentPane();
		cPane.setLayout( new BorderLayout() );

		JButton open = initOpenBut();
		Component[] itemsToAdd = initChangerPanel();

		JPanel topBar = new JPanel();
		topBar.setLayout( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
		topBar.add( open );
		for ( Component anItemsToAdd : itemsToAdd ) {
			topBar.add( anItemsToAdd );
		}

		cPane.add( topBar, BorderLayout.NORTH );

		JScrollPane display = initPDFDisplay();//setup scrollpane with pdf display inside
		cPane.add( display, BorderLayout.CENTER );

		frame.setBounds( 10, 10, 615, 700 );
		frame.setModal( true );


	}

	private JButton initOpenBut()
	{
		JButton open = new JButton();
		open.setIcon( new ImageIcon( getClass().getResource( "/org/jpedal/examples/viewer/res/open.gif" ) ) );
		open.setText( "Zapisz" );
		open.setToolTipText( "Zapisz" );
		open.setBorderPainted( false );
		open.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				try {
					MojeUtils.zapiszPlik( plik, "pdf", false );
				}
				catch ( Exception e1 ) {}
			}
		} );

		return open;
	}

	private JScrollPane initPDFDisplay()
	{
		JScrollPane currentScroll = new JScrollPane();
		currentScroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		currentScroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );

		currentScroll.setViewportView( pdfDecoder );

		return currentScroll;
	}

	private Component[] initChangerPanel()
	{
		Component[] list = new Component[11];

		/**back to page 1*/
		JButton start = new JButton();
		start.setBorderPainted( false );
		URL startImage = getClass().getResource( "/org/jpedal/examples/viewer/res/start.gif" );
		start.setIcon( new ImageIcon( startImage ) );
		start.setToolTipText( "Przewin do początku dokumentu" );
		list[0] = start;
		start.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if ( currentFile != null && currentPage != 1 ) {
					currentPage = 1;
					try {
						pdfDecoder.decodePage( currentPage );
						pdfDecoder.invalidate();
						frame.repaint();
					}
					catch ( Exception e1 ) {
						System.err.println( "back to page 1" );
						e1.printStackTrace();
					}
					pageCounter2.setText( String.valueOf( currentPage ) );
				}
			}
		} );
		JButton fback = new JButton();
		fback.setBorderPainted( false );
		URL fbackImage = getClass().getResource( "/org/jpedal/examples/viewer/res/fback.gif" );
		fback.setIcon( new ImageIcon( fbackImage ) );
		fback.setToolTipText( "Cofnij o 10 stron" );
		list[1] = fback;
		fback.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if ( currentFile != null && currentPage > 10 ) {
					currentPage -= 10;
					try {
						pdfDecoder.decodePage( currentPage );
						pdfDecoder.invalidate();
						frame.repaint();
					}
					catch ( Exception e1 ) {
						System.err.println( "back 10 pages" );
						e1.printStackTrace();
					}
					pageCounter2.setText( String.valueOf( currentPage ) );
				}
			}
		} );

		JButton back = new JButton();
		back.setBorderPainted( false );
		URL backImage = getClass().getResource( "/org/jpedal/examples/viewer/res/back.gif" );
		back.setIcon( new ImageIcon( backImage ) );
		back.setToolTipText( "Cofnij o stronę" );
		list[2] = back;
		back.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if ( currentFile != null && currentPage > 1 ) {
					currentPage -= 1;
					try {
						pdfDecoder.decodePage( currentPage );
						pdfDecoder.invalidate();
						frame.repaint();
					}
					catch ( Exception e1 ) {
						System.err.println( "back 1 page" );
						e1.printStackTrace();
					}

					//		          set page number display
					pageCounter2.setText( String.valueOf( currentPage ) );
				}
			}
		} );

		pageCounter2.setEditable( true );
		pageCounter2.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent a )
			{

				String value = pageCounter2.getText().trim();
				int newPage;

				try {
					newPage = Integer.parseInt( value );

					if ( ( newPage > pdfDecoder.getPageCount() ) | ( newPage < 1 ) ) {
						return;
					}

					currentPage = newPage;
					try {
						pdfDecoder.decodePage( currentPage );
						pdfDecoder.invalidate();
						frame.repaint();
					}
					catch ( Exception e ) {
						System.err.println( "page number entered" );
						e.printStackTrace();
					}

				}
				catch ( Exception e ) {
					JOptionPane.showMessageDialog( null, '>' + value
						+ "< is Not a valid Value.\nPlease enter a number between 1 and "
						+ pdfDecoder.getPageCount() );
				}

			}

		} );

		list[3] = pageCounter1;
		list[4] = new JPanel();
		list[5] = pageCounter2;
		list[6] = new JPanel();
		list[7] = pageCounter3;

		/**forward icon*/
		JButton forward = new JButton();
		forward.setBorderPainted( false );
		URL fowardImage = getClass().getResource( "/org/jpedal/examples/viewer/res/forward.gif" );
		forward.setIcon( new ImageIcon( fowardImage ) );
		forward.setToolTipText( "Przewiń o stronę" );
		list[8] = forward;
		forward.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if ( currentFile != null && currentPage < pdfDecoder.getPageCount() ) {
					currentPage += 1;
					try {
						pdfDecoder.decodePage( currentPage );
						pdfDecoder.invalidate();
						frame.repaint();
					}
					catch ( Exception e1 ) {
						System.err.println( "forward 1 page" );
						e1.printStackTrace();
					}

					pageCounter2.setText( String.valueOf( currentPage ) );
				}
			}
		} );

		/**fast forward icon*/
		JButton fforward = new JButton();
		fforward.setBorderPainted( false );
		URL ffowardImage = getClass().getResource( "/org/jpedal/examples/viewer/res/fforward.gif" );
		fforward.setIcon( new ImageIcon( ffowardImage ) );
		fforward.setToolTipText( "Przewiń o 10 stron" );
		list[9] = fforward;
		fforward.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if ( currentFile != null && currentPage < pdfDecoder.getPageCount() - 9 ) {
					currentPage += 10;
					try {
						pdfDecoder.decodePage( currentPage );
						pdfDecoder.invalidate();
						frame.repaint();
					}
					catch ( Exception e1 ) {
						System.err.println( "forward 10 pages" );
						e1.printStackTrace();
					}

					pageCounter2.setText( String.valueOf( currentPage ) );
				}
			}
		} );

		/**goto last page*/
		JButton end = new JButton();
		end.setBorderPainted( false );
		URL endImage = getClass().getResource( "/org/jpedal/examples/viewer/res/end.gif" );
		end.setIcon( new ImageIcon( endImage ) );
		end.setToolTipText( "Przewiń do końca dokumentu" );
		//		    currentBar1.add(end);
		list[10] = end;
		end.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if ( currentFile != null && currentPage < pdfDecoder.getPageCount() ) {
					currentPage = pdfDecoder.getPageCount();
					try {
						pdfDecoder.decodePage( currentPage );
						pdfDecoder.invalidate();
						frame.repaint();
					}
					catch ( Exception e1 ) {
						System.err.println( "forward to last page" );
						e1.printStackTrace();
					}

					pageCounter2.setText( String.valueOf( currentPage ) );
				}
			}
		} );

		return list;
	}


	@Override
	public void uzupelnijFormatke( Object obiekt, JDialog parent )
	{
		currentPage = 1;
		pageCounter2.setText( "1" );
		frame = parent;
		if ( obiekt instanceof Faktura ) {
			plik = Drukarz.tworzFakturaPDF( (Faktura) obiekt );
		}
		else if ( obiekt instanceof Wysylka ) {
			plik = Drukarz.tworzWysylkaPDF( (Wysylka) obiekt );
		}
		initFormatki( plik.getAbsolutePath() );
	}

	public void uzupelnijRaport( String[][] PLN, String[][] EUR, String[][] USD, String[][] UAH,
			JDialog parent )
	{
		currentPage = 1;
		pageCounter2.setText( "1" );
		frame = parent;
		plik = Drukarz.tworzRaportDlugowPDF( PLN, EUR, USD, UAH );
		if ( plik != null ) {
			initFormatki( plik.getAbsolutePath() );
			frame.setVisible( true );
		}
		else {
			MojeUtils.showMsg( "Kontrachent nie posiada długów." );
		}
	}

	@Override
	public ObiektZId pobierzZFormatki( int id ) throws Exception
	{
		return null;
	}

	@Override
	public void czysc()
	{}

}
