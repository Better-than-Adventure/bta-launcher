package net.minecraft;

import java.io.File;
import java.net.URI;

public class Util
{
  private enum OS {
    linux, solaris, windows, macos, unknown;
  }
  
  private static File workDir = null;
  
  public static File getWorkingDirectory() {
    if (workDir == null) workDir = getWorkingDirectory("minecraft-bta"); 
    return workDir;
  }
  public static File getWorkingDirectory(String applicationName) {
    File workingDirectory;
    String applicationData, userHome = System.getProperty("user.home", ".");
    
    switch (getPlatform()) {
      case solaris:
        workingDirectory = new File(userHome, String.valueOf('.') + applicationName + '/');
        break;
      case windows:
        applicationData = System.getenv("APPDATA");
        if (applicationData != null) { workingDirectory = new File(applicationData, "." + applicationName + '/'); break; }
         workingDirectory = new File(userHome, String.valueOf('.') + applicationName + '/');
        break;
      case macos:
        workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
        break;
      default:
        workingDirectory = new File(userHome, String.valueOf(applicationName) + '/'); break;
    } 
    if (!workingDirectory.exists() && !workingDirectory.mkdirs()) throw new RuntimeException("The working directory could not be created: " + workingDirectory); 
    return workingDirectory;
  }
  
  private static OS getPlatform() {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("win")) return OS.windows; 
    if (osName.contains("mac")) return OS.macos; 
    if (osName.contains("solaris")) return OS.solaris; 
    if (osName.contains("sunos")) return OS.solaris; 
    if (osName.contains("linux")) return OS.linux; 
    if (osName.contains("unix")) return OS.linux; 
    return OS.unknown;
  }
  
  public static boolean isEmpty(String str) {
    return !(str != null && str.length() != 0);
  }
  
  public static void openLink(URI uri) {
    try {
      Object o = Class.forName("java.awt.Desktop").getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
      o.getClass().getMethod("browse", new Class[] { URI.class }).invoke(o, new Object[] { uri });
    } catch (Throwable e) {
      System.out.println("Failed to open link " + uri.toString());
    } 
  }
}


/* Location:              C:\Users\josep\Downloads\minecraft (1).jar!\net\minecraft\Util.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */