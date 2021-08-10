package net.minecraft;

import java.sql.Timestamp;

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
}
