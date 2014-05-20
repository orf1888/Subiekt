package model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WalutaTest
{

	@Test
	public void testKonstruktor()
	{
		Waluta w = new Waluta(42, "Eurogbki");
		assertEquals(42, w.getId());
	}

	@Test(expected = NullPointerException.class)
	public void testNullValue()
	{
		new Waluta(42, null);
	}
}
