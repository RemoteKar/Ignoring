package org.stellium.ignoring.mixin;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.stellium.ignoring.config.IgnoringConfig;
import org.stellium.ignoring.entity.EntityCaptures;
import org.stellium.ignoring.util.ArgbUtils;

@Mixin(ModelPart.class)
public class ModelPartMixin {
   @ModifyArg(
      method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/geom/ModelPart;compile(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"
      ),
      index = 4
   )
   private int render(int argb) {
      Entity entity = EntityCaptures.MAIN.getEntity();
      return entity != null ? ArgbUtils.applyAlpha(argb, IgnoringConfig.get().transparency) : argb;
   }
}
