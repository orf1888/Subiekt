package widok.wysylka;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import model.ObiektWiersz;
import model.Wysylka;
import model.produkt.ProduktWWysylce;
import utils.MojeUtils;
import utils.UserShowException;
import widok.abstrakt.ModelTabeli;
import widok.abstrakt.ModelTabeli.IsCellEditableFunktor;
import widok.abstrakt.PanelOgolnyParametry;
import widok.abstrakt.PanelOgolnyTabela;

public class WysylkaPanelListaWybranych extends PanelOgolnyTabela
{

	private static final long serialVersionUID = -5296564424835896434L;

	private int globalny_lp;

	WysylkaPanelEdytujDodaj wysylkaPanelEdytujDodaj;

	private final ActionListener usunListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				int numerUsuwanegoWiersza = pobierzNumerZaznaczonegoWiersza();

				int usuwanyProduktLP = Integer
						.parseInt((String) getModelValueAt(
								numerUsuwanegoWiersza, 0));
				wysylkaPanelEdytujDodaj.usunProduktZListy(usuwanyProduktLP);

				tabelaUsunWiersz(numerUsuwanegoWiersza);
				--globalny_lp;
				wysylkaPanelEdytujDodaj.aktualizujProdukty_LP(usuwanyProduktLP);
				aktualizujProdukty_LP_formatka(usuwanyProduktLP);
			} catch (Exception e1)
			{
				MojeUtils.showPrintError(e1);
			}

		}

		private void aktualizujProdukty_LP_formatka(int usuwanyProduktLP)
		{
			for (int i = 0; i < getModelRows(); ++i)
			{
				int lp_formatki = Integer.parseInt((String) getModelValueAt(i,
						0));
				if (lp_formatki > usuwanyProduktLP)
				{
					setModelValueAt("" + (lp_formatki - 1), i, 0);
				}
			}
		}
	};

	private final TableModelListener listaWybranychListener = new TableModelListener()
	{

		@Override
		public void tableChanged(TableModelEvent e)
		{
			int row = e.getFirstRow();
			int column = e.getColumn();
			if (row < 0)
				return;
			if (column != 3)
				return;

			TableModel model = (TableModel) e.getSource();
			String data = (String) model.getValueAt(row, column);
			int edytowanyProduktLP = Integer.parseInt((String) model
					.getValueAt(row, 0));
			ProduktWWysylce zmienionyProdukt = wysylkaPanelEdytujDodaj
					.pobierzProduktZListy(edytowanyProduktLP);

			if (column == 3)
			{
				try
				{
					/* 3 ilosc */
					/* weryfikuj zmodyfikowana komorke */
					if (!MojeUtils.isNumer(data))
						throw new UserShowException("Wprowadź poprawną liczbę!");

					int nowa_wartosc = Integer.parseInt(data);

					wysylkaPanelEdytujDodaj
							.aktualizujIloscProduktow_panelMagazyn(
									zmienionyProdukt, nowa_wartosc);

					zmienionyProdukt.ilosc_produktu = nowa_wartosc;
				} catch (Exception ex)
				{
					MojeUtils.showError(ex);
					/* zla wartosc - przywroc stara */
					String staraWartosc = "" + zmienionyProdukt.ilosc_produktu;
					model.setValueAt(staraWartosc, row, column);
					return;
				}
			}

		}
	};

	public WysylkaPanelListaWybranych(
			WysylkaPanelEdytujDodaj wysylkaPanelEdytujDodaj, boolean s) {
		super(s);
		try
		{
			globalny_lp = 0;
			this.wysylkaPanelEdytujDodaj = wysylkaPanelEdytujDodaj;
			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem itemUsun = new JMenuItem("Usuń", null);
			popupMenu.add(itemUsun);
			itemUsun.setHorizontalTextPosition(SwingConstants.RIGHT);
			itemUsun.addActionListener(usunListener);

			PanelOgolnyParametry params = PanelOgolnyParametry
					.createMinimalParametry(tworzModelFuktor(), (Integer) null,
							popupMenu, null, new String[]
							{ "L.p.", "Kod", "Nazwa", "Ilość" });
			funktorDwuklikTabela = new FunktorDwuklikTabelaWybieraniaProduktu(
					this);
			init(params, true);
			zmienEditableFunktor(listaWybranychEditableFunktor);
			dodajListener(listaWybranychListener);
			zmienDwuklikFunktor(new FunktorDwuklikTabelaWybieraniaProduktu(this));
		} catch (Exception e)
		{
			MojeUtils.showPrintError(e);
		}
	}

	@Override
	protected void ustawTabele(ModelTabeli model, Integer columnToDelete)
	{
		super.ustawTabele(model, columnToDelete);
		zmienEditableFunktor(listaWybranychEditableFunktor);
		dodajListener(listaWybranychListener);
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
					Wysylka wysylka = wysylkaPanelEdytujDodaj.getWysylka();
					if (wysylka == null)
						return new String[][]
						{};

					String[][] data = new String[wysylka.produkty.size()][];
					for (int i = 0; i < wysylka.produkty.size(); ++i)
						data[i] = wysylka.produkty.get(i).pisz();

					return data;
				} catch (Exception e)
				{
					MojeUtils.showPrintError(e);
					return null;
				}
			}
		};
	}

	public static class FunktorDwuklikTabelaWybieraniaProduktu extends
			FunktorDwuklikTabela
	{
		protected WysylkaPanelListaWybranych parent;

		FunktorDwuklikTabelaWybieraniaProduktu(WysylkaPanelListaWybranych parent) {
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
			/* nieedytowalne: pierwsza (lp) i druga (nazwa) */
			return column == 3;
		}
	};

	public int zwiekszLP()
	{
		return ++globalny_lp;
	}

	public void czysc() throws SQLException
	{
		globalny_lp = 0;
		przeladujTabele(new String[][]
		{}, editableFunktor, true);
	}

	public void zaladujListeWysylki()
	{
		try
		{
			przeladujTabele(tworzModelFuktor().getBeginningData(),
					editableFunktor, true);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		globalny_lp = pobierzIloscWierszy();
	}
}