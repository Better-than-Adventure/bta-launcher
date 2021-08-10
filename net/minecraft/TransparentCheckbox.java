/*    */ package net.minecraft;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import javax.swing.JCheckBox;
/*    */ 
/*    */ public class TransparentCheckbox
/*    */   extends JCheckBox {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public TransparentCheckbox(String string) {
/* 11 */     super(string);
/* 12 */     setForeground(Color.WHITE);
/*    */   }
/*    */   
/*    */   public boolean isOpaque() {
/* 16 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\josep\Downloads\minecraft (1).jar!\net\minecraft\TransparentCheckbox.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */