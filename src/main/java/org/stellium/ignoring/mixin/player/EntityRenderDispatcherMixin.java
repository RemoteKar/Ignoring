package org.stellium.ignoring.mixin.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState.ShadowPiece;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.stellium.ignoring.config.IgnoringConfig;
import org.stellium.ignoring.render.IgnoringStateEntity;
import org.stellium.ignoring.render.TransparencyManager;
import org.stellium.ignoring.render.TransparencyRenderer;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
   @Inject(method = "extractEntity(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;", at = @At("RETURN"))
   private <E extends Entity> void ignoring$captureEntity(E entity, float tickDelta, CallbackInfoReturnable<EntityRenderState> cir) {
      EntityRenderState state = (EntityRenderState)cir.getReturnValue();
      if (state != null) {
         ((IgnoringStateEntity)state).ignoring$setEntity(entity);
      }
   }

   @WrapOperation(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"
      ),
      method = "submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"
   )
   private void handleEntityRendering(
      EntityRenderer<?, EntityRenderState> instance,
      EntityRenderState state,
      PoseStack matrices,
      SubmitNodeCollector commandQueue,
      CameraRenderState cameraState,
      Operation<Void> original
   ) {
      Entity entity = ((IgnoringStateEntity)state).ignoring$entity();
      if (entity == null) {
         original.call(new Object[]{instance, state, matrices, commandQueue, cameraState});
      } else {
         TransparencyRenderer.handleEntityRendering(entity, () -> original.call(new Object[]{instance, state, matrices, commandQueue, cameraState}));
      }
   }

   @WrapOperation(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitShadow(Lcom/mojang/blaze3d/vertex/PoseStack;FLjava/util/List;)V"
      ),
      method = "submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"
   )
   private void handleShadowRendering(
      SubmitNodeCollector instance,
      PoseStack matrices,
      float shadowRadius,
      List<ShadowPiece> shadowPieces,
      Operation<Void> original,
      @Local(argsOnly = true) EntityRenderState state
   ) {
      Entity entity = ((IgnoringStateEntity)state).ignoring$entity();
      if (entity != null && TransparencyManager.canRenderTransparencyShadow(entity)) {
         float alpha = IgnoringConfig.get().transparency / 255.0F;
         List<ShadowPiece> adjusted = new ArrayList<>(shadowPieces.size());

         for (ShadowPiece piece : shadowPieces) {
            adjusted.add(new ShadowPiece(piece.relativeX(), piece.relativeY(), piece.relativeZ(), piece.shapeBelow(), alpha));
         }

         original.call(new Object[]{instance, matrices, shadowRadius, adjusted});
      } else {
         original.call(new Object[]{instance, matrices, shadowRadius, shadowPieces});
      }
   }

   @Inject(
      method = "submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
      at = @At("TAIL")
   )
   private void ignoring$clearCapturedEntity(
      EntityRenderState state,
      CameraRenderState cameraState,
      double x,
      double y,
      double z,
      PoseStack matrices,
      SubmitNodeCollector commandQueue,
      CallbackInfo ci
   ) {
      ((IgnoringStateEntity)state).ignoring$setEntity(null);
   }
}
