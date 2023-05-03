package net.mehvahdjukaar.modelfix.mixins;

import net.mehvahdjukaar.modelfix.ModelFixGeom;
import net.mehvahdjukaar.modelfix.PlatStuff;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemModelGenerator.class)
public abstract class ItemModelMixin {

    @Inject(method = "createSideElements", at = @At("RETURN"))
    public void increaseSide(SpriteContents spriteContents, String string, int tintIndex,
                             CallbackInfoReturnable<List<BlockElement>> cir) {
       if(PlatStuff.isModStateValid()) ModelFixGeom.enlargeFaces(cir);
    }

    /**
     * @author MehVahdJukaar
     * @reason fixing item models gaps
     */
    @Overwrite
    private void createOrExpandSpan(List<ItemModelGenerator.Span> listSpans, ItemModelGenerator.SpanFacing spanFacing, int pixelX, int pixelY) {
       if(PlatStuff.isModStateValid())  ModelFixGeom.createOrExpandSpan(listSpans, spanFacing, pixelX, pixelY);
    }


}
