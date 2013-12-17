package widok.abstrakt;

import javax.swing.JPopupMenu;

import kontroler.ObiektBazaManager;
import widok.abstrakt.PanelOgolnyTabela.InitModelFunktor;

public class PanelOgolnyParametry
{

	public PanelOgolnyParametry(InitModelFunktor initModelFunktor,
			Integer columnToDelete, JPopupMenu popupMenu,
			ObiektBazaManager obiektBazaManager,
			PanelEdytujDodajObiekt panelDodaj,
			PanelEdytujDodajObiekt panelEdytuj,
			PanelEdytujDodajObiekt panelWyswietl, boolean pokazBtnUsun,
			String[] obiektKolumny) {
		setBounds(550, 425);
		this.initModelFunktor = initModelFunktor;
		this.columnToDelete = columnToDelete;
		this.popupMenu = popupMenu;
		this.obiektBazaManager = obiektBazaManager;
		this.panelDodaj = panelDodaj;
		this.panelEdytuj = panelEdytuj;
		this.panelWyswietl = panelWyswietl;
		this.obiektKolumny = obiektKolumny;
		this.pokazBtnUsun = pokazBtnUsun;
	}

	private PanelOgolnyParametry() {
		setBounds(550, 425);
	}

	public static PanelOgolnyParametry createMinimalParametry(
			InitModelFunktor initModelFunktor, Integer columnToDelete,
			JPopupMenu popupMenu, PanelEdytujDodajObiekt panelWyswietl,
			String[] obiektKolumny)
	{
		PanelOgolnyParametry res = new PanelOgolnyParametry();
		res.initModelFunktor = initModelFunktor;
		res.columnToDelete = columnToDelete;
		res.popupMenu = popupMenu;
		res.obiektBazaManager = null;
		res.panelDodaj = null;
		res.panelEdytuj = null;
		res.panelWyswietl = panelWyswietl;
		res.obiektKolumny = obiektKolumny;
		res.pokazBtnUsun = false;
		return res;
	}

	public void setBounds(int sizeX, int sizeY)
	{
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	InitModelFunktor initModelFunktor;

	Integer columnToDelete;

	JPopupMenu popupMenu;

	ObiektBazaManager obiektBazaManager;

	PanelEdytujDodajObiekt panelDodaj;

	PanelEdytujDodajObiekt panelEdytuj;

	PanelEdytujDodajObiekt panelWyswietl;

	String[] obiektKolumny;

	boolean pokazBtnUsun;

	int sizeX;

	int sizeY;
}