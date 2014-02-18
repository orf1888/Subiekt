package model;

public class Sorter
{
	public static String tableName = "sorter";
	public int id;
	public String sorterFS;
	public String sorterFZ;
	public String sorterW;
	public String sorterT;
	public static String[] kolumnyWBazie =
	{ "faktury_sprzedazy", "faktury_zakupu", "wysylki", "transport" };

	public Sorter(String sorterFZ, String sorterFv, String sorterW,
			String sorterT) {
		this.sorterFZ = sorterFv;
		this.sorterFS = sorterFZ;
		this.sorterW = sorterW;
		this.sorterT = sorterT;
		this.id = 1;
	}
}
