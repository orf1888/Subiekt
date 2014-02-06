package model;

public class Sorter
{
	public static String tableName = "sorter";
	public int id;
	public String sorterFS;
	public String sorterFZ;
	public String sorterW;
	public static String[] kolumnyWBazie =
	{ "faktury_sprzedazy", "faktury_zakupu", "wysylki" };

	public Sorter(String sorterFZ, String sorterFv, String sorterW) {
		this.sorterFZ = sorterFv;
		this.sorterFS = sorterFZ;
		this.sorterW = sorterW;
		this.id = 1;
	}
}
