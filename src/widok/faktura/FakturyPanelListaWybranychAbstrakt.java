package widok.faktura;

import java.sql.SQLException;
import java.util.ArrayList;

import model.Faktura;
import model.ObiektWiersz;
import model.produkt.ProduktWFakturze;
import utils.MojeUtils;
import widok.abstrakt.ModelTabeli.IsCellEditableFunktor;
import widok.abstrakt.PanelOgolnyTabela;

public class FakturyPanelListaWybranychAbstrakt
	extends PanelOgolnyTabela
{

	private static final long serialVersionUID = 966289454104607336L;

	protected int globalny_lp;

	FakturyPanelEdytujDodaj fakturyPanel;

	//FakturyPanelKorekta fakturyPanelKorekta;


	public static class FunktorDwuklikTabelaWybieraniaProduktu
		extends FunktorDwuklikTabela
	{
		protected FakturyPanelListaWybranychAbstrakt parent;

		FunktorDwuklikTabelaWybieraniaProduktu( FakturyPanelListaWybranychAbstrakt parent )
		{
			this.parent = parent;
		}

		@Override
		public void run( ObiektWiersz wiersz, int row )
		{
			/*parent.oknoModalne =
				new DialogEdytujDodaj( parent.anulujListener, parent.anulujListener,
						parent.panelWyswietl );

			parent.wstawDaneDoFormatki( wiersz );

			parent.oknoModalne.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
			parent.oknoModalne.setVisible( true );*/
		}
	}

	public static IsCellEditableFunktor listaWybranychEditableFunktor = new IsCellEditableFunktor() {
		@Override
		public boolean isCellEditable( int row, int column )
		{
			// nieedytowalne: pierwsza (lp) i druga (nazwa), oraz ostatnia (wartosc)
			return column > 1 && column < 4;
		}
	};

	public int zwiekszLP()
	{
		return ++globalny_lp;
	}

	public void czysc() throws SQLException
	{
		globalny_lp = 0;
		przeladujTabele( new String[][] {}, editableFunktor );
	}

	public void zaladujListeFaktury()
	{
		try {
			przeladujTabele( tworzModelFuktor().getBeginningData(), editableFunktor );
		}
		catch ( SQLException e ) {
			e.printStackTrace();
		}
		// licz lp
		globalny_lp = 0;
		int rowCount = model.getRowCount();
		for ( int i = 0; i < rowCount; i++ ) {
			if ( !( (String) model.getValueAt( i, 0 ) ).isEmpty() )
				globalny_lp++;
		}
	}

	public InitModelFunktor tworzModelFuktor()
	{
		return new InitModelFunktor() {
			@Override
			public String[][] getBeginningData()
			{
				try {
					Faktura faktura = fakturyPanel.getFaktura();
					if ( faktura == null )
						return new String[][] {};

					if ( faktura.produktyKorekta == null )
						faktura.produktyKorekta = new ArrayList();
					int size = faktura.produkty.size() + faktura.produktyKorekta.size();
					String[][] data = new String[size][];
					int i = 0;
					for ( ProduktWFakturze p : faktura.produkty ) {
						data[i] = p.pisz();
						++i;
						for ( ProduktWFakturze pKorekta : faktura.produktyKorekta )
							if ( pKorekta.produkt.nazwa.equals( p.produkt.nazwa ) ) {
								data[i] = pKorekta.pisz();
								++i;
								break;
							}
					}
					return data;
				}
				catch ( Exception e ) {
					MojeUtils.showPrintError( e );
					return null;
				}
			}
		};
	}
}
