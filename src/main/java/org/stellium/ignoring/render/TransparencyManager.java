package org.stellium.ignoring.render;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.stellium.ignoring.config.IgnoringConfig;
import org.stellium.ignoring.util.ArgbUtils;

public class TransparencyManager {
   public static int getTranslucentArgb(Entity entity, int original) {
      if (!IgnoringConfig.get().ignoreRender) {
         return original;
      } else {
         Minecraft client = Minecraft.getInstance();
         LocalPlayer player = client.player;
         ClientLevel world = client.level;
         Vec3 cameraPos = client.gameRenderer.mainCamera().position();
         Vec3 entityPos = entity.position();
         if (player == null || world == null) {
            return original;
         } else if (player.equals(entity)) {
            return getColorForYourself(original);
         } else {
            return entity.isInvisibleTo(player) ? original : ArgbUtils.swapAlpha(original, getAlpha(cameraPos, entityPos));
         }
      }
   }

   private static int getAlpha(Vec3 cameraPos, Vec3 entityPos) {
      float hidingActivationDistance = 4.0F;
      float fullHidingDistance = 2.8F;
      float minHidingValue = 0.2F;
      float distance = calculateDistance(cameraPos, entityPos);
      if (hidingActivationDistance <= 0.0F) {
         return 255;
      } else {
         float a = hidingActivationDistance - fullHidingDistance;
         float b = distance - fullHidingDistance;
         if (a <= 0.0F || b <= 0.0F) {
            return (int)(minHidingValue * 255.0F);
         } else {
            return b >= a ? 255 : (int)(Mth.clamp(minHidingValue + (1.0F - minHidingValue) * (b / a), 0.0F, 1.0F) * 255.0F);
         }
      }
   }

   private static int getColorForYourself(int original) {
      return FabricLoader.getInstance().isDevelopmentEnvironment() ? ArgbUtils.swapAlpha(original, 51) : original;
   }

   public static float calculateDistance(Vec3 cameraPos, Vec3 entityPos) {
      float f = (float)(cameraPos.x() - entityPos.x());
      float g = (float)(cameraPos.y() - entityPos.y());
      float h = (float)(cameraPos.z() - entityPos.z());
      return Mth.sqrt(f * f + g * g + h * h);
   }

   public static boolean canRenderTransparencyShadow(Entity entity) {
      IgnoringConfig config = IgnoringConfig.get();
      return config.ignoreRender && config.shouldIgnorePlayer(entity);
   }
}
