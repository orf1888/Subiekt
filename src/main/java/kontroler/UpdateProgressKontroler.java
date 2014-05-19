package kontroler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import utils.Globals;
import widok.UpdateProgress;

public class UpdateProgressKontroler extends Thread
{
	private final UpdateProgress up;

	public UpdateProgressKontroler(UpdateProgress up) {
		this.up = up;
	}

	@Override
	public void run()
	{
		try
		{
			String[] cmdLine = new String[]
			{ "cmd", "/c", Globals.UpdateBat };
			System.err.println(cmdLine[2]);
			ProcessBuilder builder = new ProcessBuilder(cmdLine);
			builder.redirectError(new File("nul"));
			Process child = builder.start();
			child.getOutputStream().close();

			Reader reader = new InputStreamReader(child.getInputStream());
			try (BufferedReader bufferedReader = new BufferedReader(reader))
			{
				int i = 0;
				String line;
				while ((line = bufferedReader.readLine()) != null)
				{

					System.err.println(++i + " " + line);
					up.setProgress(i);
					if (line.contains("\\bin\\git\" pull"))
					{
						up.setLblStatus("Update repozytorium...");
					}
					if (line.contains("Scanning for projects..."))
					{
						up.setLblStatus("Kompilacja Subiekt GTC...");
					}
					if (line.contains("T E S T S"))
					{
						up.setLblStatus("Uruchamainie testów...");
					}
					if (line.contains("BUILD SUCCESS"))
					{
						up.setLblStatus("Ponowne uruchomienie...");
					}
					if (line.contains("Koniec!"))
					{
						reader.close();
						System.exit(0);
					}

				}
			}

			int exitStatus;
			try
			{
				exitStatus = child.waitFor();
			} catch (InterruptedException e)
			{
				throw new IOException(e);
			}
			if (exitStatus != 0)
				throw new IOException("rpm failed with exit status "
						+ exitStatus);
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}

	}
}
