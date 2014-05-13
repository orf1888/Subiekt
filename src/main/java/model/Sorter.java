package model;

public class Sorter
{
	public static String tableName = "sorter";
	public int id;
	public String sorterFS;
	public String sorterFZ;
	public String sorterW;
	public String sorterT;
	public String sorterWp;
	public static String[] kolumnyWBazie =
	{ "faktury_sprzedazy", "faktury_zakupu", "wysylki", "transport", "wplata" };

	public Sorter(String sorterFZ, String sorterFv, String sorterW,
			String sorterT, String sorterWp) {
		this.sorterFZ = sorterFv;
		this.sorterFS = sorterFZ;
		this.sorterW = sorterW;
		this.sorterT = sorterT;
		this.sorterWp = sorterWp;
		this.id = 1;
	}
}
