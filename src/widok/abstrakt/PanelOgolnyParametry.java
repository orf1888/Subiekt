package widok.abstrakt;

import javax.swing.JPopupMenu;

import kontroler.ObiektBazaManager;
import widok.abstrakt.PanelOgolnyTabela.InitModelFunktor;

public class PanelOgolnyParametry
{

	public PanelOgolnyParametry(InitModelFunktor initModelFunktor,
			JPopupMenu popupMenu, ObiektBazaManager obiektBazaManager,
			PanelEdytujDodajObiekt panelDodaj,
			PanelEdytujDodajObiekt panelEdytuj,
			PanelEdytujDodajObiekt panelWyswietl, boolean pokazBtnUsun,
			OpisKolumn opisKolumny) {
		setBounds(550, 425);
		this.initModelFunktor = initModelFunktor;
		this.popupMenu = popupMenu;
		this.obiektBazaManager = obiektBazaManager;
		this.panelDodaj = panelDodaj;
		this.panelEdytuj = panelEdytuj;
		this.panelWyswietl = panelWyswietl;
		this.opisKolumny = opisKolumny;
		this.pokazBtnUsun = pokazBtnUsun;
	}

	private PanelOgolnyParametry() {
		setBounds(550, 425);
	}

	public static PanelOgolnyParametry createMinimalParametry(
			InitModelFunktor initModelFunktor, JPopupMenu popupMenu,
			ObiektBazaManager obiektBazaManager,
			PanelEdytujDodajObiekt panelWyswietl, OpisKolumn opisKolumny)
	{
		PanelOgolnyParametry res = new PanelOgolnyParametry();
		res.initModelFunktor = initModelFunktor;
		res.popupMenu = popupMenu;
		res.obiektBazaManager = obiektBazaManager;
		res.panelDodaj = null;
		res.panelEdytuj = null;
		res.panelWyswietl = panelWyswietl;
		res.opisKolumny = opisKolumny;
		res.pokazBtnUsun = false;
		return res;
	}

	public void setBounds(int sizeX, int sizeY)
	{
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	InitModelFunktor initModelFunktor;

	JPopupMenu popupMenu;

	ObiektBazaManager obiektBazaManager;

	PanelEdytujDodajObiekt panelDodaj;

	PanelEdytujDodajObiekt panelEdytuj;

	PanelEdytujDodajObiekt panelWyswietl;

	static public class OpisKolumn
	{
		public OpisKolumn(String[] a, int _columnToDelete) {
			obiektKolumny = a;
			this.columnToDelete = _columnToDelete;
		}

		public String[] obiektKolumny;
		public int columnToDelete;
	}

	OpisKolumn opisKolumny;

	boolean pokazBtnUsun;

	int sizeX;

	int sizeY;
}