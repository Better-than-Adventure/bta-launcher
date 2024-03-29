package net.minecraft;

import java.applet.Applet;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.SocketPermission;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.*;
import java.util.zip.*;

public class GameUpdater implements Runnable {
	public static final int STATE_INIT = 1;
	public static final int STATE_DETERMINING_PACKAGES = 2;
	public static final int STATE_CHECKING_CACHE = 3;
	public static final int STATE_DOWNLOADING = 4;
	public static final int STATE_EXTRACTING_PACKAGES = 5;
	public static final int STATE_UPDATING_CLASSPATH = 6;
	public static final int STATE_SWITCHING_APPLET = 7;
	public static final int STATE_INITIALIZE_REAL_APPLET = 8;
	public static final int STATE_START_REAL_APPLET = 9;
	public static final int STATE_DONE = 10;
	public int percentage;
	public int currentSizeDownload;
	public int totalSizeDownload;
	public int currentSizeExtract;
	public int totalSizeExtract;
	protected URL[] urlList;
	private static ClassLoader classLoader;
	protected Thread loaderThread;
	protected Thread animationThread;
	public boolean fatalError;
	public String fatalErrorDescription;
	protected String subtaskMessage = "";
	protected int state = 1;

	protected String[] genericErrorMessage = new String[] { "An error occured while loading the applet.",
			"Please contact support to resolve this issue.", "<placeholder for error message>" };

	protected boolean certificateRefused;

	protected String[] certificateRefusedMessage = new String[] { "Permissions for Applet Refused.",
			"Please accept the permissions dialog to allow", "the applet to continue the loading process." };

	protected static boolean natives_loaded = false;

	public static boolean forceUpdate = false;
	private String latestVersion;
	private String mainGameUrl;
	public boolean pauseAskUpdate;
	public boolean shouldUpdate;
	public boolean skipUpdate;

	public GameUpdater(String latestVersion, String mainGameUrl, boolean skipUpdate) {
		this.latestVersion = MinecraftLauncher.selectedRelease.body;
		this.mainGameUrl = mainGameUrl;
		this.skipUpdate = skipUpdate;
	}

	public void init() {
		this.state = 1;

	}

	private String generateStacktrace(Exception exception) {
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		exception.printStackTrace(printWriter);
		return result.toString();
	}

	protected String getDescriptionForState() {
		switch (this.state) {
		case 1:
			return "Initializing loader";
		case 2:
			return "Determining packages to load";
		case 3:
			return "Checking cache for existing files";
		case 4:
			return "Downloading packages";
		case 5:
			return "Extracting downloaded packages";
		case 6:
			return "Updating classpath";
		case 7:
			return "Switching applet";
		case 8:
			return "Initializing real applet";
		case 9:
			return "Starting real applet";
		case 10:
			return "Done loading";
		}
		return "unknown state";
	}

	protected String trimExtensionByCapabilities(String file) {
		return file;
	}

	protected void loadJarURLs() throws Exception {
		this.state = 2;
		String[] jarList = new String[7];
		jarList[0] = "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl/2.9.0/lwjgl-2.9.0.jar";
		jarList[1] = "https://libraries.minecraft.net/net/java/jinput/jinput/2.0.5/jinput-2.0.5.jar";
		jarList[2] = "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl_util/2.9.0/lwjgl_util-2.9.0.jar";
		jarList[3] = MinecraftLauncher.selectedRelease.assets[0].browser_download_url;
		jarList[4] = "https://launcher.mojang.com/v1/objects/43db9b498cb67058d2e12d394e6507722e71bb45/client.jar";

		this.urlList = new URL[jarList.length];

		String osName = System.getProperty("os.name");

		if (osName.startsWith("Win"))
		{
			jarList[5] = "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.0/lwjgl-platform-2.9.0-natives-windows.jar";
			jarList[6] = "https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-windows.jar";
		} 
		else if (osName.startsWith("Linux"))
		{
			jarList[5] = "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.0/lwjgl-platform-2.9.0-natives-linux.jar";
			jarList[6] = "https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-linux.jar";
		} 
		else if (osName.startsWith("Mac"))
		{
			jarList[5] = "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.0/lwjgl-platform-2.9.0-natives-osx.jar";
			jarList[6] = "https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-osx.jar";
		} 
		else 
		{
			fatalErrorOccured("OS (" + osName + ") not supported", null);
		}
		
		for (int i = 0; i < jarList.length; i++) {
			this.urlList[i] = new URL(jarList[i]);
		}
	}

