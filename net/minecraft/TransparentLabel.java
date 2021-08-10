/*    */ package net.minecraft;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import javax.swing.JLabel;
/*    */ 
/*    */ public class TransparentLabel
/*    */   extends JLabel {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public TransparentLabel(String string, int center) {
/* 11 */     super(string, center);
/* 12 */     setForeground(Color.WHITE);
/*    */   }
/*    */   
/*    */   public TransparentLabel(String string) {
/* 16 */     super(string);
/* 17 */     setForeground(Color.WHITE);
/*    */   }
/*    */   
/*    */   public boolean isOpaque() {
/* 21 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\josep\Downloads\minecraft (1).jar!\net\minecraft\TransparentLabel.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */