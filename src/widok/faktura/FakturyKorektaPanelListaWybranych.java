package widok.faktura;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import model.Faktura;
import model.produkt.ProduktWFakturze;
import utils.MojeUtils;
import utils.UserShowException;
import widok.abstrakt.ModelTabeli;
import widok.abstrakt.PanelOgolnyParametry;

public class FakturyKorektaPanelListaWybranych extends
		FakturyPanelListaWybranychAbstrakt
{

	private static final long serialVersionUID = 4068527048205276262L;

	public int zliczProdukty_Nazwa(String nazwa)
	{
		int count = 0;

		ModelTabeli model = fakturyPanel.listaWybranychPanel.model;
		for (int i = 0; i < model.getRowCount(); ++i)
			if (((String) model.getValueAt(i, 1)).equals(nazwa))
				++count;

		return count;
	}

	private final ActionListener korygujListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				int numerUsuwanegoWiersza = pobierzNumerZaznaczonegoWiersza();

				String strLP = (String) getModelValueAt(numerUsuwanegoWiersza,
						0);
				if (strLP.isEmpty())
					return;

				boolean ok = false;
				for (ProduktWFakturze p : fakturyPanel.faktura.produkty)
					if (strLP.equals("" + p.lp))
					{
						ok = true;
					}
				if (!ok)
				{
					MojeUtils.showMsg("Nie można korygować nowego produktu!");
					return;
				}

				int produktLP = Integer.parseInt(strLP);

				ProduktWFakturze produktKorekta = fakturyPanel
						.pobierzProduktZListy(produktLP).copy();
				produktKorekta.korekta = true;

				if (zliczProdukty_Nazwa(produktKorekta.produkt.nazwa) >= 2)
					return;

				/* zmien na liscie Produktow (na formatce magazyn) */
				int index = fakturyPanel
						.szukajIndexProduktuWModelu(produktKorekta);
				int ilosc = Integer
						.parseInt((String) fakturyPanel.panelMagazynu
								.getModelValueAt(index, 2));
				if (fakturyPanel.rodzajFaktury == Faktura.SPRZEDAZ)
					ilosc += produktKorekta.ilosc_produktu;
				else
					ilosc -= produktKorekta.ilosc_produktu;
				fakturyPanel.panelMagazynu
						.setModelValueAt("" + ilosc, index, 2);

				/* nowy produkt ma ujemna ilosc produktu */
				produktKorekta.ilosc_produktu = -produktKorekta.ilosc_produktu;

				/* zmien wartosc */
				produktKorekta.wartoscPoKorekcie = 0;

				((FakturyPanelKorekta) fakturyPanel).korektaProduktu(
						numerUsuwanegoWiersza, produktLP);

				((FakturyPanelKorekta) fakturyPanel).listaProduktowKorekta
						.add(produktKorekta);
			} catch (Exception e1)
			{
				MojeUtils.showPrintError(e1);
			}

		}

	};

	private final ActionListener usunListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				int numerUsuwanegoWiersza = pobierzNumerZaznaczonegoWiersza();

				if (((String) getModelValueAt(numerUsuwanegoWiersza, 0))
						.isEmpty())
				{
					/* korekta */
					((FakturyPanelKorekta) fakturyPanel)
							.usunProduktKorektaZListy((String) getModelValueAt(
									numerUsuwanegoWiersza, 1));
					tabelaUsunWiersz(numerUsuwanegoWiersza);
				} else
				{
					int usuwanyProduktLP = Integer
							.parseInt((String) getModelValueAt(
									numerUsuwanegoWiersza, 0));

					for (ProduktWFakturze p : fakturyPanel.faktura.produkty)
						if (p.lp == usuwanyProduktLP)
						{
							return;
						}

					/* nie-korekta, produkt nowo dodany */

					fakturyPanel.usunProduktZListy(usuwanyProduktLP);
					--globalny_lp;
					tabelaUsunWiersz(numerUsuwanegoWiersza);
					fakturyPanel.aktualizujProdukty_LP(usuwanyProduktLP);
					aktualizujProdukty_LP_formatka(usuwanyProduktLP);
				}
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

	boolean exit = false;

	private final TableModelListener listaWybranychListener = new TableModelListener()
	{

		@Override
		public void tableChanged(TableModelEvent e)
		{
			if (exit)
			{
				exit = false;
				return;
			}
			int row = e.getFirstRow();
			int column = e.getColumn();
			if (row < 0)
				return;
			if (column < 2 || column > 3)
				return;

			TableModel model = (TableModel) e.getSource();

			boolean isKorekta = ((String) model.getValueAt(row, 0)).isEmpty();

			if (!isKorekta)
			{
				int edytowanyProduktLP = Integer.parseInt((String) model
						.getValueAt(row, 0));
				boolean wycofaj = false;
				for (ProduktWFakturze p : fakturyPanel.faktura.produkty)
					if (p.lp == edytowanyProduktLP)
					{
						wycofaj = true;
					}

				if (wycofaj)
				{
					/* nie mozemy edytowac produktow ze starej faktury - wycofaj */
					ProduktWFakturze zmienionyProdukt = fakturyPanel
							.pobierzProduktZListy(edytowanyProduktLP);

					if (column == 2)
					{
						String staraWartosc = MojeUtils
								.utworzWartoscZlotowki(zmienionyProdukt._cena_jednostkowa);
						exit = true;
						model.setValueAt(staraWartosc, row, column);
					} else
					{
						String staraWartosc = ""
								+ zmienionyProdukt.ilosc_produktu;
						exit = true;
						model.setValueAt(staraWartosc, row, column);
					}
				} else
				{
					/* zwykla edycja */
					String data = (String) model.getValueAt(row, column);
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
								throw new UserShowException(
										"Wprowadź poprawną liczbę!");

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
							String staraWartosc = ""
									+ zmienionyProdukt.ilosc_produktu;
							model.setValueAt(staraWartosc, row, column);
							return;
						}
					}
					int wartosc = zmienionyProdukt.liczWartoscZNarzutem();
					model.setValueAt(MojeUtils.utworzWartoscZlotowki(wartosc),
							row, 4);

				}
				return;
			} else
			{
				/* edycja korekty */
				String data = (String) model.getValueAt(row, column);
				String edytowanyProduktNazwa = (String) model
						.getValueAt(row, 1);
				int iloscOryginalnegoProduktu = Integer.parseInt((String) model
						.getValueAt(row - 1, 3));
				ProduktWFakturze zmienionyProdukt = ((FakturyPanelKorekta) fakturyPanel)
						.pobierzProduktKorekteZListy(edytowanyProduktNazwa);

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
						if (data.startsWith("-"))
						{

							if (!MojeUtils.isNumer(data.substring(1)))
								throw new UserShowException(
										"Wprowadź poprawną liczbę!");

							/* sprawdz czy nie pobieramy za duzo */
							int nowa_wartosc = Integer.parseInt(data
									.substring(1));

							fakturyPanel.aktualizujIloscProduktow_panelMagazyn(
									zmienionyProdukt, -nowa_wartosc,
									fakturyPanel.rodzajFaktury);

							zmienionyProdukt.ilosc_produktu = -nowa_wartosc;
						} else
						{
							if (!MojeUtils.isNumer(data))
								throw new UserShowException(
										"Wprowadź poprawną liczbę!");

							/* sprawdz czy nie pobieramy za duzo */
							int nowa_wartosc = Integer.parseInt(data);

							fakturyPanel.aktualizujIloscProduktow_panelMagazyn(
									zmienionyProdukt, nowa_wartosc,
									fakturyPanel.rodzajFaktury);

							zmienionyProdukt.ilosc_produktu = nowa_wartosc;
						}
					} catch (Exception ex)
					{
						MojeUtils.showError(ex);
						/* zla wartosc - przywroc stara */
						String staraWartosc = ""
								+ zmienionyProdukt.ilosc_produktu;
						model.setValueAt(staraWartosc, row, column);
						return;
					}
				}
				zmienionyProdukt.wartoscPoKorekcie = zmienionyProdukt._cena_jednostkowa
						* (zmienionyProdukt.ilosc_produktu + iloscOryginalnegoProduktu);
				model.setValueAt(
						MojeUtils
								.utworzWartoscZlotowki(zmienionyProdukt.wartoscPoKorekcie),
						row, 4);
			}
		}
	};

	public FakturyKorektaPanelListaWybranych(
			FakturyPanelEdytujDodaj fakturyPanel) {
		try
		{
			globalny_lp = 0;
			this.fakturyPanel = fakturyPanel;
			JPopupMenu popupMenu = new JPopupMenu();
			{
				JMenuItem itemKoryguj = new JMenuItem("Koryguj", null);
				popupMenu.add(itemKoryguj);
				itemKoryguj.setHorizontalTextPosition(SwingConstants.RIGHT);
				itemKoryguj.addActionListener(korygujListener);
			}
			{
				JMenuItem itemUsun = new JMenuItem("Usuń", null);
				popupMenu.add(itemUsun);
				itemUsun.setHorizontalTextPosition(SwingConstants.RIGHT);
				itemUsun.addActionListener(usunListener);
			}

			PanelOgolnyParametry params = PanelOgolnyParametry
					.createMinimalParametry(tworzModelFuktor(), (Integer) null,
							popupMenu, null, new String[]
							{ "L.p.", "Nazwa", "Cena", "Ilość", "Wartość" });
			funktorDwuklikTabela = new FunktorDwuklikTabelaWybieraniaProduktu(
					this);
			init(params);
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
	protected void ustawTabele(ModelTabeli model, Integer columnToDelete)
	{
		super.ustawTabele(model, columnToDelete);
		zmienEditableFunktor(listaWybranychEditableFunktor);
		dodajListener(listaWybranychListener);
	}
}