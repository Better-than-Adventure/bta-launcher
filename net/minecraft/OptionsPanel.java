package net.minecraft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.border.EmptyBorder;

public class OptionsPanel extends JDialog {
	private static final long serialVersionUID = 1L;
	public static boolean enablePrerelease;

	public OptionsPanel(Frame parent) {
    super(parent);
    
    setModal(true);
    
    JPanel panel = new JPanel(new BorderLayout());
    JLabel label = new JLabel("Launcher options", 0);
    label.setBorder(new EmptyBorder(0, 0, 16, 0));
    label.setFont(new Font("Default", 1, 16));
    panel.add(label, "North");
    
    JPanel optionsPanel = new JPanel(new BorderLayout());
    JPanel labelPanel = new JPanel(new GridLayout(0, 1));
    JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
    optionsPanel.add(labelPanel, "West");
    optionsPanel.add(fieldPanel, "Center");
    
    final JButton forceButton = new JButton("Force update!");
    forceButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
            GameUpdater.forceUpdate = true;
            forceButton.setText("Will force!");
            forceButton.setEnabled(false);
          }
        });
    
    labelPanel.add(new JLabel("Force game update: ", 4));
    fieldPanel.add(forceButton);
    
    labelPanel.add(new JLabel("Game location on disk: ", 4));
    TransparentLabel dirLink = new TransparentLabel(Util.getWorkingDirectory().toString()) {
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
    dirLink.setCursor(Cursor.getPredefinedCursor(12));
    dirLink.addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent arg0) {
            try {
              Util.openLink((new URL("file://" + Util.getWorkingDirectory().getAbsolutePath())).toURI());
            } catch (Exception e) {
              e.printStackTrace();
            } 
          }
        });
    dirLink.setForeground(new Color(2105599));
    fieldPanel.add(dirLink);

    
    labelPanel.add(new JLabel("Use snapshots and prereleases: ", 4));
    JCheckBox checkBox = new JCheckBox();
    File file = new File(Util.getWorkingDirectory() + "prerelease");
    if (file.exists()) checkBox.setSelected(true);
    checkBox.addActionListener(new ActionListener() {
    	@Override
    	public void actionPerformed(ActionEvent e)
    	{
    		if (checkBox.isSelected())
    		{
    			try 
    			{
    				file.createNewFile();
    				enablePrerelease = true;
    			}
    			catch (Exception q)
    			{
    				checkBox.setSelected(false);
    			}
    		}
    		else
    		{
    			try
    			{
    				file.delete();
    			}
    			catch (Exception q)
    			{
    				checkBox.setSelected(true);
    				enablePrerelease = false;
    			}
    		}
    	}
    });
    fieldPanel.add(checkBox);
    
    panel.add(optionsPanel, "Center");
    
    JPanel buttonsPanel = new JPanel(new BorderLayout());
    buttonsPanel.add(new JPanel(), "Center");
    JButton doneButton = new JButton("Done");
    doneButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
            OptionsPanel.this.setVisible(false);
          }
        });
    buttonsPanel.add(doneButton, "East");
    buttonsPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
    
    panel.add(buttonsPanel, "South");
    
    add(panel);
    panel.setBorder(new EmptyBorder(16, 24, 24, 24));
    pack();
    setLocationRelativeTo(parent);
  }
}

/*
 * Location: C:\Users\josep\Downloads\minecraft
 * (1).jar!\net\minecraft\OptionsPanel.class Java compiler version: 5 (49.0)
 * JD-Core Version: 1.1.3
 */