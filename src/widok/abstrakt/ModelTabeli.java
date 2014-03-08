package widok.abstrakt;

import javax.swing.table.DefaultTableModel;

import utils.MojeUtils;
import widok.abstrakt.PanelOgolnyParametry.OpisKolumn;

public class ModelTabeli extends DefaultTableModel
{
	private static final long serialVersionUID = 1L;

	public abstract static class IsCellEditableFunktor
	{
		abstract public boolean isCellEditable(int row, int column);
	}

	public static IsCellEditableFunktor notEditableCellFunktor = new IsCellEditableFunktor()
	{
		@Override
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}
	};

	private IsCellEditableFunktor isCellEditableFunktor;

	/**
	 * @param data
	 * @param opisKolumn
	 *            .obiektKolumny
	 * @param editable
	 *            - czy model jest edytowalny
	 * @throws Exception
	 */
	public ModelTabeli(Object[][] data, OpisKolumn opisKolumn,
			IsCellEditableFunktor isCellEditableFunktor) throws Exception {
		super(data, opisKolumn.obiektKolumny);

		if (data.length > 0
				&& (data[0].length != opisKolumn.obiektKolumny.length))
			throw new Exception("ModelTabeli dostal tablice \'data\' ktora ma "
					+ data[0].length + " kolumn, a opisKolumn jest na "
					+ opisKolumn.obiektKolumny.length);

		if (opisKolumn.columnToDelete >= 0
				&& opisKolumn.columnToDelete > opisKolumn.obiektKolumny.length)
			throw new Exception("ModelTabeli dostal columnToDelete "
					+ opisKolumn.columnToDelete + ", a opisKolumn jest na "
					+ opisKolumn.obiektKolumny.length);

		setIsCellEditableFunktor(isCellEditableFunktor);
	}

	public void setIsCellEditableFunktor(
			IsCellEditableFunktor isCellEditableFunktor)
	{
		this.isCellEditableFunktor = isCellEditableFunktor;
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return isCellEditableFunktor.isCellEditable(row, column);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Override
	public Class getColumnClass(int column)
	{
		if (column >= 0 && column < getColumnCount())
		{
			try
			{
				return getValueAt(0, column).getClass();
			} catch (Exception e)
			{
				System.out.println(column);
				MojeUtils.error(e);
			}
		} else
			return Object.class;
		return null;
	}

	public String[] pobierzWiersz(int row)
	{
		int liczbaKolumn = this.getColumnCount();
		String[] wiersz = new String[liczbaKolumn];

		for (int i = 0; i < liczbaKolumn; i++)
		{
			Object obj = this.getValueAt(row, i);
			wiersz[i] = (obj != null ? obj.toString() : "");
		}

		return wiersz;
	}

	/**
	 * zamienia wiersz, np. w wyniku wywolania "Edit" zmienna "editable" nie ma
	 * tu nic do rzeczy
	 * 
	 * @param row
	 * @param wiersz
	 */
	public void edytujWiersz(int row, String[] wiersz)
	{
		int liczbaKolumn = this.getColumnCount();

		for (int i = 0; i < liczbaKolumn; i++)
		{
			this.setValueAt(wiersz[i], row, i);
		}
	}
}