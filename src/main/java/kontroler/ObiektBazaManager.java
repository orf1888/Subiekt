package kontroler;

import model.ObiektWiersz;
import model.ObiektZId;

public interface ObiektBazaManager
{
	String[][] pobierzWierszeZBazy(ObiektWyszukanieWarunki warunki)
			throws Exception;

	ObiektZId pobierzObiektZBazy(final ObiektWiersz wiersz);

	void dodaj(Object nowy) throws Exception;

	void edytuj(Object stary, Object nowy) throws Exception;

	void zmienWidocznosc(ObiektWiersz wiersz, boolean widoczny)
			throws Exception;
}
