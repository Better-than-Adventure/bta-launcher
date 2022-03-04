package net.minecraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import com.google.gson.Gson;

public class Options
{
	public boolean prerelease;
	public String versionOverride;
	public int ramAllocated;
	
	public Options()
	{
		prerelease = false;
		versionOverride = "";
		ramAllocated = 4096;
	}
	
	public static Options readOptions()
	{
		Gson gson = new Gson();
		File file = new File(Util.getWorkingDirectory() + "/launcher.json");
		boolean generateFile = true;
		Options options = null;
		
		if (file.exists())
		{
			try {
				options = gson.fromJson(readStream(new FileInputStream(file)), Options.class);
				generateFile = false;
			}
			catch (Exception e) { }
		}
		
		if (generateFile == true)
		{
			options = new Options();
			writeOptions(options);
		}
		
		return options;
	}
	
	public static void writeOptions(Options options)
	{
		Gson gson = new Gson();
		File file = new File(Util.getWorkingDirectory() + "/launcher.json");
		
		try
		{
			if (file.exists())
			{
				file.delete();
			}
			
			file.createNewFile();
			
			try (PrintWriter out = new PrintWriter(Util.getWorkingDirectory() + "/launcher.json")) {
				out.print(gson.toJson(options));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	protected static String readStream(InputStream is) {
	    StringBuilder sb = new StringBuilder(512);
	    try {
	        Reader r = new InputStreamReader(is, "UTF-8");
	        int c = 0;
	        while ((c = r.read()) != -1) {
	            sb.append((char) c);
	        }
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	    return sb.toString();
	}
}
