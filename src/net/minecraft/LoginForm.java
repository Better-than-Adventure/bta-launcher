package net.minecraft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class LoginForm
  extends TransparentPanel {
  private static final int PANEL_SIZE = 100;
  private static final long serialVersionUID = 1L;
  private static final Color LINK_COLOR = new Color(8421631);
  
  public JTextField userName = new JTextField(20);
  public JPasswordField password = new JPasswordField(20);
  private TransparentCheckbox rememberBox = new TransparentCheckbox("Remember password");
  private TransparentButton launchButton = new TransparentButton("Login");
  private TransparentButton optionsButton = new TransparentButton("Options");
  private TransparentButton retryButton = new TransparentButton("Try again");
  private TransparentButton offlineButton = new TransparentButton("Play offline");
  private TransparentLabel errorLabel = new TransparentLabel("", 0);
  
  private LauncherFrame launcherFrame;
  
  public LoginForm(final LauncherFrame launcherFrame) {
    this.launcherFrame = launcherFrame;








    
    BorderLayout gbl = new BorderLayout();
    setLayout(gbl);
    
    add(buildMainLoginPanel(), "Center");
    
    readUsername();
    
    ActionListener al = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          LoginForm.this.doLogin();
        }
      };
    
    this.userName.addActionListener(al);
    this.password.addActionListener(al);
    
    this.retryButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
            LoginForm.this.errorLabel.setText("");
            LoginForm.this.removeAll();
            LoginForm.this.add(LoginForm.this.buildMainLoginPanel(), "Center");
            LoginForm.this.validate();
          }
        });
    
    this.offlineButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
            launcherFrame.playCached(LoginForm.this.userName.getText());
          }
        });
    
    this.launchButton.addActionListener(al);
    
    this.optionsButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
            (new OptionsPanel(launcherFrame)).setVisible(true);
          }
        });
  }
  private boolean outdated = false; private JScrollPane scrollPane;
  
  public void doLogin() {
    setLoggingIn();
    (new Thread() {
        public void run() {
          try {
            LoginForm.this.launcherFrame.login(LoginForm.this.userName.getText(), new String(LoginForm.this.password.getPassword()));
          } catch (Exception e) {
            LoginForm.this.setError(e.toString());
          } 
        }
      }).start();
  }
  private void readUsername() {
    try {
      DataInputStream dis;
      File lastLogin = new File(Util.getWorkingDirectory(), "lastlogin");
      
      Cipher cipher = getCipher(2, "passwordfile");
      if (cipher != null) {
        dis = new DataInputStream(new CipherInputStream(new FileInputStream(lastLogin), cipher));
      } else {
        dis = new DataInputStream(new FileInputStream(lastLogin));
      } 
      this.userName.setText(dis.readUTF());
      this.password.setText(dis.readUTF());
      this.rememberBox.setSelected(((this.password.getPassword()).length > 0));
      dis.close();
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  private void writeUsername() {
    try {
      DataOutputStream dos;
      File lastLogin = new File(Util.getWorkingDirectory(), "lastlogin");
      
      Cipher cipher = getCipher(1, "passwordfile");
      if (cipher != null) {
        dos = new DataOutputStream(new CipherOutputStream(new FileOutputStream(lastLogin), cipher));
      } else {
        dos = new DataOutputStream(new FileOutputStream(lastLogin));
      } 
      dos.writeUTF(this.userName.getText());
      dos.writeUTF(this.rememberBox.isSelected() ? new String(this.password.getPassword()) : "");
      dos.close();
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  private Cipher getCipher(int mode, String password) throws Exception {
    Random random = new Random(43287234L);
    byte[] salt = new byte[8];
    random.nextBytes(salt);
    PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);
    
    SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray()));
    Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
    cipher.init(mode, pbeKey, pbeParamSpec);
    return cipher;
  }


  
  private JScrollPane getUpdateNews() {
    if (this.scrollPane != null) return this.scrollPane;
    
    try {
      final JTextPane editorPane = new JTextPane() {
          private static final long serialVersionUID = 1L;
        };
      editorPane.setText("<html><body><font color=\"#808080\"><br><br><br><br><br><br><br><center>Loading update news..</center></font></body></html>");
      editorPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent he) {
              if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                  Util.openLink(he.getURL().toURI());
                } catch (Exception e) {
                  e.printStackTrace();
                } 
              }
            }
          });
      (new Thread() {
          public void run() {
            try {
              editorPane.setPage(new URL("https://betterthanadventure.tumblr.com/"));
              editorPane.getDocument().putProperty("ZOOM_FACTOR", new Double(2.5));
            } catch (Exception e) {
              e.printStackTrace();
              editorPane.setText("<html><body><font color=\"#808080\"><br><br><br><br><br><br><br><center>Failed to update news<br>" + e.toString() + "</center></font></body></html>");
            } 
          }
        }).start();
      editorPane.setBackground(Color.DARK_GRAY);
      editorPane.setEditable(false);
      this.scrollPane = new JScrollPane(editorPane);
      this.scrollPane.setBorder((Border)null);
      editorPane.setMargin((Insets)null);
      
      this.scrollPane.setBorder(new MatteBorder(0, 0, 2, 0, Color.BLACK));
    } catch (Exception e2) {
      e2.printStackTrace();
    } 
    
    return this.scrollPane;
  }
  
  private JPanel buildMainLoginPanel() {
    JPanel p = new TransparentPanel(new BorderLayout());
    p.add(getUpdateNews(), "Center");
    
    JPanel southPanel = new TexturedPanel();
    southPanel.setLayout(new BorderLayout());
    southPanel.add(new LogoPanel(), "West");
    southPanel.add(new TransparentPanel(), "Center");
    southPanel.add(center(buildLoginPanel()), "East");
    southPanel.setPreferredSize(new Dimension(100, 100));
    
    p.add(southPanel, "South");
    return p;
  }
  
  private JPanel buildLoginPanel() {
    TransparentPanel panel = new TransparentPanel();
    panel.setInsets(4, 0, 4, 0);
    
    BorderLayout layout = new BorderLayout();
    layout.setHgap(0);
    layout.setVgap(8);
    panel.setLayout(layout);

    
    GridLayout gl1 = new GridLayout(0, 1);
    gl1.setVgap(2);
    GridLayout gl2 = new GridLayout(0, 1);
    gl2.setVgap(2);
    GridLayout gl3 = new GridLayout(0, 1);
    gl3.setVgap(2);
    
    TransparentPanel titles = new TransparentPanel(gl1);
    TransparentPanel values = new TransparentPanel(gl2);
    
    titles.add(new TransparentLabel("Username:", 4));
    titles.add(new TransparentLabel("Password:", 4));
    titles.add(new TransparentLabel("", 4));

    
    values.add(this.userName);
    values.add(this.password);
    values.add(this.rememberBox);
    
    panel.add(titles, "West");
    panel.add(values, "Center");
    
    TransparentPanel loginPanel = new TransparentPanel(new BorderLayout());
    
    TransparentPanel third = new TransparentPanel(gl3);
    titles.setInsets(0, 0, 0, 4);
    third.setInsets(0, 10, 0, 10);
    
    third.add(this.optionsButton);
    third.add(this.launchButton);
    
    try {
      if (this.outdated) {
        TransparentLabel accountLink = getUpdateLink();
        third.add(accountLink);
      }
      else {
        
        TransparentLabel accountLink = new TransparentLabel("Need account?") {
            private static final long serialVersionUID = 0L;
            
            public void paint(Graphics g) {
              super.paint(g);
              
              int x = 0;
              int y = 0;


              
              FontMetrics fm = g.getFontMetrics();
              int width = fm.stringWidth(getText());
              int height = fm.getHeight();
              
              if (getAlignmentX() == 2.0F) { x = 0; }
              else if (getAlignmentX() == 0.0F) { x = (getBounds()).width / 2 - width / 2; }
              else if (getAlignmentX() == 4.0F) { x = (getBounds()).width - width; }
               y = (getBounds()).height / 2 + height / 2 - 1;
              
              g.drawLine(x + 2, y, x + width - 2, y);
            }
            
            public void update(Graphics g) {
              paint(g);
            }
          };
        
        accountLink.setCursor(Cursor.getPredefinedCursor(12));
        accountLink.addMouseListener(new MouseAdapter() {
              public void mousePressed(MouseEvent arg0) {
                try {
                  Util.openLink((new URL("http://www.minecraft.net/register.jsp")).toURI());
                } catch (Exception e) {
                  e.printStackTrace();
                } 
              }
            });
        accountLink.setForeground(LINK_COLOR);
        third.add(accountLink);
      }
    
    }
    catch (Error error) {}


    
    loginPanel.add(third, "Center");
    panel.add(loginPanel, "East");
    
    this.errorLabel.setFont(new Font(null, 2, 16));
    this.errorLabel.setForeground(new Color(16728128));
    this.errorLabel.setText("");
    panel.add(this.errorLabel, "North");
    
    return panel;
  }
  
  private TransparentLabel getUpdateLink() {
    TransparentLabel accountLink = new TransparentLabel("You need to update the launcher!") {
        private static final long serialVersionUID = 0L;
        
        public void paint(Graphics g) {
          super.paint(g);
          
          int x = 0;
          int y = 0;


          
          FontMetrics fm = g.getFontMetrics();
          int width = fm.stringWidth(getText());
          int height = fm.getHeight();
          
          if (getAlignmentX() == 2.0F) { x = 0; }
          else if (getAlignmentX() == 0.0F) { x = (getBounds()).width / 2 - width / 2; }
          else if (getAlignmentX() == 4.0F) { x = (getBounds()).width - width; }
           y = (getBounds()).height / 2 + height / 2 - 1;
          
          g.drawLine(x + 2, y, x + width - 2, y);
        }
        
        public void update(Graphics g) {
          paint(g);
        }
      };
    
    accountLink.setCursor(Cursor.getPredefinedCursor(12));
    accountLink.addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent arg0) {
            try {
              Util.openLink((new URL("http://www.minecraft.net/download.jsp")).toURI());
            } catch (Exception e) {
              e.printStackTrace();
            } 
          }
        });
    accountLink.setForeground(LINK_COLOR);
    return accountLink;
  }
  
  private JPanel buildMainOfflinePanel() {
    JPanel p = new TransparentPanel(new BorderLayout());
    p.add(getUpdateNews(), "Center");
    
    JPanel southPanel = new TexturedPanel();
    southPanel.setLayout(new BorderLayout());
    southPanel.add(new LogoPanel(), "West");
    southPanel.add(new TransparentPanel(), "Center");
    southPanel.add(center(buildOfflinePanel()), "East");
    southPanel.setPreferredSize(new Dimension(100, 100));
    
    p.add(southPanel, "South");
    return p;
  }
  
  private Component center(Component c) {
    TransparentPanel tp = new TransparentPanel(new GridBagLayout());
    tp.add(c);
    return tp;
  }





  
  private TransparentPanel buildOfflinePanel() {
    TransparentPanel panel = new TransparentPanel();
    panel.setInsets(0, 0, 0, 20);

    
    BorderLayout layout = new BorderLayout();
    panel.setLayout(layout);
    
    TransparentPanel loginPanel = new TransparentPanel(new BorderLayout());
    
    GridLayout gl = new GridLayout(0, 1);
    gl.setVgap(2);
    TransparentPanel pp = new TransparentPanel(gl);
    pp.setInsets(0, 8, 0, 0);
    
    pp.add(this.retryButton);
    pp.add(this.offlineButton);
    
    loginPanel.add(pp, "East");
    
    boolean canPlayOffline = this.launcherFrame.canPlayOffline(this.userName.getText());
    this.offlineButton.setEnabled(canPlayOffline);
    if (!canPlayOffline) {
      loginPanel.add(new TransparentLabel("(Not downloaded)", 4), "South");
    }
    panel.add(loginPanel, "Center");
    
    TransparentPanel p2 = new TransparentPanel(new GridLayout(0, 1));
    this.errorLabel.setFont(new Font(null, 2, 16));
    this.errorLabel.setForeground(new Color(16728128));
    p2.add(this.errorLabel);
    if (this.outdated) {
      TransparentLabel accountLink = getUpdateLink();
      p2.add(accountLink);
    } 
    
    loginPanel.add(p2, "Center");

    
    return panel;
  }
  
  public void setError(String errorMessage) {
    removeAll();
    add(buildMainLoginPanel(), "Center");
    this.errorLabel.setText(errorMessage);
    validate();
  }
  
  public void loginOk() {
    writeUsername();
  }
  
  public void setLoggingIn() {
    removeAll();
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(getUpdateNews(), "Center");

    
    JPanel southPanel = new TexturedPanel();
    southPanel.setLayout(new BorderLayout());
    southPanel.add(new LogoPanel(), "West");
    southPanel.add(new TransparentPanel(), "Center");
    JLabel label = new TransparentLabel("Logging in...                      ", 0);
    label.setFont(new Font(null, 1, 16));
    southPanel.add(center(label), "East");
    southPanel.setPreferredSize(new Dimension(100, 100));
    
    panel.add(southPanel, "South");
    
    add(panel, "Center");
    validate();
  }
  
  public void setNoNetwork() {
    removeAll();
    add(buildMainOfflinePanel(), "Center");
    validate();
  }
  
  public void checkAutologin() {
    if ((this.password.getPassword()).length > 0) {
      this.launcherFrame.login(this.userName.getText(), new String(this.password.getPassword()));
    }
  }
  
  public void setOutdated() {
    this.outdated = true;
  }
}


/* Location:              C:\Users\josep\Downloads\minecraft (1).jar!\net\minecraft\LoginForm.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */