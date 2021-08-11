package net.minecraft;

public class VersionItem
{
	public String name;
	public GithubRelease release;
	
	public VersionItem(GithubRelease release)
	{
		this.release = release;
		this.name = release.name;
	}
	
	protected VersionItem(String name)
	{
		this.name = name;
		this.release = null;
	}
	
	public String toString()
	{
		return this.name;
	}
	
	public static VersionItem latestStable = new VersionItem("Latest stable version");
	public static VersionItem latestPrerelease = new VersionItem("Latest snapshot or prerelease");
}
