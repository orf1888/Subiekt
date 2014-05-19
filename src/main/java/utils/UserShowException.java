package utils;

import java.io.IOException;

import utils.Loger.LogerNazwa;

public class UserShowException extends RuntimeException
{

	private static final long serialVersionUID = -7358344264782353956L;

	public UserShowException(String message) {
		super(message);
		try
		{
			Loger.log(LogerNazwa.BledyLog, message);
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public UserShowException(String message, Throwable thr) {
		super(message, thr);
		try
		{
			Loger.log(LogerNazwa.BledyLog, message);
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

}
