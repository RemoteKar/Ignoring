package org.stellium.ignoring.render;

import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.stellium.ignoring.config.IgnoringConfig;
import org.stellium.ignoring.entity.EntityCaptures;
import org.stellium.ignoring.mixin.accessor.RenderLayerAccessor;
import org.stellium.ignoring.mixin.accessor.RenderSetupAccessor;
import org.stellium.ignoring.mixin.accessor.RenderSetupTextureSpecAccessor;

public class TransparencyLayers {
   public static int targetAlpha() {
      Entity entity = EntityCaptures.MAIN.getEntity();
      if (entity == null) return -1;
      IgnoringConfig config = IgnoringConfig.get();
      return config.ignoreRender && config.shouldIgnorePlayer(entity) ? config.transparency : -1;
   }
   public static RenderType modelLayer(RenderType original) {
      int alpha = targetAlpha();
      if (alpha < 0 || alpha == 255 || original.hasBlending()) return original;
      Identifier texture = getTextureLocation(original);
      return texture == null ? original : net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }
   public static RenderType itemLayer(RenderType original, net.minecraft.client.renderer.item.ItemStackRenderState.FoilType foil) {
      boolean blocks = TextureAtlas.LOCATION_BLOCKS.equals(getTextureLocation(original));
      return switch (foil) {
         case NONE -> blocks ? Sheets.translucentBlockItemSheet() : Sheets.translucentItemSheet();
         case STANDARD -> blocks ? Sheets.translucentBlockItemGlintSheet() : Sheets.translucentItemGlintSheet();
         case SPECIAL -> blocks ? Sheets.translucentBlockItemGlintSpecialSheet() : Sheets.translucentItemGlintSpecialSheet();
      };
   }
   public static RenderType getArmorLayer(boolean cull, Identifier texture, Supplier<RenderType> original) {
      if (canReplaceRenderLayer()) {
         return cull ? RenderTypes.entityTranslucentCull(texture) : RenderTypes.entityTranslucent(texture);
      } else {
         return original.get();
      }
   }

   public static RenderType getLayer(Identifier texture, Supplier<RenderType> original) {
      return canReplaceRenderLayer() ? RenderTypes.entityTranslucentCull(texture) : original.get();
   }

   public static RenderType getItemLayer(RenderType original) {
      if (!canReplaceRenderLayer()) {
         return original;
      }

      Identifier texture = getTextureLocation(original);
      return TextureAtlas.LOCATION_BLOCKS.equals(texture) ? Sheets.translucentBlockItemSheet() : Sheets.translucentItemSheet();
   }

   public static RenderType getItemLayer(Supplier<RenderType> original) {
      return getItemLayer(original.get());
   }

   private static Identifier getTextureLocation(RenderType layer) {
      try {
         Map<String, Object> textures = ((RenderSetupAccessor)(Object)((RenderLayerAccessor)layer).ignoring$getRenderSetup()).ignoring$getTextures();
         if (textures != null && !textures.isEmpty()) {
            Object textureSpec = textures.get("Sampler0");
            if (textureSpec == null) textureSpec = textures.values().iterator().next();
            return ((RenderSetupTextureSpecAccessor)textureSpec).ignoring$getLocation();
         } else {
            return null;
         }
      } catch (Exception ignored) {
         return null;
      }
   }

   private static boolean canReplaceRenderLayer() {
      try {
         IgnoringConfig config = IgnoringConfig.get();
      } catch (Exception e) {
         return false;
      }

      IgnoringConfig var3 = IgnoringConfig.get();
      if (var3 == null) {
         return false;
      }

      if (!var3.ignoreRender) {
         return false;
      }

      Entity entity = EntityCaptures.MAIN.getEntity();
      return entity == null ? false : var3.shouldIgnorePlayer(entity);
   }


}