	public void run() {
		init();
		this.state = 3;

		this.percentage = 5;

		try {
			loadJarURLs();

			String path = AccessController.<String>doPrivileged(new PrivilegedExceptionAction<String>() {
				public String run() throws Exception {
					return Util.getWorkingDirectory() + File.separator + "bin" + File.separator;
				}
			});

			File dir = new File(path);

			if (!dir.exists()) {
				dir.mkdirs();
			}

			if (this.latestVersion != null) {
				File versionFile = new File(dir, "version");

				boolean cacheAvailable = false;
				if (!this.skipUpdate && !forceUpdate && versionFile.exists() && (this.latestVersion.equals("-1")
						|| this.latestVersion.equals(readVersionFile(versionFile)))) {
					cacheAvailable = true;
					this.percentage = 90;
				}

				if (!this.skipUpdate && (forceUpdate || !cacheAvailable)) {
					this.shouldUpdate = true;
					if (!forceUpdate && versionFile.exists()) {

						checkShouldUpdate();
					}
					if (this.shouldUpdate) {

						writeVersionFile(versionFile, "");

						downloadJars(path);
						extractJars(path);
						for (int i = 0; i < urlList.length; i++)
						{
							if (urlList[i].toString().contains("natives"))
							{
								extractNatives(path, urlList[i]);
							}
						}

						if (this.latestVersion != null) {
							this.percentage = 90;
							writeVersionFile(versionFile, this.latestVersion);
						}
					} else {
						cacheAvailable = true;
						this.percentage = 90;
					}
				}
			}

			updateClassPath(dir);
			this.state = 10;
		} catch (AccessControlException ace) {
			fatalErrorOccured(ace.getMessage(), ace);
			this.certificateRefused = true;
		} catch (Exception e) {
			fatalErrorOccured(e.getMessage(), e);
		} finally {
			this.loaderThread = null;
		}
	}

	private void checkShouldUpdate() {
	    this.pauseAskUpdate = true;
	    while (this.pauseAskUpdate) {
	      try {
	        Thread.sleep(1000L);
	      } catch (InterruptedException e) {
	        e.printStackTrace();
	      } 
	    } 
	}

	protected String readVersionFile(File file) throws Exception {
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		String version = dis.readUTF();
		dis.close();
		return version;
	}

	protected void writeVersionFile(File file, String version) throws Exception {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
		dos.writeUTF(version);
		dos.close();
	}

	protected void updateClassPath(File dir) throws Exception {
		this.state = 6;

		this.percentage = 95;
		
		URL[] urls = {
				new File(dir, "lwjgl.jar").toURI().toURL(),
				new File(dir, "jinput.jar").toURI().toURL(),
				new File(dir, "lwjgl_util.jar").toURI().toURL(),
				new File(dir, "bta.jar").toURI().toURL(),
				new File(dir, "minecraft.jar").toURI().toURL()
		};

		if (classLoader == null) {
			classLoader = new URLClassLoader(urls) {
				protected PermissionCollection getPermissions(CodeSource codesource) {
					PermissionCollection perms = null;

					try {
						Method method = SecureClassLoader.class.getDeclaredMethod("getPermissions",
								new Class[] { CodeSource.class });
						method.setAccessible(true);
						perms = (PermissionCollection) method.invoke(getClass().getClassLoader(),
								new Object[] { codesource });

						String host = "www.minecraft.net";

						if (host != null && host.length() > 0)

						{
							perms.add(new SocketPermission(host, "connect,accept"));
						} else {
							codesource.getLocation().getProtocol().equals("file");
						}

						perms.add(new FilePermission("<<ALL FILES>>", "read"));
					} catch (Exception e) {
						e.printStackTrace();
					}

					return perms;
				}
			};
		}

		String path = dir.getAbsolutePath();
		if (!path.endsWith(File.separator))
			path = String.valueOf(path) + File.separator;
		unloadNatives(path);

		System.setProperty("org.lwjgl.librarypath", String.valueOf(path) + "natives");
		System.setProperty("net.java.games.input.librarypath", String.valueOf(path) + "natives");

		natives_loaded = true;
	}

