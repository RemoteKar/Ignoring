package org.stellium.ignoring.mixin.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.stellium.ignoring.config.IgnoringConfig;
import org.stellium.ignoring.entity.EntityCaptures;

@Mixin(CapeLayer.class)
public class CapeFeatureRendererMixin {
   @WrapOperation(
      method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;entitySolid(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"
      )
   )
   private RenderType ignoring$useTranslucentCapeLayer(Identifier texture, Operation<RenderType> original) {
      Entity entity = EntityCaptures.MAIN.getEntity();
      if (entity == null) {
         return (RenderType)original.call(new Object[]{texture});
      }

      IgnoringConfig config = IgnoringConfig.get();
      return config.ignoreRender && config.shouldIgnorePlayer(entity) && config.transparency < 255
         ? RenderTypes.entityTranslucent(texture)
         : (RenderType)original.call(new Object[]{texture});
   }
}
