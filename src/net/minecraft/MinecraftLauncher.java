package net.minecraft;

public class MinecraftLauncher {
	public static Options options;
	public static GithubRelease[] releases;
	public static GithubRelease selectedRelease = null;

	public static void main(String[] args) throws Exception {

		// Load options
		options = Options.readOptions();

		// Load releases
		releases = GithubRelease.getReleases();

		// Determine release to use
		updateSelectedRelease();

		LauncherFrame.main(args);
	}

	public static void updateSelectedRelease()
	{
		if (!options.versionOverride.equals(""))
		{
			for (int i = 0; i < releases.length; i++)
			{
				if (releases[i].body.equals(options.versionOverride))
				{
					selectedRelease = releases[i];
					return;
				}	
			}
		}
		for (int i = 0; i < releases.length; i++)
		{
			if (releases[i].prerelease == options.prerelease)
			{
				selectedRelease = releases[i];
				break;
			}
		}
	}
}

/*
 * Location: C:\Users\josep\Downloads\minecraft
 * (1).jar!\net\minecraft\MinecraftLauncher.class Java compiler version: 5
 * (49.0) JD-Core Version: 1.1.3
 */