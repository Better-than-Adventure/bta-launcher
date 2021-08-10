/*    */ package net.minecraft;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.GradientPaint;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Image;
/*    */ import java.awt.geom.Point2D;
/*    */ import java.io.IOException;
/*    */ import javax.imageio.ImageIO;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class TexturedPanel
/*    */   extends JPanel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private Image img;
/*    */   private Image bgImage;
/*    */   
/*    */   public TexturedPanel() {
/* 21 */     setOpaque(true);
/*    */     
/*    */     try {
/* 24 */       this.bgImage = ImageIO.read(LoginForm.class.getResource("dirt.png")).getScaledInstance(32, 32, 16);
/* 25 */     } catch (IOException e) {
/* 26 */       e.printStackTrace();
/*    */     } 
/*    */   }
/*    */   
/*    */   public void update(Graphics g) {
/* 31 */     paint(g);
/*    */   }
/*    */   
/*    */   public void paintComponent(Graphics g2) {
/* 35 */     int w = getWidth() / 2 + 1;
/* 36 */     int h = getHeight() / 2 + 1;
/* 37 */     if (this.img == null || this.img.getWidth(null) != w || this.img.getHeight(null) != h) {
/* 38 */       this.img = createImage(w, h);
/*    */       
/* 40 */       Graphics g = this.img.getGraphics();
/* 41 */       for (int x = 0; x <= w / 32; x++) {
/* 42 */         for (int y = 0; y <= h / 32; y++)
/* 43 */           g.drawImage(this.bgImage, x * 32, y * 32, null); 
/*    */       } 
/* 45 */       if (g instanceof Graphics2D) {
/* 46 */         Graphics2D gg = (Graphics2D)g;
/* 47 */         int gh = 1;
/* 48 */         gg.setPaint(new GradientPaint(new Point2D.Float(0.0F, 0.0F), new Color(553648127, true), new Point2D.Float(0.0F, gh), new Color(0, true)));
/* 49 */         gg.fillRect(0, 0, w, gh);
/*    */         
/* 51 */         gh = h;
/* 52 */         gg.setPaint(new GradientPaint(new Point2D.Float(0.0F, 0.0F), new Color(0, true), new Point2D.Float(0.0F, gh), new Color(1610612736, true)));
/* 53 */         gg.fillRect(0, 0, w, gh);
/*    */       } 
/* 55 */       g.dispose();
/*    */     } 
/* 57 */     g2.drawImage(this.img, 0, 0, w * 2, h * 2, null);
/*    */   }
/*    */ }


/* Location:              C:\Users\josep\Downloads\minecraft (1).jar!\net\minecraft\TexturedPanel.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */