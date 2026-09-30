package org.stellium.ignoring.mixin.fixes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.stellium.ignoring.entity.EntityCaptures;

@Mixin(Gui.class)
public class GameRendererMixin {
   @WrapOperation(
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"),
      method = "extractRenderState(Lnet/minecraft/client/DeltaTracker;ZZ)V"
   )
   private void generated(Screen instance, GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, Operation<Void> original) {
      EntityCaptures.MAIN.setEnabled(false);

      try {
         original.call(new Object[]{instance, context, mouseX, mouseY, delta});
      } finally {
         EntityCaptures.MAIN.setEnabled(true);
      }
   }
}
