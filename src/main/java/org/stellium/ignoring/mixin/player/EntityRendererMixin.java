package org.stellium.ignoring.mixin.player;
import net.minecraft.client.renderer.SubmitNodeCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.stellium.ignoring.render.TransparencyLayers;
import org.stellium.ignoring.util.ArgbUtils;
@Mixin(SubmitNodeCollection.class)
public class EntityRendererMixin {
    @ModifyVariable(method = "nameTag", at = @At("HEAD"), argsOnly = true, index = 5)
    private static int ignoring$nameAlpha(int color) {
        int alpha = TransparencyLayers.targetAlpha();
        return alpha < 0 ? color : ArgbUtils.swapAlpha(color, alpha);
    }
    @ModifyVariable(method = "nameTag", at = @At("HEAD"), argsOnly = true, index = 6)
    private static int ignoring$backgroundAlpha(int color) {
        int alpha = TransparencyLayers.targetAlpha();
        return alpha < 0 || color == 0 ? color : ArgbUtils.swapAlpha(color, alpha);
    }
}
