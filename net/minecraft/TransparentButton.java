/*    */ package net.minecraft;
/*    */ 
/*    */ import javax.swing.JButton;
/*    */ 
/*    */ public class TransparentButton extends JButton {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public TransparentButton(String string) {
/*  9 */     super(string);
/*    */   }
/*    */   
/*    */   public boolean isOpaque() {
/* 13 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\josep\Downloads\minecraft (1).jar!\net\minecraft\TransparentButton.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */