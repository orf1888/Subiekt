package model;

import kontroler.ObiektWyszukanieWarunki.StrukturaWarunku;

public interface ObiektZId
{
	int getId();

	String[] piszWierszTabeli() throws Exception;

	String[] getKolumnyPrzeszukiwania();

	StrukturaWarunku getWarunekWidoczny();
}
