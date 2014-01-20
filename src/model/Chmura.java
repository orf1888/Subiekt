package model;

import java.util.Locale;

import utils.Globals;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

public class Chmura
{
	private final String APP_KEY;
	private final String APP_SECRET;
	private final DbxAppInfo appInfo;
	private final DbxRequestConfig config;
	private final DbxWebAuthNoRedirect webAuth;

	public Chmura() {
		APP_KEY = "nfpcl8a2t902hhg";
		APP_SECRET = "ehsmi4h4a3eq0cf";
		appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
		config = new DbxRequestConfig(Globals.WersjaAplikacji, Locale
				.getDefault().toString());
		webAuth = new DbxWebAuthNoRedirect(getConfig(), appInfo);
	}

	public DbxWebAuthNoRedirect getWebAuth()
	{
		return webAuth;
	}

	public DbxRequestConfig getConfig()
	{
		return config;
	}

}
