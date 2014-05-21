package kontroler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import utils.Globals;
import utils.MojeUtils;
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
		int i = 0, exitStatus;
		try
		{
			/* Update repository */
			String[] cmdLineGitPull = new String[]
			{ "cmd", "/c", Globals.gitPullBat };
			ProcessBuilder builder = new ProcessBuilder(cmdLineGitPull);
			builder.redirectError(new File("nul"));
			Process child = builder.start();
			child.getOutputStream().close();

			Reader reader = new InputStreamReader(child.getInputStream());
			try (BufferedReader bufferedReader = new BufferedReader(reader))
			{
				String line;
				while ((line = bufferedReader.readLine()) != null)
				{

					System.err.println(++i + " " + line);
					up.setProgress(i);
					if (line.contains("\\bin\\git\" pull"))
					{
						up.setLblStatus("Update repozytorium...");
					}
					if (line.contains("Already up-to-date."))
					{
						up.dispose();
						reader.close();
						MojeUtils
								.showMsg("Dana instatncja \"Subiekt GTC\" jest aktualna!");
						return;
					}

				}
				reader.close();
			}
			try
			{
				exitStatus = child.waitFor();
			} catch (InterruptedException e)
			{
				throw new IOException(e);
			}
			if (exitStatus != 0)
				throw new IOException("Błąd update'u repozytorium! "
						+ exitStatus);

			/* Maven verify */
			String[] cmdLineMvnVerify = new String[]
			{ "cmd", "/c", Globals.mavenVerifyBat };
			builder = new ProcessBuilder(cmdLineMvnVerify);
			builder.redirectError(new File("nul"));
			child = builder.start();
			child.getOutputStream().close();
			reader = new InputStreamReader(child.getInputStream());
			try (BufferedReader bufferedReader = new BufferedReader(reader))
			{
				String line;
				while ((line = bufferedReader.readLine()) != null)
				{

					System.err.println(++i + " " + line);
					up.setProgress(i);
					if (line.contains("Scanning for projects..."))
					{
						up.setLblStatus("Kompilacja \"Subiekt GTC\"...");
					}
					if (line.contains("T E S T S"))
					{
						up.setLblStatus("Uruchamainie testów...");
					}
					if (line.contains("BUILD SUCCESS"))
					{
						up.setLblStatus("Ponowne uruchomienie...");
					}
					if (line.contains("BUILD FAILURE"))
					{
						up.dispose();
						reader.close();
						MojeUtils
								.showMsg("Błąd budowania \"Subiekt GTC\".\nTen problem należy niezwłocznie zgłosić!");
						return;
					}

				}
				reader.close();
			}
			try
			{
				exitStatus = child.waitFor();
			} catch (InterruptedException e)
			{
				throw new IOException(e);
			}
			if (exitStatus != 0)
				throw new IOException("Błąd budowania! " + exitStatus);

			/* Replace Subiekt GTC instace */
			String[] cmdLineReplace = new String[]
			{ "cmd", "/c", Globals.replaceBat };
			builder = new ProcessBuilder(cmdLineReplace);
			builder.redirectError(new File("nul"));
			child = builder.start();
			child.getOutputStream().close();

			System.exit(0);

			try
			{
				exitStatus = child.waitFor();
			} catch (InterruptedException e)
			{
				throw new IOException(e);
			}
			if (exitStatus != 0)
				throw new IOException("Błąd podmiany! " + exitStatus);

		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}

	}
}
