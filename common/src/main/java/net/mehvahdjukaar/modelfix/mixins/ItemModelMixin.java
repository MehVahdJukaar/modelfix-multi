package net.mehvahdjukaar.modelfix.mixins;

import net.mehvahdjukaar.modelfix.ModelFix;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemModelGenerator.class)
public abstract class ItemModelMixin {

    @Inject(method = "createSideElements", at = @At("RETURN"))
    public void increaseSide(SpriteContents spriteContents, String string, int tintIndex,
                             CallbackInfoReturnable<List<BlockElement>> cir) {
        ModelFix.enlargeFaces(cir);
    }

    /**
     * @author MehVahdJukaar
     * @reason fixing item models gaps
     */
    @Overwrite
    private void createOrExpandSpan(List<ItemModelGenerator.Span> listSpans, ItemModelGenerator.SpanFacing spanFacing, int pixelX, int pixelY) {
        ModelFix.createOrExpandSpan(listSpans, spanFacing, pixelX, pixelY);
    }


}
