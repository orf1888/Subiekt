package utils;

import java.io.File;
import java.io.FileOutputStream;

import kontroler.PodmiotBaza;
import kontroler.WalutaManager;
import model.Faktura;
import model.Waluta;
import model.Wysylka;
import model.produkt.ProduktWFakturze;
import model.produkt.ProduktWWysylce;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class Drukarz
{
	static BaseFont helvetica;

	static Font helvetica16;

	static Font helvetica12;

	static HeaderFooter narzedzieDoNaglowkaStopki = new HeaderFooter();

	/**
	 * zwraca plik
	 */
	public static File tworzFakturaPDF(Faktura faktura)
	{
		try
		{
			File temp = File.createTempFile("temp",
					Long.toString(System.nanoTime()));
			Document doc = otworzPdf(temp);
			wypelnijFakturaPdf(faktura, doc);
			doc.close();
			return temp;
		} catch (Exception e)
		{
			MojeUtils.showPrintError(e);
			return null;
		}
	}

	public static File tworzWysylkaPDF(Wysylka wysylka)
	{
		try
		{
			File temp = File.createTempFile("temp",
					Long.toString(System.nanoTime()));
			Document doc = otworzPdf(temp);
			wypelnijWysylkaPdf(wysylka, doc);
			doc.close();
			return temp;
		} catch (Exception e)
		{
			MojeUtils.showPrintError(e);
			return null;
		}
	}

	public static File tworzRaportDlugowPDF(String[][] PLN, String[][] EUR,
			String[][] USD, String[][] UAH)
	{
		try
		{
			File temp = File.createTempFile("temp",
					Long.toString(System.nanoTime()));
			Document doc = otworzPdf(temp);
			if (wypelnijRaportODluznikach(PLN, EUR, USD, UAH, doc))
			{
				doc.close();
				return temp;
			} else
				return null;
		} catch (Exception e)
		{
			MojeUtils.showPrintError(e);
			return null;
		}
	}

	private static Document otworzPdf(File temp) throws Exception
	{
		Document document = new Document(PageSize.A4, 15, 15, 35, 45);

		PdfWriter writer = PdfWriter.getInstance(document,
				new FileOutputStream(temp.getAbsolutePath()));

		writer.setBoxSize("art", new Rectangle(15, 50, 559, 788));
		writer.setPageEvent(narzedzieDoNaglowkaStopki);

		/* Ustawiamy polską czcionkę */
		if (helvetica == null)
		{
			helvetica = BaseFont.createFont(BaseFont.HELVETICA,
					BaseFont.CP1250, BaseFont.EMBEDDED);
			helvetica16 = new Font(helvetica, 16);
			helvetica12 = new Font(helvetica, 12);
		}
		document.open();
		return document;
	}

	@SuppressWarnings("deprecation")
	private static void wypelnijFakturaPdf(Faktura faktura, Document doc)
			throws Exception
	{
		float[] szerokosci = new float[]
		{ 500, 400, 500 };
		// ///////////////////TERMIN WYSTAWIENIA////////////////////////////
		{
			PdfPTable tabelka_dat = new PdfPTable(3);
			tabelka_dat.addCell(newCellWysrodkowanySzary("Data wystawienia",
					helvetica12));
			tabelka_dat.addCell(newSeparator());
			tabelka_dat.addCell(newCellWysrodkowanySzary("Termin płatności",
					helvetica12));
			// newline
			tabelka_dat.addCell(newCellWysrodkowany(MojeUtils
					.formatujDate(faktura.data_wystawienia.toGMTString()),
					helvetica12));
			tabelka_dat.addCell(newSeparator());
			tabelka_dat.addCell(newCellWysrodkowany(MojeUtils
					.formatujDate(faktura.termin_platnosci.toGMTString()),
					helvetica12));
			// settings

			tabelka_dat.setWidths(szerokosci);
			Paragraph paragDaty = new Paragraph();
			paragDaty.add(tabelka_dat);
			doc.add(paragDaty);
		}

		// ///////////////////NAZWA DOKUMENTU///////////////////////////////
		{
			Paragraph nazwa_dokumentu = new Paragraph("Faktura "
					+ (faktura.isKorekta ? "korygująca " : "VAT ")
					+ faktura.piszNumerFaktury(), helvetica16);
			nazwa_dokumentu.setSpacingBefore(50);
			nazwa_dokumentu.setAlignment(Element.ALIGN_CENTER);
			doc.add(nazwa_dokumentu);
		}

		// ///////////////////KOMU DOKUMENT/////////////////////////////////
		{
			PdfPTable tabelka_kontrachenci = new PdfPTable(3);
			tabelka_kontrachenci.addCell(newCellWysrodkowanySzary(
					"Sprzedawca:", helvetica12));
			tabelka_kontrachenci.addCell(newSeparator());
			tabelka_kontrachenci.addCell(newCellWysrodkowanySzary("Nabywca:",
					helvetica12));
			// newline
			tabelka_kontrachenci.addCell(newCellPodkreslony(
					PodmiotBaza.pobierzPodmiot().nazwa + "\n"
							+ PodmiotBaza.pobierzPodmiot().ulica + "\n"
							+ PodmiotBaza.pobierzPodmiot().kod_pocztowy + " "
							+ PodmiotBaza.pobierzPodmiot().miasto + "\n"
							+ "NIP:" + PodmiotBaza.pobierzPodmiot().nip,
					helvetica12));
			tabelka_kontrachenci.addCell(newSeparator());
			tabelka_kontrachenci.addCell(newCellPodkreslony(
					(faktura.kontrahent == null ? "" : faktura.kontrahent
							.piszDoFaktury()), helvetica12));
			// settings
			tabelka_kontrachenci.setWidths(szerokosci);
			Paragraph kontrachenci = new Paragraph();
			kontrachenci.setSpacingBefore(50);
			kontrachenci.add(tabelka_kontrachenci);
			doc.add(kontrachenci);
		}

		// ///////////////////TABELA PRODUKTÓW//////////////////////////////
		{
			// // naglowek
			String[] naglowki =
			{ "L.p.", "Nazwa", "Cena", "Ilość", "Wartość" };
			PdfPTable tabelka_produkty = new PdfPTable(naglowki.length);
			tabelka_produkty.setSpacingBefore(25);
			tabelka_produkty.setSpacingAfter(10);

			for (String naglowek : naglowki)
			{
				PdfPCell komorka_lp = new PdfPCell(new Phrase(naglowek,
						helvetica12));
				komorka_lp.setBackgroundColor(new BaseColor(244, 244, 244));
				tabelka_produkty.addCell(komorka_lp);
			}

			// // wiersze
			for (ProduktWFakturze p : faktura.produkty)
			{
				tabelka_produkty.addCell(new Phrase(p.lp + "", helvetica12));
				tabelka_produkty.addCell(new Phrase(p.produkt.nazwa,
						helvetica12));
				tabelka_produkty.addCell(newCellDoPrawej(
						MojeUtils.utworzWartoscZlotowki(p._cena_jednostkowa),
						helvetica12));
				tabelka_produkty.addCell(newCellDoPrawej("" + p.ilosc_produktu,
						helvetica12));
				tabelka_produkty.addCell(newCellDoPrawej(MojeUtils
						.utworzWartoscZlotowki(p.liczWartoscZNarzutem()),
						helvetica12));

				for (ProduktWFakturze pKorekta : faktura.produktyKorekta)
					if (p.produkt.nazwa.equals(pKorekta.produkt.nazwa))
					{
						tabelka_produkty.addCell(newSeparator());
						tabelka_produkty.addCell(newSeparator());
						tabelka_produkty
								.addCell(newCellDoPrawej(
										MojeUtils
												.utworzWartoscZlotowki(pKorekta._cena_jednostkowa),
										helvetica12));
						tabelka_produkty.addCell(newCellDoPrawej(""
								+ pKorekta.ilosc_produktu, helvetica12));
						tabelka_produkty
								.addCell(newCellDoPrawej(
										MojeUtils
												.utworzWartoscZlotowki(pKorekta.wartoscPoKorekcie),
										helvetica12));
					}
			}
			// // ostatni wiersz
			tabelka_produkty.addCell(newSeparator());
			tabelka_produkty.addCell(newSeparator());
			tabelka_produkty.addCell(newSeparator());
			tabelka_produkty.addCell(new Phrase("Razem:", helvetica12));
			tabelka_produkty.addCell(newCellDoPrawej(
					MojeUtils.utworzWartoscZlotowki(faktura.wartosc_z_narzutem)
							+ " "
							+ WalutaManager.pobierzNazweZBazy(
									faktura.waluta - 1, Waluta.tabelaWaluta),
					helvetica12));

			// settings
			float[] columnWidths = new float[]
			{ 35, 300, 70, 65, 90 };
			tabelka_produkty.setWidths(columnWidths);
			Paragraph tabelka = new Paragraph();
			tabelka.add(tabelka_produkty);
			doc.add(tabelka);
		}
		// ///////////////////DOLNE PODPISY/////////////////////////////////
		{
			PdfPTable tabelka_podpisy = new PdfPTable(3);

			tabelka_podpisy.addCell(newCellWysrodkowanySzary("Wystawił",
					helvetica12));
			tabelka_podpisy.addCell(newSeparator());
			tabelka_podpisy.addCell(newCellWysrodkowanySzary("Odebrał",
					helvetica12));
			// newline
			tabelka_podpisy.addCell(newCellPodkreslony("", helvetica12));
			tabelka_podpisy.addCell(newSeparator());
			tabelka_podpisy.addCell(newCellPodkreslony("", helvetica12));

			Paragraph podpisy = new Paragraph();
			podpisy.setSpacingBefore(50);
			podpisy.setKeepTogether(true);
			tabelka_podpisy.setWidths(szerokosci);

			podpisy.add(tabelka_podpisy);
			doc.add(podpisy);
		}
	}

	@SuppressWarnings("deprecation")
	private static void wypelnijWysylkaPdf(Wysylka wysylka, Document doc)
			throws Exception
	{
		float[] szerokosci = new float[]
		{ 500, 400, 500 };
		// ///////////////////TERMIN WYSTAWIENIA////////////////////////////
		{
			PdfPTable tabelka_dat = new PdfPTable(3);
			tabelka_dat.addCell(newCellWysrodkowanySzary("Data wysyłki",
					helvetica12));
			tabelka_dat.addCell(newSeparator());
			tabelka_dat.addCell(newSeparator());
			// newline
			tabelka_dat.addCell(newCellWysrodkowany(
					MojeUtils.formatujDate(wysylka.data_wysylki.toGMTString()),
					helvetica12));
			tabelka_dat.addCell(newSeparator());
			tabelka_dat.addCell(newSeparator());
			// settings

			tabelka_dat.setWidths(szerokosci);
			Paragraph paragDaty = new Paragraph();
			paragDaty.add(tabelka_dat);
			doc.add(paragDaty);

			// ///////////////////NAZWA DOKUMENTU///////////////////////////////
			{
				Paragraph nazwa_dokumentu = new Paragraph("Wysyłka "
						+ wysylka.getNumer(), helvetica16);
				nazwa_dokumentu.setSpacingBefore(50);
				nazwa_dokumentu.setAlignment(Element.ALIGN_CENTER);
				doc.add(nazwa_dokumentu);
			}
			// ///////////////////TABELA PRODUKTÓW//////////////////////////////
			{
				// // naglowek
				String[] naglowki =
				{ "L.p.", "Kod", "Nazwa", "Ilość" };
				PdfPTable tabelka_produkty = new PdfPTable(naglowki.length);
				tabelka_produkty.setSpacingBefore(25);
				tabelka_produkty.setSpacingAfter(10);

				for (String naglowek : naglowki)
				{
					PdfPCell komorka_lp = new PdfPCell(new Phrase(naglowek,
							helvetica12));
					komorka_lp.setBackgroundColor(new BaseColor(244, 244, 244));
					tabelka_produkty.addCell(komorka_lp);
				}

				// // wiersze
				for (ProduktWWysylce p : wysylka.produkty)
				{
					tabelka_produkty
							.addCell(new Phrase(p.lp + "", helvetica12));
					tabelka_produkty.addCell(new Phrase(p.produkt.kod,
							helvetica12));
					tabelka_produkty.addCell(new Phrase(p.produkt.nazwa,
							helvetica12));
					tabelka_produkty.addCell(newCellDoPrawej(""
							+ p.ilosc_produktu, helvetica12));
				}
				// settings
				float[] columnWidths = new float[]
				{ 35, 150, 300, 65 };
				tabelka_produkty.setWidths(columnWidths);
				Paragraph tabelka = new Paragraph();
				tabelka.add(tabelka_produkty);
				doc.add(tabelka);
			}
			// ///////////////////DOLNE PODPISY/////////////////////////////////
			{
				PdfPTable tabelka_podpisy = new PdfPTable(3);

				tabelka_podpisy.addCell(newCellWysrodkowanySzary("Wystawił",
						helvetica12));
				tabelka_podpisy.addCell(newSeparator());
				tabelka_podpisy.addCell(newCellWysrodkowanySzary("Odebrał",
						helvetica12));
				// newline
				tabelka_podpisy.addCell(newCellPodkreslony("", helvetica12));
				tabelka_podpisy.addCell(newSeparator());
				tabelka_podpisy.addCell(newCellPodkreslony("", helvetica12));

				Paragraph podpisy = new Paragraph();
				podpisy.setSpacingBefore(50);
				podpisy.setKeepTogether(true);
				tabelka_podpisy.setWidths(szerokosci);

				podpisy.add(tabelka_podpisy);
				doc.add(podpisy);
			}
		}
	}

	private static boolean wypelnijRaportODluznikach(String[][] PLN,
			String[][] EUR, String[][] USD, String[][] UAH, Document doc)
			throws DocumentException
	{
		if (PLN.length == 0 && EUR.length == 0 && USD.length == 0
				&& UAH.length == 0)
		{
			return false;
		} else
		{
			float[] szerokosci = new float[]
			{ 500, 400, 500 };
			// ///////////////////TERMIN WYSTAWIENIA////////////////////////////
			{
				PdfPTable tabelka_dat = new PdfPTable(3);
				tabelka_dat.addCell(newCellWysrodkowanySzary(
						"Data utworzenia raportu", helvetica12));
				tabelka_dat.addCell(newSeparator());
				tabelka_dat.addCell(newSeparator());
				// newline
				tabelka_dat
						.addCell(newCellWysrodkowany(MojeUtils
								.formatujDate(MojeUtils.pobierzAktualnaDate()),
								helvetica12));
				tabelka_dat.addCell(newSeparator());
				tabelka_dat.addCell(newSeparator());
				// settings

				tabelka_dat.setWidths(szerokosci);
				Paragraph paragDaty = new Paragraph();
				paragDaty.add(tabelka_dat);
				doc.add(paragDaty);
			}
			// ///////////////////NAZWA DOKUMENTU///////////////////////////////
			{
				String nazwa_kontrachenta = new String();
				if (EUR.length != 0)
					nazwa_kontrachenta = EUR[0][3];
				if (PLN.length != 0)
					nazwa_kontrachenta = PLN[0][3];
				if (USD.length != 0)
					nazwa_kontrachenta = USD[0][3];
				if (UAH.length != 0)
					nazwa_kontrachenta = UAH[0][3];

				Paragraph nazwa_dokumentu = new Paragraph(
						"Raport długów dla kontrahenta\n" + nazwa_kontrachenta,
						helvetica16);
				nazwa_dokumentu.setSpacingBefore(50);
				nazwa_dokumentu.setAlignment(Element.ALIGN_CENTER);
				doc.add(nazwa_dokumentu);
			}
			// ///////////////////TABELA FAKTÓR
			// PLN//////////////////////////////
			{
				if (PLN.length != 0)
				{
					// // naglowek
					String[] naglowki =
					{ "L.p.", "Nr. faktury", "Data wystawienia",
							"Data Płatności", "Wartość" };
					PdfPTable tabelka_PLN = new PdfPTable(naglowki.length);
					tabelka_PLN.setSpacingBefore(25);
					tabelka_PLN.setSpacingAfter(10);

					for (String naglowek : naglowki)
					{
						PdfPCell komorka_lp = new PdfPCell(new Phrase(naglowek,
								helvetica12));
						komorka_lp.setBackgroundColor(new BaseColor(244, 244,
								244));
						tabelka_PLN.addCell(komorka_lp);
					}
					// // wiersze PLN
					for (int i = 0; i < PLN.length; i++)
					{
						tabelka_PLN.addCell(new Phrase((i + 1) + "",
								helvetica12));
						tabelka_PLN.addCell(new Phrase(PLN[i][0], helvetica12));
						tabelka_PLN.addCell(new Phrase(PLN[i][1], helvetica12));
						tabelka_PLN.addCell(new Phrase(PLN[i][2], helvetica12));
						tabelka_PLN.addCell(newCellDoPrawej(PLN[i][4],
								helvetica12));
					}
					// ostatni wiersz z sumą
					tabelka_PLN.addCell(newSeparator());
					tabelka_PLN.addCell(newSeparator());
					tabelka_PLN.addCell(newSeparator());
					tabelka_PLN.addCell(newSeparator());
					tabelka_PLN.addCell(newCellWysrodkowanyZRamka(
							"Suma długu PLN\n"
									+ MojeUtils
											.zliczWartoscFakturDoRaportu(PLN),
							helvetica12));

					// settings
					float[] columnWidths = new float[]
					{ 35, 130, 105, 100, 110 };
					tabelka_PLN.setWidths(columnWidths);
					Paragraph tabelka = new Paragraph();
					tabelka.add(tabelka_PLN);
					doc.add(tabelka);

				}
			}

			// ///////////////////TABELA FAKTÓR
			// EUR//////////////////////////////
			{
				if (EUR.length != 0)
				{
					// // naglowek
					String[] naglowki =
					{ "L.p.", "Nr. faktury", "Data wystawienia",
							"Data Płatności", "Wartość" };
					PdfPTable tabelka_EUR = new PdfPTable(naglowki.length);
					tabelka_EUR.setSpacingBefore(25);
					tabelka_EUR.setSpacingAfter(10);

					for (String naglowek : naglowki)
					{
						PdfPCell komorka_lp = new PdfPCell(new Phrase(naglowek,
								helvetica12));
						komorka_lp.setBackgroundColor(new BaseColor(244, 244,
								244));
						tabelka_EUR.addCell(komorka_lp);
					}
					// // wiersze PLN
					for (int i = 0; i < EUR.length; i++)
					{
						tabelka_EUR.addCell(new Phrase((i + 1) + "",
								helvetica12));
						tabelka_EUR.addCell(new Phrase(EUR[i][0], helvetica12));
						tabelka_EUR.addCell(new Phrase(EUR[i][1], helvetica12));
						tabelka_EUR.addCell(new Phrase(EUR[i][2], helvetica12));
						tabelka_EUR.addCell(newCellDoPrawej(EUR[i][4],
								helvetica12));
					}
					// ostatni wiersz z sumą
					tabelka_EUR.addCell(newSeparator());
					tabelka_EUR.addCell(newSeparator());
					tabelka_EUR.addCell(newSeparator());
					tabelka_EUR.addCell(newSeparator());
					tabelka_EUR.addCell(newCellWysrodkowanyZRamka(
							"Suma długu EUR\n"
									+ MojeUtils
											.zliczWartoscFakturDoRaportu(EUR),
							helvetica12));

					// settings
					float[] columnWidths = new float[]
					{ 35, 130, 105, 100, 110 };
					tabelka_EUR.setWidths(columnWidths);
					Paragraph tabelka = new Paragraph();
					tabelka.add(tabelka_EUR);
					doc.add(tabelka);

				}
			}

			// ///////////////////TABELA FAKTÓR
			// USD//////////////////////////////
			{
				if (USD.length != 0)
				{
					// // naglowek
					String[] naglowki =
					{ "L.p.", "Nr. faktury", "Data wystawienia",
							"Data Płatności", "Wartość" };
					PdfPTable tabelka_USD = new PdfPTable(naglowki.length);
					tabelka_USD.setSpacingBefore(25);
					tabelka_USD.setSpacingAfter(10);

					for (String naglowek : naglowki)
					{
						PdfPCell komorka_lp = new PdfPCell(new Phrase(naglowek,
								helvetica12));
						komorka_lp.setBackgroundColor(new BaseColor(244, 244,
								244));
						tabelka_USD.addCell(komorka_lp);
					}
					// // wiersze PLN
					for (int i = 0; i < USD.length; i++)
					{
						tabelka_USD.addCell(new Phrase((i + 1) + "",
								helvetica12));
						tabelka_USD.addCell(new Phrase(USD[i][0], helvetica12));
						tabelka_USD.addCell(new Phrase(USD[i][1], helvetica12));
						tabelka_USD.addCell(new Phrase(USD[i][2], helvetica12));
						tabelka_USD.addCell(newCellDoPrawej(USD[i][4],
								helvetica12));
					}
					// ostatni wiersz z sumą
					tabelka_USD.addCell(newSeparator());
					tabelka_USD.addCell(newSeparator());
					tabelka_USD.addCell(newSeparator());
					tabelka_USD.addCell(newSeparator());
					tabelka_USD.addCell(newCellWysrodkowanyZRamka(
							"Suma długu USD\n"
									+ MojeUtils
											.zliczWartoscFakturDoRaportu(USD),
							helvetica12));

					// settings
					float[] columnWidths = new float[]
					{ 35, 130, 105, 100, 110 };
					tabelka_USD.setWidths(columnWidths);
					Paragraph tabelka = new Paragraph();
					tabelka.add(tabelka_USD);
					doc.add(tabelka);

				}
			}

			// ///////////////////TABELA FAKTÓR
			// UAH//////////////////////////////
			{
				if (UAH.length != 0)
				{
					// // naglowek
					String[] naglowki =
					{ "L.p.", "Nr. faktury", "Data wystawienia",
							"Data Płatności", "Wartość" };
					PdfPTable tabelka_UAH = new PdfPTable(naglowki.length);
					tabelka_UAH.setSpacingBefore(25);
					tabelka_UAH.setSpacingAfter(10);

					for (String naglowek : naglowki)
					{
						PdfPCell komorka_lp = new PdfPCell(new Phrase(naglowek,
								helvetica12));
						komorka_lp.setBackgroundColor(new BaseColor(244, 244,
								244));
						tabelka_UAH.addCell(komorka_lp);
					}
					// // wiersze PLN
					for (int i = 0; i < UAH.length; i++)
					{
						tabelka_UAH.addCell(new Phrase((i + 1) + "",
								helvetica12));
						tabelka_UAH.addCell(new Phrase(UAH[i][0], helvetica12));
						tabelka_UAH.addCell(new Phrase(UAH[i][1], helvetica12));
						tabelka_UAH.addCell(new Phrase(UAH[i][2], helvetica12));
						tabelka_UAH.addCell(newCellDoPrawej(UAH[i][4],
								helvetica12));
					}
					// ostatni wiersz z sumą
					tabelka_UAH.addCell(newSeparator());
					tabelka_UAH.addCell(newSeparator());
					tabelka_UAH.addCell(newSeparator());
					tabelka_UAH.addCell(newSeparator());
					tabelka_UAH.addCell(newCellWysrodkowanyZRamka(
							"Suma długu UAH\n"
									+ MojeUtils
											.zliczWartoscFakturDoRaportu(UAH),
							helvetica12));

					// settings
					float[] columnWidths = new float[]
					{ 35, 130, 105, 100, 110 };
					tabelka_UAH.setWidths(columnWidths);
					Paragraph tabelka = new Paragraph();
					tabelka.add(tabelka_UAH);
					doc.add(tabelka);

				}
			}
			return true;
		}
	}

	private static PdfPCell newSeparator()
	{
		PdfPCell komorka_data_separator = new PdfPCell();
		komorka_data_separator.setBorder(Rectangle.NO_BORDER);
		return komorka_data_separator;
	}

	private static PdfPCell newCellPodkreslony(String string, Font font)
	{
		PdfPCell komorka = new PdfPCell(new Phrase(string, font));
		komorka.setBorder(Rectangle.BOTTOM);
		komorka.setMinimumHeight(50);
		return komorka;
	}

	private static PdfPCell newCellWysrodkowany(String string, Font font)
	{
		PdfPCell komorka = new PdfPCell(new Phrase(string, font));
		komorka.setBorder(Rectangle.NO_BORDER);
		komorka.setHorizontalAlignment(Element.ALIGN_CENTER);
		return komorka;
	}

	private static PdfPCell newCellWysrodkowanyZRamka(String string, Font font)
	{
		PdfPCell komorka = new PdfPCell(new Phrase(string, font));
		komorka.setBorder(Rectangle.BOX);
		komorka.setBackgroundColor(new BaseColor(244, 244, 244));
		komorka.setHorizontalAlignment(Element.ALIGN_CENTER);
		return komorka;
	}

	private static PdfPCell newCellDoPrawej(String string, Font font)
	{
		PdfPCell komorka = new PdfPCell(new Phrase(string, font));
		komorka.setHorizontalAlignment(Element.ALIGN_RIGHT);
		return komorka;
	}

	private static PdfPCell newCellWysrodkowanySzary(String string, Font font)
	{
		PdfPCell komorka = new PdfPCell(new Phrase(string, font));
		komorka.setBorder(Rectangle.TOP);
		komorka.setUseBorderPadding(true);
		komorka.setBorderWidthTop(2f);
		komorka.setBorderColorTop(BaseColor.BLACK);
		komorka.setBackgroundColor(new BaseColor(244, 244, 244));
		komorka.setHorizontalAlignment(Element.ALIGN_CENTER);
		return komorka;
	}

	static class HeaderFooter extends PdfPageEventHelper
	{
		/** Alternating phrase for the header. */
		// Phrase[] header = new Phrase[2];
		Phrase footer;

		/** Current page number (will be reset for every chapter). */
		int pagenumber;

		@Override
		public void onOpenDocument(PdfWriter writer, Document document)
		{
			// header[0] = new Phrase( "Movie history" );
			footer = new Phrase("");
		}

		@Override
		public void onChapter(PdfWriter writer, Document document,
				float paragraphPosition, Paragraph title)
		{
			// header[1] = new Phrase( title.getContent() );
			pagenumber = 1;
		}

		@Override
		public void onStartPage(PdfWriter writer, Document document)
		{
			pagenumber++;
		}

		@Override
		public void onEndPage(PdfWriter writer, Document document)
		{
			Rectangle rect = writer.getBoxSize("art");
			ColumnText.showTextAligned(writer.getDirectContent(),
					Element.ALIGN_CENTER,
					new Phrase("Strona " + writer.getPageNumber()),
					(rect.getLeft() + rect.getRight()) / 2,
					rect.getBottom() - 18, 0);
			/* Stopka */
			ColumnText.showTextAligned(writer.getDirectContent(),
					Element.ALIGN_CENTER, new Phrase(
							("Wygenerowno w " + Globals.WersjaAplikacji)),
					(rect.getLeft() + 100), rect.getBottom() - 30, 0);
		}
	}

}
