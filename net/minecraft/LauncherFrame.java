package net.minecraft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.UIManager;

import net.kronos.mclaunch_util_lib.auth.*;
import net.kronos.mclaunch_util_lib.auth.model.*;

public class LauncherFrame extends Frame {
	public Map<String, String> customParameters = new HashMap<String, String>();
	public static final int VERSION = 13;
	private static final long serialVersionUID = 1L;
	public Launcher launcher;
	public LoginForm loginForm;

	public LauncherFrame() {
		super("Better than Adventure! Launcher");

		setBackground(Color.BLACK);
		this.loginForm = new LoginForm(this);
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(this.loginForm, "Center");

		p.setPreferredSize(new Dimension(854, 480));

		setLayout(new BorderLayout());
		add(p, "Center");

		pack();
		setLocationRelativeTo((Component) null);

		try {
			setIconImage(ImageIO.read(LauncherFrame.class.getResource("favicon.png")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				(new Thread() {
					public void run() {
						try {
							Thread.sleep(30000L);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("FORCING EXIT!");
						System.exit(0);
					}
				}).start();
				if (LauncherFrame.this.launcher != null) {
					LauncherFrame.this.launcher.stop();
					LauncherFrame.this.launcher.destroy();
				}
				System.exit(0);
			}
		});
	}

	public void playCached(String userName) {
		try {
			if (userName == null || userName.length() <= 0) {
				userName = "Player";
			}
			this.launcher = new Launcher();
			this.launcher.customParameters.putAll(this.customParameters);
			this.launcher.customParameters.put("userName", userName);
			this.launcher.init();
			removeAll();
			add(this.launcher, "Center");
			validate();
			this.launcher.start();
			this.loginForm = null;
			setTitle("Better than Adventure!");
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.toString());
		}
	}

	public void login(String userName, String password) {
		YggdrasilRequester req = new YggdrasilRequester();
		YggdrasilAuthenticateRes res = null;
		
		try
		{
			res = req.authenticate(YggdrasilAgent.getMinecraftAgent(), userName, password);
		}
		catch(Exception | YggdrasilError e)
		{
			e.printStackTrace();
			if (e instanceof YggdrasilError)
			{
				showError(((YggdrasilError) e).getErrorMessage());
			}
			return;
		}
		
		this.launcher = new Launcher();
		this.launcher.customParameters.putAll(this.customParameters);
		this.launcher.customParameters.put("userName", res.getSelectedProfile().getName());
		this.launcher.customParameters.put("latestVersion", "0");
		this.launcher.customParameters.put("downloadTicket", "0");
		this.launcher.customParameters.put("sessionId", "token:" + res.getAccessToken() + ":" + res.getSelectedProfile().getId());
		this.launcher.init();

		removeAll();
		add(this.launcher, "Center");
		validate();
		this.launcher.start();
		this.loginForm.loginOk();
		this.loginForm = null;
		setTitle("Better than Adventure!");
	}

	private void showError(String error) {
		removeAll();
		add(this.loginForm);
		this.loginForm.setError(error);
		validate();
	}

	public boolean canPlayOffline(String userName) {
		Launcher launcher = new Launcher();
		launcher.customParameters.putAll(this.customParameters);
		launcher.init(userName, null, null, null);
		return launcher.canPlayOffline();
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception exception) {
		}

		LauncherFrame launcherFrame = new LauncherFrame();
		launcherFrame.setVisible(true);
		launcherFrame.customParameters.put("stand-alone", "true");

		String userName = null;
		String password = null;
		byte b;
		int i;
		String[] arrayOfString;
		for (i = (arrayOfString = args).length, b = 0; b < i;) {
			String argument = arrayOfString[b];
			if (argument.startsWith("-u=") || argument.startsWith("--user=")) {
				userName = getArgValue(argument);
				launcherFrame.customParameters.put("username", userName);
				launcherFrame.loginForm.userName.setText(userName);
			} else if (argument.startsWith("-p=") || argument.startsWith("--password=")) {
				password = getArgValue(argument);
				launcherFrame.customParameters.put("password", password);
				launcherFrame.loginForm.password.setText(password);
			} else if (argument.startsWith("--noupdate")) {
				launcherFrame.customParameters.put("noupdate", "true");
			}
			b++;
		}

		if (args.length >= 3) {
			String ip = args[2];
			String port = "25565";
			if (ip.contains(":")) {
				String[] parts = ip.split(":");
				ip = parts[0];
				port = parts[1];
			}

			launcherFrame.customParameters.put("server", ip);
			launcherFrame.customParameters.put("port", port);
		}
	}

	private static String getArgValue(String argument) {
		int index = argument.indexOf('=');
		if (index < 0) {
			return "";
		}
		return argument.substring(index + 1);
	}
}

/*
 * Location: C:\Users\josep\Downloads\minecraft
 * (1).jar!\net\minecraft\LauncherFrame.class Java compiler version: 5 (49.0)
 * JD-Core Version: 1.1.3
 */