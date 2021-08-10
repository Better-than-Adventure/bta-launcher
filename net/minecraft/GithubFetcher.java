package net.minecraft;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;

public class GithubFetcher
{
	public static GithubRelease getLatestRelease(boolean prerelease)
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
		Arrays.sort(releases, (a, b) -> b.body.compareTo(a.body));
		
		for (int i = 0; i < releases.length; i++)
		{
			if (prerelease == true || releases[i].prerelease == prerelease)
			{
				return releases[i];
			}
		}

		return null;
	}
}
