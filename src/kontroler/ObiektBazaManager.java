package kontroler;


import java.sql.SQLException;

import model.ObiektWiersz;
import model.ObiektZId;

public interface ObiektBazaManager
{
	String[][] pobierzWierszeZBazy( ObiektWyszukanieWarunki warunki ) throws SQLException;

	ObiektZId pobierzObiektZBazy( final ObiektWiersz wiersz );

	void dodaj( Object nowy ) throws Exception;

	void edytuj( Object stary, Object nowy ) throws Exception;

	void zmienWidocznosc( ObiektWiersz wiersz, boolean widoczny ) throws SQLException;
}
