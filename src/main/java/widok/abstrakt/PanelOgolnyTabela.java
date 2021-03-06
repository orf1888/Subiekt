package widok.abstrakt;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import kontroler.ManagerTabeli;
import kontroler.ObiektBazaManager;
import kontroler.ObiektWyszukanieWarunki;
import model.ObiektWiersz;
import utils.MojeUtils;
import widok.abstrakt.ModelTabeli.IsCellEditableFunktor;
import widok.abstrakt.PanelOgolnyParametry.OpisKolumn;

public abstract class PanelOgolnyTabela extends JPanel
{
	private final boolean isSorter;
	public JComboBox<String> sorter = new JComboBox<>(new String[]
	{ "Wszystkie", "Bieżący miesiąc", "Bieżący rok" });

	public PanelOgolnyTabela(boolean s) {
		this.isSorter = s;
	}

	public static abstract class FunktorDwuklikTabela
	{
		public abstract void run(ObiektWiersz wiersz, int row);
	}

	/**
	 * funkcja tworzaca model, definiowana w kazdym panelu osobno, bo pobiera
	 * osobne dane jest wywolywana na poczatku (INIT) i ew. w przypadku
	 * toReload==true
	 */
	public static abstract class InitModelFunktor
	{
		public abstract String[][] getBeginningData();
	};