	private void unloadNatives(String nativePath) {
		if (!natives_loaded) {
			return;
		}

		try {
			Field field = ClassLoader.class.getDeclaredField("loadedLibraryNames");
			field.setAccessible(true);
			Vector<String> libs = (Vector<String>) field.get(getClass().getClassLoader());

			String path = (new File(nativePath)).getCanonicalPath();

			for (int i = 0; i < libs.size(); i++) {
				String s = libs.get(i);

				if (s.startsWith(path)) {
					libs.remove(i);
					i--;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Applet createApplet() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<Applet> appletClass = (Class) classLoader.loadClass("net.minecraft.client.MinecraftApplet");
		return appletClass.newInstance();
	}

	public Runnable createMinecraft() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class minecraftClass = (Class) classLoader.loadClass("net.minecraft.client.Minecraft");
		return (Runnable) minecraftClass.newInstance();
	}

	protected void downloadJars(String path) throws Exception {
		File versionFile = new File(path, "md5s");
		Properties md5s = new Properties();
		if (versionFile.exists()) {
			try {
				FileInputStream fis = new FileInputStream(versionFile);
				md5s.load(fis);
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.state = 4;

		int[] fileSizes = new int[this.urlList.length];
		boolean[] skip = new boolean[this.urlList.length];

		for (int i = 0; i < this.urlList.length; i++) {
			URL url = urlList[i];
			URLConnection urlconnection = url.openConnection();
			urlconnection.setDefaultUseCaches(false);
			skip[i] = false;
			if (urlconnection instanceof HttpURLConnection) {
				((HttpURLConnection) urlconnection).setRequestMethod("HEAD");

				String etagOnDisk = "\"" + md5s.getProperty(getFileName(url)) + "\"";

				if (!forceUpdate && etagOnDisk != null)
					urlconnection.setRequestProperty("If-None-Match", etagOnDisk);

				int code = ((HttpURLConnection) urlconnection).getResponseCode();
				if (code / 100 == 3) {
					skip[i] = true;
				}
			}
			fileSizes[i] = urlconnection.getContentLength();
			this.totalSizeDownload += fileSizes[i];
		}

		int initialPercentage = this.percentage = 10;

		byte[] buffer = new byte[65536];
		for (int j = 0; j < this.urlList.length; j++) {
			URL url = urlList[j];

			if (skip[j]) {
				this.percentage = initialPercentage + fileSizes[j] * 45 / this.totalSizeDownload;
			} else {

				try {

					md5s.remove(getFileName(url));
					md5s.store(new FileOutputStream(versionFile), "md5 hashes for downloaded files");
				} catch (Exception e) {
					e.printStackTrace();
				}

				int unsuccessfulAttempts = 0;
				int maxUnsuccessfulAttempts = 3;
				boolean downloadFile = true;

				while (downloadFile) {
					downloadFile = false;

					URLConnection urlconnection = url.openConnection();

					String etag = "";

					if (urlconnection instanceof HttpURLConnection) {
						urlconnection.setRequestProperty("Cache-Control", "no-cache");

						urlconnection.connect();

						// etag = urlconnection.getHeaderField("ETag");
						// etag = etag.substring(1, etag.length() - 1);
					}

					String currentFile = getFileName(url);
					InputStream inputstream = getJarInputStream(currentFile, urlconnection);
					FileOutputStream fos = new FileOutputStream(String.valueOf(path) + currentFile);

					long downloadStartTime = System.currentTimeMillis();
					int downloadedAmount = 0;
					int fileSize = 0;
					String downloadSpeedMessage = "";

					// MessageDigest m = MessageDigest.getInstance("MD5");
					int bufferSize;
					while ((bufferSize = inputstream.read(buffer, 0, buffer.length)) != -1) {
						fos.write(buffer, 0, bufferSize);
						// m.update(buffer, 0, bufferSize);
						this.currentSizeDownload += bufferSize;
						fileSize += bufferSize;
						this.percentage = initialPercentage + this.currentSizeDownload * 45 / this.totalSizeDownload;
						this.subtaskMessage = "Retrieving: " + currentFile + " "
								+ (this.currentSizeDownload * 100 / this.totalSizeDownload) + "%";

						downloadedAmount += bufferSize;
						long timeLapse = System.currentTimeMillis() - downloadStartTime;

						if (timeLapse >= 1000L) {
							float downloadSpeed = downloadedAmount / (float) timeLapse;
							downloadSpeed = (int) (downloadSpeed * 100.0F) / 100.0F;
							downloadSpeedMessage = " @ " + downloadSpeed + " KB/sec";
							downloadedAmount = 0;
							downloadStartTime += 1000L;
						}

						this.subtaskMessage = String.valueOf(this.subtaskMessage) + downloadSpeedMessage;
					}

					inputstream.close();
					fos.close();
//          String md5 = (new BigInteger(1, m.digest())).toString(16);
//          while (md5.length() < 32) {
//            md5 = "0" + md5;
//          }
					boolean md5Matches = true;
//          if (etag != null) {
//            md5Matches = md5.equals(etag);
//          }

					if (urlconnection instanceof HttpURLConnection) {
						if (md5Matches && (fileSize == fileSizes[j] || fileSizes[j] <= 0)) {

							try {
								md5s.setProperty(getFileName(this.urlList[j]), etag);
								md5s.store(new FileOutputStream(versionFile), "md5 hashes for downloaded files");
							} catch (Exception e) {
								e.printStackTrace();
							}
							continue;
						}
						unsuccessfulAttempts++;
						if (unsuccessfulAttempts < maxUnsuccessfulAttempts) {
							downloadFile = true;
							this.currentSizeDownload -= fileSize;
							continue;
						}
						throw new Exception("failed to download " + currentFile);
					}
				}
			}
		}

		this.subtaskMessage = "";
	}

	protected InputStream getJarInputStream(String currentFile, final URLConnection urlconnection) throws Exception {
		final InputStream[] is = new InputStream[1];

		for (int j = 0; j < 3 && is[0] == null; j++) {
			Thread t = new Thread() {
				public void run() {
					try {
						is[0] = urlconnection.getInputStream();
					} catch (IOException iOException) {
					}
				}
			};

			t.setName("JarInputStreamThread");
			t.start();

			int iterationCount = 0;
			while (is[0] == null && iterationCount++ < 5) {
				try {
					t.join(1000L);
				} catch (InterruptedException interruptedException) {
				}
			}

			if (is[0] == null) {
				try {
					t.interrupt();
					t.join();
				} catch (InterruptedException interruptedException) {
				}
			}
		}

		if (is[0] == null) {
			if (currentFile.equals("minecraft.jar")) {
				throw new Exception("Unable to download " + currentFile);
			}
			throw new Exception("Unable to download " + currentFile);
		}

		return is[0];
	}

	protected void extractZip(String in, String out) throws Exception {
		File f = new File(in);
		if (!f.exists())
			return;

		FileInputStream fileInputHandle = new FileInputStream(f);

		ZipInputStream zis = new ZipInputStream(fileInputHandle);
		ZipEntry zipEntry = zis.getNextEntry();
		zipEntry = zis.getNextEntry();

		byte[] buffer = new byte[(int) zipEntry.getSize()];
		FileOutputStream fos = new FileOutputStream(out);
		int len;
		while ((len = zis.read(buffer)) > 0) {
			fos.write(buffer, 0, len);
		}
		fos.close();
		zis.closeEntry();
		zis.close();

		f.delete();
	}

	protected void deleteMetaInf(String in)
	{

		Path zip_path = Paths.get(in);

		try {
			try (FileSystem zipfs = FileSystems.newFileSystem(zip_path, null)) {
				Path path = zipfs.getPath("META-INF/MANIFEST.MF");
				Files.deleteIfExists(path);
				path = zipfs.getPath("META-INF/MOJANG_C.DSA");
				Files.deleteIfExists(path);
				path = zipfs.getPath("META-INF/MOJANG_C.SF");
				Files.deleteIfExists(path);
				path = zipfs.getPath("META-INF");
				Files.deleteIfExists(path);
			}			
		}
		catch (Exception e)
		{
			return;
		}
	}

	protected void extractJars(String path) throws Exception {
		this.state = 5;

		float increment = 10.0F / this.urlList.length;

		for (int i = 0; i < this.urlList.length; i++) {
			this.percentage = 55 + (int) (increment * (i + 1));
			String filename = getFileName(this.urlList[i]);

			if (filename.endsWith("client.jar"))
			{
				new File(String.valueOf(path) + "client.jar").renameTo(new File(String.valueOf(path) + "minecraft.jar"));
				deleteMetaInf(String.valueOf(path) + "minecraft.jar");
			}
			else if (filename.endsWith("jinput-2.0.5.jar"))
			{
				new File(String.valueOf(path) + "jinput-2.0.5.jar").renameTo(new File(String.valueOf(path) + "jinput.jar"));
			}
			else if (filename.endsWith("lwjgl_util-2.9.0.jar"))
			{
				new File(String.valueOf(path) + "lwjgl_util-2.9.0.jar").renameTo(new File(String.valueOf(path) + "lwjgl_util.jar"));
			}
			else if (filename.endsWith("lwjgl-2.9.0.jar"))
			{
				new File(String.valueOf(path) + "lwjgl-2.9.0.jar").renameTo(new File(String.valueOf(path) + "lwjgl.jar"));
			}
		}
	}

	protected void extractNatives(String path, URL jarPath) throws Exception {
		this.state = 5;

		int initialPercentage = this.percentage;

		String nativeJar = getJarName(jarPath);

		Certificate[] certificate = Launcher.class.getProtectionDomain().getCodeSource().getCertificates();

		if (certificate == null) {
			URL location = Launcher.class.getProtectionDomain().getCodeSource().getLocation();

			JarURLConnection jurl = (JarURLConnection) (new URL(
					"jar:" + location.toString() + "!/net/minecraft/Launcher.class")).openConnection();
			jurl.setDefaultUseCaches(true);
			try {
				certificate = jurl.getCertificates();
			} catch (Exception exception) {
			}
		}

		File nativeFolder = new File(String.valueOf(path) + "natives");
		if (!nativeFolder.exists()) {
			nativeFolder.mkdir();
		}

		File file = new File(String.valueOf(path) + nativeJar);
		if (!file.exists())
			return;
		JarFile jarFile = new JarFile(file, true);
		Enumeration<JarEntry> entities = jarFile.entries();

		this.totalSizeExtract = 0;

		while (entities.hasMoreElements()) {
			JarEntry entry = entities.nextElement();

			if (entry.isDirectory() || entry.getName().indexOf('/') != -1) {
				continue;
			}
			this.totalSizeExtract = (int) (this.totalSizeExtract + entry.getSize());
		}

		this.currentSizeExtract = 0;

		entities = jarFile.entries();

		while (entities.hasMoreElements()) {
			JarEntry entry = entities.nextElement();

			if (entry.isDirectory() || entry.getName().indexOf('/') != -1) {
				continue;
			}

			File file1 = new File(String.valueOf(path) + "natives" + File.separator + entry.getName());
			if (file1.exists() && !file1.delete()) {
				continue;
			}

			InputStream in = jarFile.getInputStream(jarFile.getEntry(entry.getName()));
			OutputStream out = new FileOutputStream(
					String.valueOf(path) + "natives" + File.separator + entry.getName());

			byte[] buffer = new byte[65536];
			int bufferSize;
			while ((bufferSize = in.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, bufferSize);
				this.currentSizeExtract += bufferSize;

				this.percentage = initialPercentage + this.currentSizeExtract * 20 / this.totalSizeExtract;
				this.subtaskMessage = "Extracting: " + entry.getName() + " "
						+ (this.currentSizeExtract * 100 / this.totalSizeExtract) + "%";
			}

			validateCertificateChain(certificate, entry.getCertificates());

			in.close();
			out.close();
		}
		this.subtaskMessage = "";

		jarFile.close();

		File f = new File(String.valueOf(path) + nativeJar);
		f.delete();
	}

	protected static void validateCertificateChain(Certificate[] ownCerts, Certificate[] native_certs)
			throws Exception {
		if (ownCerts == null)
			return;
		if (native_certs == null)
			throw new Exception(
					"Unable to validate certificate chain. Native entry did not have a certificate chain at all");

		if (ownCerts.length != native_certs.length)
			throw new Exception("Unable to validate certificate chain. Chain differs in length [" + ownCerts.length
					+ " vs " + native_certs.length + "]");

		for (int i = 0; i < ownCerts.length; i++) {
			if (!ownCerts[i].equals(native_certs[i])) {
				throw new Exception("Certificate mismatch: " + ownCerts[i] + " != " + native_certs[i]);
			}
		}
	}

	protected String getJarName(URL url) {
		String fileName = url.getFile();

		if (fileName.contains("?")) {
			fileName = fileName.substring(0, fileName.indexOf("?"));
		}

		return fileName.substring(fileName.lastIndexOf('/') + 1);
	}

	protected String getFileName(URL url) {
		String fileName = url.getFile();
		if (fileName.contains("?")) {
			fileName = fileName.substring(0, fileName.indexOf("?"));
		}
		return fileName.substring(fileName.lastIndexOf('/') + 1);
	}

	protected void fatalErrorOccured(String error, Exception e) {
		e.printStackTrace();
		this.fatalError = true;
		this.fatalErrorDescription = "Fatal error occured (" + this.state + "): " + error;
		System.out.println(this.fatalErrorDescription);

		System.out.println(generateStacktrace(e));
	}

	public boolean canPlayOffline() {
		try {
			String path = AccessController.<String>doPrivileged(new PrivilegedExceptionAction<String>() {
				public String run() throws Exception {
					return Util.getWorkingDirectory() + File.separator + "bin" + File.separator;
				}
			});

			File dir = new File(path);
			if (!dir.exists())
				return false;

			dir = new File(dir, "version");
			if (!dir.exists())
				return false;

			if (dir.exists()) {
				String version = readVersionFile(dir);
				if (version != null && version.length() > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
}

/*
 * Location: C:\Users\josep\Downloads\minecraft
 * (1).jar!\net\minecraft\GameUpdater.class Java compiler version: 5 (49.0)
 * JD-Core Version: 1.1.3
 */