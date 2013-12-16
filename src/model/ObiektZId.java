package model;

import kontroler.ObiektWyszukanieWarunki.StrukturaWarunku;

public interface ObiektZId
{
    int getId();

    String[] piszWierszTabeli();

    String[] getKolumnyPrzeszukiwania();

    StrukturaWarunku getWarunekWidoczny();
}
