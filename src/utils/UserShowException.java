package utils;

import java.io.IOException;

import utils.Loger.LogerNazwa;

public class UserShowException extends Exception
{

	private static final long serialVersionUID = -7358344264782353956L;

	public UserShowException(String message) throws IOException {
		super(message);
		Loger.log(LogerNazwa.BledyLog, message);
	}
}
