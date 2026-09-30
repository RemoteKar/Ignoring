package org.stellium.ignoring.mixin.render;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.stellium.ignoring.render.IgnoringItemSubmit;
import org.stellium.ignoring.render.TransparencyLayers;
import org.stellium.ignoring.util.ArgbUtils;
@Mixin(ItemFeatureRenderer.class)
public class ItemCommandRendererMixin {
    @ModifyArg(method = "prepareMainSubmit", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/QuadInstance;setColor(I)V"), index = 0)
    private int ignoring$itemAlpha(int color, @Local(argsOnly = true) ItemFeatureRenderer.Submit submit) {
        int alpha = ((IgnoringItemSubmit)(Object)submit).ignoring$alpha();
        return alpha < 0 ? color : ArgbUtils.swapAlpha(color, alpha);
    }
    @ModifyVariable(method = "prepareMainSubmit", at = @At("STORE"))
    private RenderType ignoring$itemLayer(RenderType layer, @Local(argsOnly = true) ItemFeatureRenderer.Submit submit) {
        int alpha = ((IgnoringItemSubmit)(Object)submit).ignoring$alpha();
        return alpha >= 0 && alpha < 255 ? TransparencyLayers.itemLayer(layer, submit.foilType()) : layer;
    }
}
