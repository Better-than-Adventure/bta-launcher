package net.minecraft;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;

public class GithubRelease
{
	public class Asset
	{
		public String name;
		public Timestamp created_at;
		public String browser_download_url;
	}
	
	public String name;
	public boolean prerelease;
	public Asset[] assets;
	public String body;
	
	public static GithubRelease[] getReleases()
	{
		Gson gson = new Gson();
		URL url;
		
		try
		{
			url = new URL("https://api.github.com/repos/Joe4422/test-upload-repo-bta/releases");			
		}
		catch (Exception e)
		{
			return null;
		}
		
		Scanner scanner = null;
		String out;
		try
		{
			scanner = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A");
			out = scanner.next();
		}
		catch (Exception e)
		{
			return null;
		}
		finally
		{
			scanner.close();
		}
		
		GithubRelease[] releases = gson.fromJson(out, GithubRelease[].class);
		Arrays.sort(releases, (a, b) -> new Integer(Integer.parseInt(b.body)).compareTo(new Integer(Integer.parseInt(a.body))));

		return releases;
	}
	
	public String toString()
	{
		return this.name;
	}

}
