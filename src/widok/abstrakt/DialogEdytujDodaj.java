package widok.abstrakt;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import model.ObiektZId;

public class DialogEdytujDodaj
	extends JDialog
{
	private static final long serialVersionUID = -7013955380239851810L;

	private PanelEdytujDodajObiekt panelEdytujDodaj = null;

	/**
	 * Create the dialog.
	 * 
	 * @param tytul
	 * @param potwierdzListener
	 * @param cancelListener
	 */
	public DialogEdytujDodaj( ActionListener potwierdzListener, ActionListener cancelListener,
			PanelEdytujDodajObiekt panelEdytujDodaj, int sizeX, int sizeY )
	{
		this.panelEdytujDodaj = panelEdytujDodaj;
		setModal( true );
		//setBounds( 100, 100, 450, 385 );
		//setBounds( 100, 100, 1150, 550 );
		setBounds( 100, 100, sizeX, sizeY );
		//
		getContentPane().setLayout( new BorderLayout() );
		// panelEdytujDodajKontrachenta.setLayout(new FlowLayout());
		// panelEdytujDodajKontrachenta.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add( panelEdytujDodaj, BorderLayout.CENTER );
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add( buttonPane, BorderLayout.SOUTH );
			buttonPane.setLayout( new BorderLayout( 0, 0 ) );
			{
				JButton okButton = new JButton( "OK" );
				okButton.setActionCommand( "OK" );
				buttonPane.add( okButton, BorderLayout.WEST );
				okButton.addActionListener( potwierdzListener );
			}
			{
				JButton cancelButton = new JButton( "Anuluj" );
				cancelButton.setActionCommand( "Cancel" );
				buttonPane.add( cancelButton, BorderLayout.EAST );
				cancelButton.addActionListener( cancelListener );
			}
		}
	}

	public void uzupelnijFormatke( Object obiekt )
	{
		panelEdytujDodaj.uzupelnijFormatke( obiekt, this );
	}

	public ObiektZId pobierzZFormatki( int id ) throws Exception
	{
		return panelEdytujDodaj.pobierzZFormatki( id );
	}

	public void czysc()
	{
		if ( panelEdytujDodaj == null )
			return;
		panelEdytujDodaj.czysc();
	}

}
