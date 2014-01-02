package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class UserShowException extends Exception
{

	private static final long serialVersionUID = -7358344264782353956L;

	public UserShowException(String message) throws IOException {
		super(message);
		writeLog();
	}

	private void writeLog() throws IOException
	{
		FileWriter file = new FileWriter("log//log.txt", true);
		BufferedWriter out = new BufferedWriter(file);
		out.write(MojeUtils.pobierzAktualnaDate() + "--->" + getMessage()
				+ "\n");
		out.close();
		file.close();
	}
}
