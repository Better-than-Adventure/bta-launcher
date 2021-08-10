/*    */ package net.minecraft;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Image;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.io.IOException;
/*    */ import javax.imageio.ImageIO;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class LogoPanel
/*    */   extends JPanel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private Image bgImage;
/*    */   
/*    */   public LogoPanel() {
/* 18 */     setOpaque(true);
/*    */     
/*    */     try {
/* 21 */       BufferedImage src = ImageIO.read(LoginForm.class.getResource("logo.png"));
/* 22 */       int w = src.getWidth();
/* 23 */       int h = src.getHeight();
/* 24 */       this.bgImage = src.getScaledInstance(w, h, 16);
/* 25 */       setPreferredSize(new Dimension(w + 32, h + 32));
/* 26 */     } catch (IOException e) {
/* 27 */       e.printStackTrace();
/*    */     } 
/*    */   }
/*    */   
/*    */   public void update(Graphics g) {
/* 32 */     paint(g);
/*    */   }
/*    */   
/*    */   public void paintComponent(Graphics g2) {
/* 36 */     g2.drawImage(this.bgImage, 24, 24, null);
/*    */   }
/*    */ }


/* Location:              C:\Users\josep\Downloads\minecraft (1).jar!\net\minecraft\LogoPanel.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */