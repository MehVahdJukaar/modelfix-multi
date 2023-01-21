package net.mehvahdjukaar.modelfix;

import net.mehvahdjukaar.modelfix.moonlight_configs.ConfigBuilder;
import net.mehvahdjukaar.modelfix.moonlight_configs.ConfigSpec;
import net.mehvahdjukaar.modelfix.moonlight_configs.ConfigType;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Supplier;

public class ModelFix {
    public static final String MOD_ID = "modelfix";

    public static final Logger LOGGER = LogManager.getLogger();

    private static final boolean MAC_OS = Util.getPlatform() == Util.OS.OSX;

    private static final ResourceLocation BLOCK_ATLAS = new ResourceLocation("textures/atlas/blocks.png");

    private static Supplier<Double> expansion;
    private static Supplier<Double> indent;
    private static Supplier<Double> shrinkMult = () -> 0d;
    public static ConfigSpec config;

    public static void init(boolean fabric) {
        ConfigBuilder builder = ConfigBuilder.create(new ResourceLocation(MOD_ID, "client"), ConfigType.CLIENT);

        builder.push("default");
        var e = builder.comment("quad expansion increment. enlarges each quad. Use to hide gaps. Keep both as close to 0 as possible")
                .define("item_quad_expansion", fabric ? 0.002 : 0.008d, -0.1d, 0.1d);
        var i = builder.comment("quad x/y offset. simply put moves the quad toward the center of the item. Use to hide gaps")
                .define("item_quad_indent", fabric ? 0.0001 : 0.007d, -0.1d, 0.1d);

        builder.pop();
        builder.push("mac_os").comment("It has been reported that some mac os systems are affected by atlass bleeding so the mod cant apply its main fix by removing atlas shrinking. Instead it can reduce it as much as possible by multiplying it by shrink_value_multiplier");
        var me = builder.comment("quad expansion increment. enlarges each quad. Use to hide gaps. Keep both as close to 0 as possible")
                .define("item_quad_expansion", 10 * (fabric ? 0.002 : 0.008d), -0.1d, 0.1d);
        var mi = builder.comment("quad x/y offset. simply put moves the quad toward the center of the item. Use to hide gaps")
                .define("item_quad_indent", 10 * (fabric ? 0.0001 : 0.007d), -0.1d, 0.1d);
        var sm = builder.comment("set to 0 for non macos behavior")
                .define("shrink_ratio_multiplier", 0.2, 0, 1);
        builder.pop();

        expansion = MAC_OS ? me : e;
        indent = MAC_OS ? mi : i;
        if (MAC_OS) shrinkMult = sm;

        builder.onChange(()-> {
            if(Minecraft.getInstance().getResourceManager()!=null) {
                Minecraft.getInstance().reloadResourcePacks();
            }
        });

       config = builder.buildAndRegister();
    }


    //who needs anti atlas bleeding when it doesn't occur even on mipmap 4 high render distance lol
    //apparently on mac os it does waaa
    public static float getShrinkRatio(ResourceLocation atlasLocation, float defaultValue, float returnValue) {
        if (atlasLocation.equals(BLOCK_ATLAS) && defaultValue == returnValue) {
            return (float) (defaultValue * shrinkMult.get());
        }
        return -1;
    }

    public static void createOrExpandSpan(List<ItemModelGenerator.Span> listSpans, ItemModelGenerator.SpanFacing spanFacing,
                                          int pixelX, int pixelY) {
        int length;
        ItemModelGenerator.Span existingSpan = null;
        for (ItemModelGenerator.Span span2 : listSpans) {
            if (span2.getFacing() == spanFacing) {
                int i = spanFacing.isHorizontal() ? pixelY : pixelX;
                if (span2.getAnchor() != i) continue;
                //skips faces with transparent pixels so we can enlarge safely
                if (expansion.get() != 0 && span2.getMax() != (!spanFacing.isHorizontal() ? pixelY : pixelX) - 1)
                    continue;
                existingSpan = span2;
                break;
            }
        }


        length = spanFacing.isHorizontal() ? pixelX : pixelY;
        if (existingSpan == null) {
            int newStart = spanFacing.isHorizontal() ? pixelY : pixelX;
            listSpans.add(new ItemModelGenerator.Span(spanFacing, length, newStart));
        } else {
            existingSpan.expand(length);
        }
    }

    public static void enlargeFaces(CallbackInfoReturnable<List<BlockElement>> cir) {
        double inc =  indent.get();
        double inc2 = expansion.get();
        for (var e : cir.getReturnValue()) {
            Vector3f from = e.from;
            Vector3f to = e.to;

            var set = e.faces.keySet();
            if (set.size() == 1) {
                var dir = set.stream().findAny().get();
                switch (dir) {
                    case UP -> {
                        from.set(from.x() - inc2, from.y() - inc, from.z() - inc2);
                        to.set(to.x() + inc2, to.y() - inc, to.z() + inc2);
                    }
                    case DOWN -> {
                        from.set(from.x() - inc2, from.y() + inc, from.z() - inc2);
                        to.set(to.x() + inc2, to.y() + inc, to.z() + inc2);
                    }
                    case WEST -> {
                        from.set(from.x() - inc, from.y() + inc2, from.z() - inc2);
                        to.set(to.x() - inc, to.y() - inc2, to.z() + inc2);
                    }
                    case EAST -> {
                        from.set(from.x() + inc, from.y() + inc2, from.z() - inc2);
                        to.set(to.x() + inc, to.y() - inc2, to.z() + inc2);
                    }
                }
            }
        }
    }

    public static Screen makeScreen(Screen screen) {
        return config.makeScreen(screen);
    }
}
