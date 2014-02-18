package widok.faktura;

import java.util.ArrayList;

import model.Faktura;
import model.ObiektWiersz;
import model.produkt.ProduktWFakturze;
import utils.MojeUtils;
import widok.abstrakt.ModelTabeli.IsCellEditableFunktor;
import widok.abstrakt.PanelOgolnyTabela;

public class FakturyPanelListaWybranychAbstrakt extends PanelOgolnyTabela
{

	public FakturyPanelListaWybranychAbstrakt(boolean s) {
		super(s);
	}

	private static final long serialVersionUID = 966289454104607336L;

	protected int globalny_lp;

	FakturyPanelEdytujDodaj fakturyPanel;

	public static class FunktorDwuklikTabelaWybieraniaProduktu extends
			FunktorDwuklikTabela
	{
		protected FakturyPanelListaWybranychAbstrakt parent;

		FunktorDwuklikTabelaWybieraniaProduktu(
				FakturyPanelListaWybranychAbstrakt parent) {
			this.parent = parent;
		}

		@Override
		public void run(ObiektWiersz wiersz, int row)
		{}
	}

	public static IsCellEditableFunktor listaWybranychEditableFunktor = new IsCellEditableFunktor()
	{
		@Override
		public boolean isCellEditable(int row, int column)
		{
			return (column == 2 || column == 3 || column == 5);
		}
	};

	public int zwiekszLP()
	{
		return ++globalny_lp;
	}

	public void czysc() throws Exception
	{
		globalny_lp = 0;
		przeladujTabele(new String[][]
		{}, editableFunktor, true);
	}

	public void zaladujListeFaktury()
	{
		try
		{
			przeladujTabele(tworzModelFuktor().getBeginningData(),
					editableFunktor, true);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		/* licz lp */
		globalny_lp = 0;
		int rowCount = model.getRowCount();
		for (int i = 0; i < rowCount; i++)
		{
			if (!((String) model.getValueAt(i, 0)).isEmpty())
				globalny_lp++;
		}
	}

	public InitModelFunktor tworzModelFuktor()
	{
		return new InitModelFunktor()
		{
			@Override
			public String[][] getBeginningData()
			{
				try
				{
					Faktura faktura = fakturyPanel.getFaktura();
					if (faktura == null)
						return new String[][]
						{};

					if (faktura.produktyKorekta == null)
						faktura.produktyKorekta = new ArrayList<>();
					int size = faktura.produkty.size()
							+ faktura.produktyKorekta.size();
					String[][] data = new String[size][];
					int i = 0;
					for (ProduktWFakturze p : faktura.produkty)
					{
						data[i] = p.pisz();
						++i;
						for (ProduktWFakturze pKorekta : faktura.produktyKorekta)
							if (pKorekta.produkt.nazwa.equals(p.produkt.nazwa))
							{
								data[i] = pKorekta.pisz();
								++i;
								break;
							}
					}
					return data;
				} catch (Exception e)
				{
					MojeUtils.showPrintError(e);
					return null;
				}
			}
		};
	}
}