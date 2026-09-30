package org.stellium.ignoring.mixin.render;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.stellium.ignoring.render.IgnoringItemSubmit;
import org.stellium.ignoring.render.TransparencyLayers;
@Mixin(ItemFeatureRenderer.Submit.class)
public class ItemSubmitMixin implements IgnoringItemSubmit {
    @Unique private int ignoring$alpha;
    @Inject(method = "<init>", at = @At("RETURN"))
    private void ignoring$captureAlpha(CallbackInfo ci) { ignoring$alpha = TransparencyLayers.targetAlpha(); }
    public int ignoring$alpha() { return ignoring$alpha; }
}
