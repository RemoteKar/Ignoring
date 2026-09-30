package org.stellium.ignoring.util;

public class ArgbUtils {
   public static int applyAlpha(int argb, int alpha) {
      return argb & 16777215 | alpha << 24;
   }

   public static int swapAlpha(int argb, int alpha) {
      return getArgb(Math.min(getAlpha(argb), alpha), getRed(argb), getGreen(argb), getBlue(argb));
   }

   public static int getAlpha(int argb) {
      return argb >>> 24;
   }

   public static int getRed(int argb) {
      return argb >> 16 & 0xFF;
   }

   public static int getGreen(int argb) {
      return argb >> 8 & 0xFF;
   }

   public static int getBlue(int argb) {
      return argb & 0xFF;
   }

   public static int getArgb(int alpha, int red, int green, int blue) {
      return alpha << 24 | red << 16 | green << 8 | blue;
   }
}
