package widok.faktura;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import kontroler.FakturaBaza;
import kontroler.ObiektBazaManager;
import model.produkt.ProduktWFakturze;
import utils.MojeUtils;
import utils.UserShowException;
import widok.abstrakt.ModelTabeli;
import widok.abstrakt.PanelOgolnyParametry;
import widok.abstrakt.PanelOgolnyParametry.OpisKolumn;

public class FakturyPanelListaWybranych extends
		FakturyPanelListaWybranychAbstrakt
{
	private static final long serialVersionUID = 4068527048205276262L;

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
				fakturyPanel.usunProduktZListy(usuwanyProduktLP);

				tabelaUsunWiersz(numerUsuwanegoWiersza);
				--globalny_lp;
				fakturyPanel.aktualizujProdukty_LP(usuwanyProduktLP);
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
			if (!(column == 2 || column == 3 || column == 5))
				return;

			TableModel model = (TableModel) e.getSource();
			String data = (String) model.getValueAt(row, column);
			String rabat = (String) model.getValueAt(row, 5);
			int edytowanyProduktLP = Integer.parseInt((String) model
					.getValueAt(row, 0));
			ProduktWFakturze zmienionyProdukt = fakturyPanel
					.pobierzProduktZListy(edytowanyProduktLP);

			if (column == 2)
			{
				String staraWartosc = MojeUtils
						.utworzWartoscZlotowki(zmienionyProdukt._cena_jednostkowa);

				/* 2 cena */
				int grosze = 0;
				try
				{
					grosze = MojeUtils.utworzWartoscGrosze(data, "");
				} catch (Exception exc)
				{
					MojeUtils.error(exc);
					model.setValueAt(staraWartosc, row, column);
					return;
				}

				zmienionyProdukt._cena_jednostkowa = grosze;
			} else if (column == 3)
			{
				try
				{
					/* 3 ilosc */
					/* weryfikuj zmodyfikowana komorke */
					if (!MojeUtils.isNumer(data))
						throw new UserShowException("Wprowadź poprawną liczbę!");

					/* sprawdz czy nie pobieramy za duzo */
					int nowa_wartosc = Integer.parseInt(data);

					fakturyPanel.aktualizujIloscProduktow_panelMagazyn(
							zmienionyProdukt, nowa_wartosc,
							fakturyPanel.rodzajFaktury);

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
			if (column == 5)
			{
				try
				{
					if (!MojeUtils.isNumer(data))
						throw new UserShowException("Wprowadź poprawną liczbę!");
					zmienionyProdukt.rabat = Integer.parseInt(rabat);
				} catch (Exception e1)
				{
					MojeUtils.showError(e1);
					/* zla wartosc - przywroc stara */
					model.setValueAt("" + zmienionyProdukt.rabat, row, column);
					return;
				}
			}
			/* przelicz wartosc */
			int wartosc = zmienionyProdukt.liczWartoscZNarzutem();
			// zmienionyProdukt.w
			model.setValueAt(MojeUtils.utworzWartoscZlotowki(wartosc), row, 4);
		}
	};

	//
	public final static int kolumnaUkryta = -1; // liczac od 0, kolumna id

	public final static OpisKolumn opisKolumn = new OpisKolumn(new String[]
	{ "L.p.", "Nazwa", "Cena", "Ilość", "Wartość", "Rabat %" }, kolumnaUkryta);

	//

	public FakturyPanelListaWybranych(
			FakturyPanelEdytujDodaj fakturyPanelEdytujDodaj, boolean s) {
		super(s);
		try
		{
			globalny_lp = 0;
			this.fakturyPanel = fakturyPanelEdytujDodaj;
			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem itemUsun = new JMenuItem("Usuń", null);
			popupMenu.add(itemUsun);
			itemUsun.setHorizontalTextPosition(SwingConstants.RIGHT);
			itemUsun.addActionListener(usunListener);

			// ObiektBazaManager baza = null;
			ObiektBazaManager baza = new FakturaBaza();
			PanelOgolnyParametry params = PanelOgolnyParametry
					.createMinimalParametry(tworzModelFuktor(), popupMenu,
							baza, null, opisKolumn);
			funktorDwuklikTabela = new FunktorDwuklikTabelaWybieraniaProduktu(
					this);
			init(params, true);
			zmienEditableFunktor(listaWybranychEditableFunktor);
			dodajListener(listaWybranychListener);
			zmienDwuklikFunktor(new FunktorDwuklikTabelaWybieraniaProduktu(this));
			setSortDisabled();
		} catch (Exception e)
		{
			MojeUtils.showPrintError(e);
		}
	}

	@Override
	protected void ustawTabele(ModelTabeli model, int columnToDelete)
	{
		super.ustawTabele(model, columnToDelete);
		zmienEditableFunktor(listaWybranychEditableFunktor);
		dodajListener(listaWybranychListener);
	}
}