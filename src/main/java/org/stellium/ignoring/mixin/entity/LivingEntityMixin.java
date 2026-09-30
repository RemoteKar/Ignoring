package org.stellium.ignoring.mixin.entity;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.stellium.ignoring.config.IgnoringConfig;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
   @Inject(method = "isPickable()Z", at = @At("RETURN"), cancellable = true)
   private void injected(CallbackInfoReturnable<Boolean> cir) {
      if (IgnoringConfig.get().interactionThroughIgnoredPlayer) {
         LivingEntity entity = (LivingEntity)(Object)this;
         if (IgnoringConfig.get().shouldIgnorePlayer(entity)) {
            cir.setReturnValue(false);
         }
      }
   }
}
