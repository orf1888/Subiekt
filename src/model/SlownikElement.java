package model;

import kontroler.ObiektWyszukanieWarunki.StrukturaWarunku;

public class SlownikElement
    implements ObiektZId
{
    final static public String tabelaProducent = "slownikProducent";

    public long id;

    public String wartosc;

    public final static String[] kolumnyWBazie = { "wartosc" };

    public SlownikElement()
    {
    }

    public SlownikElement( long id, String wartosc )
    {
        this.id = id;
        this.wartosc = wartosc;
    }

    @Override
    public int getId()
    {
        return (int) id;
    }

    @Override
    public String[] piszWierszTabeli()
    {
        String[] wiersz = new String[2];
        wiersz[0] = "" + id;
        wiersz[1] = wartosc;
        return wiersz;
    }

    @Override
    public String[] getKolumnyPrzeszukiwania()
    {
        return kolumnyWBazie;
    }

    @Override
    public StrukturaWarunku getWarunekWidoczny()
    {
        return null;
    }
}
