package utils;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.filechooser.FileFilter;

public class FiltrPlikow extends FileFilter
{
	private Hashtable<String, FileFilter> filters = null;

	private String description = null;

	private String fullDescription = null;

	private boolean useExtensionsInDescription = true;

	public FiltrPlikow() {
		this.filters = new Hashtable<String, FileFilter>();
	}

	public FiltrPlikow(String extension) {
		this(extension, null);
	}

	public FiltrPlikow(String extension, String description) {
		this();
		if (extension != null)
			addExtension(extension);
		if (description != null)
			setDescription(description);
	}

	@Override
	public boolean accept(File f)
	{
		if (f != null)
		{
			if (f.isDirectory())
			{
				return true;
			}
			String extension = getExtension(f);
			if (extension != null && filters.get(getExtension(f)) != null)
			{
				return true;
			}
		}
		return false;
	}

	public String getExtension(File f)
	{
		if (f != null)
		{
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1)
			{
				return filename.substring(i + 1).toLowerCase();
			}
		}
		return null;
	}

	public void addExtension(String extension)
	{
		if (filters == null)
		{
			filters = new Hashtable<String, FileFilter>();
		}
		filters.put(extension, this);
		fullDescription = null;
	}

	@Override
	public String getDescription()
	{
		if (fullDescription == null)
		{
			if (description == null || isExtensionListInDescription())
			{
				fullDescription = description == null ? "(" : description
						+ " (";
				Enumeration<String> extensions = filters.keys();
				if (extensions != null)
				{
					fullDescription += "." + extensions.nextElement();
					while (extensions.hasMoreElements())
					{
						fullDescription += ", ." + extensions.nextElement();
					}
				}
				fullDescription += ")";
			} else
			{
				fullDescription = description;
			}
		}
		return fullDescription;
	}

	public void setDescription(String description)
	{
		this.description = description;
		fullDescription = null;
	}

	public void setExtensionListInDescription(boolean b)
	{
		useExtensionsInDescription = b;
		fullDescription = null;
	}

	public boolean isExtensionListInDescription()
	{
		return useExtensionsInDescription;
	}
}