	public final ActionListener anulujListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			ukryjModalneOkno();
		}
	};

	/**
	 * 
	 */
	ObiektBazaManager _obiektBazaManager = null;

	public ObiektBazaManager getObiektBazaManager()
	{
		return _obiektBazaManager;
	}

	public ObiektWyszukanieWarunki warunki;

	private static final long serialVersionUID = 3396143154900769567L;

	private JTable _table;

	/* ModelTabeliNieedytowalny */
	public ModelTabeli model;

	public IsCellEditableFunktor editableFunktor;

	protected DialogEdytujDodaj oknoModalne;

	private JScrollPane suwak_tabeli;

	private OpisKolumn opisKolumn;

	private int columnToDeleteCache;

	protected JPanel panel_naglowek;

	/*
	 * menu popup
	 */
	private JPopupMenu popup;

	/**
	 * funkcja ktora odswieza nam model - wczytuje z bazy
	 */
	public InitModelFunktor initModelFunktor;

	protected PanelEdytujDodajObiekt panelWyswietl = null;

	private boolean sortEnabled = true;

	/**
	 * @param columnToDelete
	 *            -- opcjonalne, numer kolumny do ukrycia
	 * @throws Exception
	 */
	protected void init(PanelOgolnyParametry params,
			boolean ustawWielkoscKolumny) throws Exception
	{
		//
		this._obiektBazaManager = params.obiektBazaManager;
		//

		/* ASSERT */
		if (funktorDwuklikTabela == null)
			throw new Exception("funktorDwuklikTabela cannot be null");

		this.editableFunktor = ModelTabeli.notEditableCellFunktor;
		this.initModelFunktor = params.initModelFunktor;

		_table = new JTable();
		_table.addMouseListener(tableMouseClickListener);
		_table.addMouseListener(tableMouseClickAdapter);

		this.popup = params.popupMenu;
		this.opisKolumn = params.opisKolumny;
		this.columnToDeleteCache = params.opisKolumny.columnToDelete;
		this.panelWyswietl = params.panelWyswietl;

		wczytajTabele(ustawWielkoscKolumny);
		setLayout(new BorderLayout(0, 0));

		{
			panel_naglowek = new JPanel();
			add(panel_naglowek, BorderLayout.NORTH);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[]
			{ 0, 0, 0 };
			gbl_panel.rowHeights = new int[]
			{ 0, 0 };
			gbl_panel.columnWeights = new double[]
			{ 0.0, 1.0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[]
			{ 0.0, Double.MIN_VALUE };
			panel_naglowek.setLayout(gbl_panel);
		}
		{
			if (isSorter)
			{
				JLabel pokaz = new JLabel("Pokaż za okres");
				JPanel panel_sortera = new JPanel();
				GridBagLayout gbl_panel = new GridBagLayout();
				gbl_panel.columnWidths = new int[]
				{ 0, 0 };
				gbl_panel.rowHeights = new int[]
				{ 0, 0 };
				gbl_panel.columnWeights = new double[]
				{ 0.0, Double.MIN_VALUE };
				gbl_panel.rowWeights = new double[]
				{ 0.0, Double.MIN_VALUE };
				panel_sortera.setLayout(gbl_panel);
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 0;
				constraints.gridy = 0;
				constraints.insets = new Insets(0, 0, 0, 5);
				panel_sortera.add(pokaz, constraints);
				GridBagConstraints constraints1 = new GridBagConstraints();
				constraints1.gridx = 1;
				constraints1.gridy = 0;
				constraints1.anchor = GridBagConstraints.WEST;
				panel_sortera.add(sorter, constraints1);
				add(panel_sortera, BorderLayout.NORTH);
			}
			JPanel panel_tabeli = new JPanel();
			add(panel_tabeli, BorderLayout.CENTER);
			panel_tabeli.setLayout(new BorderLayout(0, 0));
			suwak_tabeli = new JScrollPane();
			panel_tabeli.add(suwak_tabeli, BorderLayout.CENTER);
			suwak_tabeli.setViewportView(_table);
		}
	}

	public void wczytajTabele(boolean ustawWielkoscKolumny) throws Exception
	{
		przeladujTabele(initModelFunktor.getBeginningData(), editableFunktor,
				ustawWielkoscKolumny);
	}

	public void wyczyscTabele()
	{
		for (int i = model.getRowCount() - 1; i >= 0; --i)
			model.removeRow(i);
	}

	protected void ustawTabele(ModelTabeli model, int columnToDelete)
	{
		_table.setModel(model);

		if (sortEnabled)
		{
			RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
			_table.setRowSorter(sorter);
		}
		if (columnToDelete >= 0 && columnToDelete < _table.getColumnCount())
			_table.removeColumn(_table.getColumnModel().getColumn(
					columnToDelete));
	}

	public void tabelaDodajWiersz(String[] wiersz)
	{
		model.insertRow(model.getRowCount(), wiersz);
		ManagerTabeli manager = new ManagerTabeli(_table);
		manager.adjustColumn(1);
		if (_table.getColumnCount() > 2)
			manager.adjustColumn(2);
	}

	public String[] pobierzZaznaczonyWiersz()
	{
		int zaznaczony = pobierzNumerZaznaczonegoWiersza();
		if (zaznaczony < 0)
			return null;
		return model.pobierzWiersz(zaznaczony);
	}

	public int pobierzNumerZaznaczonegoWiersza()
	{
		int index_model = _table.getSelectedRow();
		if (index_model < 0)
			return -1;
		return _table.convertRowIndexToModel(index_model);
	}

	public int pobierzIloscWierszy()
	{
		return _table.getRowCount();
	}

	public void tabelaEdytujWiersz(int numerEdytowanegoWiersza,
			String[] wiersz, boolean ustawWielkoscKolumny) throws Exception
	{
		model.edytujWiersz(numerEdytowanegoWiersza, wiersz);
		przeladujTabele(ustawWielkoscKolumny);
	}

	public void tabelaUsunWiersz(int wiersz)
	{
		model.removeRow(wiersz);
		/* zmienila sie tabela, trzeba wyczyscic */
		resetujTabele();
	}

	public void ukryjModalneOkno()
	{
		if (oknoModalne == null || !oknoModalne.isVisible())
			return;
		oknoModalne.dispose();
	}

	private void resetujTabele()
	{
		/* do przeladowania z bazy! */
		_table.setRowSorter(null);
	}

	public void przeladujTabele(boolean ustawWielkoscKolumny) throws Exception
	{
		przeladujTabele(null/* default */, editableFunktor,
				ustawWielkoscKolumny);
	}

	public void przeladujTabele(String[][] data,
			IsCellEditableFunktor editableFunktor, boolean ustawWielkoscKolumny)
			throws Exception
	{
		resetujTabele();

		if (getObiektBazaManager() == null)
			MojeUtils.println("getObiektBazaManager() == null");

		if (data == null
		/** TMP **/
		&& getObiektBazaManager() != null)
			data = getObiektBazaManager().pobierzWierszeZBazy(warunki);
		model = new ModelTabeli(data, opisKolumn, editableFunktor);
		ustawTabele(model, columnToDeleteCache);
		if (ustawWielkoscKolumny)
		{
			ManagerTabeli manager = new ManagerTabeli(_table);
			manager.adjustColumn(1);
			manager.adjustColumn(2);
		}
	}

	/**
	 * obsluguje dwuklik na tabeli
	 */
	public FunktorDwuklikTabela funktorDwuklikTabela;

	public void zmienDwuklikFunktor(FunktorDwuklikTabela nowyFunktor)
	{
		funktorDwuklikTabela = nowyFunktor;
	}

	public void zmienEditableFunktor(IsCellEditableFunktor nowyFunktor)
	{
		editableFunktor = nowyFunktor;
		model.setIsCellEditableFunktor(editableFunktor);
	}

	public void dodajListener(TableModelListener listener)
	{
		model.addTableModelListener(listener);
	}

	MouseListener tableMouseClickListener = new MouseListener()
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			try
			{
				/* dwuklik - otwarcie okna modalnego */
				if (e.getClickCount() % 2 == 0)
				{

					ObiektWiersz wiersz = new ObiektWiersz(
							pobierzZaznaczonyWiersz());
					if (wiersz.wiersz == null)
						return;

					funktorDwuklikTabela.run(wiersz,
							((JTable) e.getSource()).getSelectedRow());
				}
			} catch (Exception ex)
			{
				MojeUtils.showPrintError(ex);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{}

		@Override
		public void mouseExited(MouseEvent e)
		{}

		@Override
		public void mousePressed(MouseEvent e)
		{}

		@Override
		public void mouseReleased(MouseEvent e)
		{}
	};

	/**
	 * wyswietla popup-menu dla tabeli, zmienia selected row na klikniety
	 */
	MouseAdapter tableMouseClickAdapter = new MouseAdapter()
	{
		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (popup != null && e.isPopupTrigger())
			{
				JTable source = (JTable) e.getSource();
				int row = source.rowAtPoint(e.getPoint());
				int column = source.columnAtPoint(e.getPoint());

				if (!source.isRowSelected(row))
					source.changeSelection(row, column, false, false);

				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	};

	public Object getModelValueAt(int rowIndex, int columnIndex)
	{
		return model.getValueAt(rowIndex, columnIndex);
	}

	public void setModelValueAt(Object value, int rowIndex, int columnIndex)
	{
		model.setValueAt(value, rowIndex, columnIndex);
	}

	public int getModelRows()
	{
		return model.getRowCount();
	}

	public void setSortDisabled()
	{
		sortEnabled = false;
	}
}