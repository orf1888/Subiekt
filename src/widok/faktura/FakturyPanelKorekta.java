package widok.faktura;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JPopupMenu;

import kontroler.FakturaBaza;
import model.Faktura;
import model.Kontrachent;
import model.ObiektZId;
import model.produkt.ProduktWFakturze;
import utils.MojeUtils;
import utils.UserShowException;
import widok.MagazynPanel;

public class FakturyPanelKorekta extends FakturyPanelEdytujDodaj
{

	private static final long serialVersionUID = 5601771883531859989L;

	public List<ProduktWFakturze> listaProduktowKorekta;

	/**
	 * @param tytul
	 * @param rodzajFaktury
	 *            - Faktura.SPRZEDAZ lub ZAKUP
	 * @param editable
	 * @param popupMagazynWFaktury
	 *            - popup PrawegoKlikMyszy na tabeli Magazyn
	 * @throws SQLException
	 */
	public FakturyPanelKorekta(String tytul, int rodzajFaktury,
			boolean editable, JPopupMenu popupMagazynWFaktury)
			throws SQLException {
		super(tytul, rodzajFaktury, editable, popupMagazynWFaktury, true);
		listaProduktowKorekta = new ArrayList<ProduktWFakturze>();

		{
			listaWybranychPanel = new FakturyKorektaPanelListaWybranych(this);
			splitPane.setRightComponent(listaWybranychPanel);

			panelMagazynu = new MagazynPanel( /* wyswietlButtony */false,
					popupMagazynWFaktury);
			panelMagazynu.zmienDwuklikFunktor(new FunktorDwuklikAkcjaFaktura(
					listaWybranychPanel, listaProduktow, this));
			splitPane.setLeftComponent(panelMagazynu);
		}
	}

	@Override
	protected void zmienKontrachent()
	{
		if (tmpNieZmieniaKontrachenta)
			return;
		Kontrachent k = pobierzKontrachenta();

		for (ProduktWFakturze _produkt : listaProduktowKorekta)
		{
			double mnoznik = 100;
			mnoznik += k._cena_mnoznik;
			/* Prawidłowe zaokrąglenie */
			double tmp = ((_produkt.produkt.cena_zakupu) * mnoznik / 100);
			_produkt._cena_jednostkowa = (int) Math.round(tmp);
			for (int i = 0; i < listaWybranychPanel.getModelRows(); ++i)
			{
				if (listaWybranychPanel.getModelValueAt(i, 0) != null
						&& !((String) listaWybranychPanel.getModelValueAt(i, 0))
								.isEmpty())
					continue;
				try
				{
					listaWybranychPanel.tabelaEdytujWiersz(i, _produkt.pisz(),
							true);
				} catch (Exception e)
				{}
			}
		}
	}

	@Override
	protected void init()
	{
		super.init();
		listaProduktowKorekta.clear();
	}

