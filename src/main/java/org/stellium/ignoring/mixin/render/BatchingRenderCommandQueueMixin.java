package org.stellium.ignoring.mixin.render;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.ItemQuads;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.stellium.ignoring.render.TransparencyLayers;
import org.stellium.ignoring.util.ArgbUtils;
@Mixin(SubmitNodeCollection.class)
public class BatchingRenderCommandQueueMixin {
    @ModifyVariable(method = "submitModel", at = @At("HEAD"), argsOnly = true, index = 7)
    private int ignoring$modelAlpha(int color) {
        int alpha = TransparencyLayers.targetAlpha();
        return alpha < 0 ? color : ArgbUtils.swapAlpha(color, alpha);
    }
    @ModifyVariable(method = "submitModel", at = @At("HEAD"), argsOnly = true, index = 4)
    private RenderType ignoring$modelLayer(RenderType layer) { return TransparencyLayers.modelLayer(layer); }
    @ModifyVariable(method = "submitItem", at = @At("HEAD"), argsOnly = true, index = 7)
    private ItemQuads ignoring$itemPhase(ItemQuads quads) {
        int alpha = TransparencyLayers.targetAlpha();
        return alpha >= 0 && alpha < 255 ? new ItemQuads(quads.all(), java.util.List.of(), quads.all()) : quads;
    }
}
