package model;

public class Podmiot
{
    public String nazwa;

    public String ulica;

    public String kod_pocztowy;

    public String miasto;

    public String nip;

    public Podmiot( String nazwa, String ulica, String kod_pocztowy, String miasto, String nip )
    {
        this.nazwa = nazwa;
        this.ulica = ulica;
        this.kod_pocztowy = kod_pocztowy;
        this.miasto = miasto;
        this.nip = nip;
    }
}