	@Override
	public ObiektZId pobierzZFormatki(int id) throws Exception
	{
		Kontrachent kontrahent = pobierzKontrachenta();

		if (kontrahent == null)
		{
			throw new UserShowException("Nie wybrano kontrachenta");
		}

		Long waluta = pobierzWalute();
		if (waluta < 0)
		{
			throw new UserShowException("Nie wybrano waluty dla faktury!");
		}

		if (listaProduktow.isEmpty())
			throw new UserShowException("Lista produktów nie może być pusta!");
		if (id == 0)
		{
			/* id = 0 gdy nowe, inaczej edytujemy istniejace walidacja */
			int numer;
			if (rodzajFaktury == Faktura.ZAKUP)
			{
				if (!MojeUtils.isNumer(numerField.getText()))
				{
					throw new UserShowException("Podano niepoprawny numer");
				}
				numer = Integer.parseInt(numerField.getText());
			} else
			{
				numer = FakturaBaza.pobierzNrFaktury(rodzajFaktury) + 1;
			}
			Date data_wystawienia = panelDatyWystawFaktury.getDate();
			Date termin_platnosci = panelTerminPlatnosci.getDate();
			if (data_wystawienia.after(termin_platnosci))
				throw new UserShowException(
						"Data wystawienia nie może być po terminie płatności!");

			int wartosc = 0;
			out: for (ProduktWFakturze p : listaProduktow)
			{
				for (ProduktWFakturze pKorekta : listaProduktowKorekta)
					if (p.produkt.nazwa.equals(pKorekta.produkt.nazwa))
					{
						wartosc += pKorekta.wartoscPoKorekcie;
						continue out;
					}
				/* else - normalnie */
				wartosc += p.liczWartoscZNarzutem();
			}

			List<ProduktWFakturze> copy_lista = new ArrayList<ProduktWFakturze>();
			copy_lista.addAll(listaProduktow);
			List<ProduktWFakturze> copy_lista_korekta = new ArrayList<ProduktWFakturze>();
			copy_lista_korekta.addAll(listaProduktowKorekta);
			boolean zaplacona = false;
			return new Faktura(0, numer, data_wystawienia, termin_platnosci,
					zaplacona, rodzajFaktury, kontrahent, wartosc, true, true,
					copy_lista, copy_lista_korekta, waluta);
		} else
		{
			int wartosc = 0;
			out: for (ProduktWFakturze p : listaProduktow)
			{
				for (ProduktWFakturze pKorekta : listaProduktowKorekta)
					if (p.produkt.nazwa.equals(pKorekta.produkt.nazwa))
					{
						wartosc += pKorekta.wartoscPoKorekcie;
						continue out;
					}
				// else - normalnie
				wartosc += p.liczWartoscZNarzutem();
			}
			List<ProduktWFakturze> copy_lista = new ArrayList<ProduktWFakturze>();
			copy_lista.addAll(listaProduktow);
			List<ProduktWFakturze> copy_lista_korekta = new ArrayList<ProduktWFakturze>();
			copy_lista_korekta.addAll(listaProduktowKorekta);

			int numer;
			if (rodzajFaktury == Faktura.ZAKUP)
			{
				if (!MojeUtils.isNumer(numerField.getText()))
				{
					throw new UserShowException("Podano niepoprawny numer");
				}
				numer = Integer.parseInt(numerField.getText());
			} else
			{
				numer = faktura.numer;
			}
			return new Faktura(faktura.id_faktura, numer,
					panelDatyWystawFaktury.getDate(),
					panelTerminPlatnosci.getDate(), false, rodzajFaktury,
					kontrahent, wartosc, true, faktura.isKorekta, copy_lista,
					copy_lista_korekta, waluta);
		}
	}

	public void korektaProduktu(int numerWiersza, int produktLP)
	{
		listaWybranychPanel.model.insertRow(
				numerWiersza + 1,
				new String[]
				{
						"",/* lp */
						(String) listaWybranychPanel.model.getValueAt(
								numerWiersza, 1),
						(String) listaWybranychPanel.model.getValueAt(
								numerWiersza, 2),
						"-"
								+ (String) listaWybranychPanel.model
										.getValueAt(numerWiersza, 3), "0" });
	}

	public ProduktWFakturze pobierzProduktKorekteZListy(
			String edytowanyProduktNazwa)
	{
		for (ProduktWFakturze _produkt : listaProduktowKorekta)
			if (_produkt.produkt.nazwa.equals(edytowanyProduktNazwa))
				return _produkt;
		return null;
	}

	public void usunProduktKorektaZListy(String nazwa)
	{
		for (ProduktWFakturze _produkt : listaProduktowKorekta)
			if (_produkt.produkt.nazwa.equals(nazwa))
			{
				listaProduktowKorekta.remove(_produkt);
				int index = szukajIndexProduktuWModelu(_produkt);
				if (index >= 0)
				{
					int ilosc = Integer.parseInt((String) panelMagazynu
							.getModelValueAt(index, 2));
					if (rodzajFaktury == Faktura.SPRZEDAZ)
						ilosc += _produkt.ilosc_produktu;
					else
						ilosc -= _produkt.ilosc_produktu;
					panelMagazynu.setModelValueAt("" + ilosc, index, 2);
				}
				return;
			}
	}
}