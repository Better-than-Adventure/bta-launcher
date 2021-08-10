/*    */ package net.minecraft;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public class MinecraftLauncher {
/*    */   private static final int MIN_HEAP = 511;
/*    */   private static final int RECOMMENDED_HEAP = 1024;
/*    */   
/*    */   public static void main(String[] args) throws Exception {
/* 10 */     float heapSizeMegs = (float)(Runtime.getRuntime().maxMemory() / 1024L / 1024L);
/*    */     
/* 12 */     if (heapSizeMegs > 511.0F) {
/* 13 */       LauncherFrame.main(args);
/*    */     } else {
/*    */       try {
				
	
/* 16 */         String pathToJar = MinecraftLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
/*    */         
/* 18 */         ArrayList<String> params = new ArrayList<String>();
/*    */         
/* 20 */         params.add("javaw");
				 params.add("-noverify");
/* 21 */         params.add("-Xmx1024m");
/* 22 */         params.add("-Dsun.java2d.noddraw=true");
/* 23 */         params.add("-Dsun.java2d.d3d=false");
/* 24 */         params.add("-Dsun.java2d.opengl=false");
/* 25 */         params.add("-Dsun.java2d.pmoffscreen=false");
/*    */         
/* 27 */         params.add("-classpath");
/* 28 */         params.add(pathToJar);
/* 29 */         params.add("net.minecraft.LauncherFrame");
/* 30 */         ProcessBuilder pb = new ProcessBuilder(params);
/* 31 */         Process process = pb.start();
/* 32 */         if (process == null) throw new Exception("!"); 
/* 33 */         System.exit(0);
/* 34 */       } catch (Exception e) {
/* 35 */         e.printStackTrace();
/* 36 */         LauncherFrame.main(args);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\josep\Downloads\minecraft (1).jar!\net\minecraft\MinecraftLauncher.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */