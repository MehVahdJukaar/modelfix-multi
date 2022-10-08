package net.mehvahdjukaar.modelfix.forge;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.modelfix.ModelFix;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Author: MehVahdJukaar
 */
@Mod(ModelFix.MOD_ID)
public class ModelFixImpl {
    private static ForgeConfigSpec.DoubleValue EXPANSION;
    public static ForgeConfigSpec.DoubleValue RECESS;

    public ModelFixImpl() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        RECESS = builder.comment("quad x/y offset. simply put moves the quad toward the center of the item. Use to hide gaps")
                .defineInRange("quad_recess", 0.007d, -0.1d,0.1d);
        EXPANSION = builder.comment("quad expansion increment. enlarges each quad. Use to hide gaps. Keep both as close to 0 as possible")
                .defineInRange("quad_expansion", 0.008d, -0.1d,0.1d);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, builder.build());
    }

    public static double getRecess() {
        return RECESS.get();// 0.0045f;//0.019f;//0.0055f;
    }
    public static double getExpansion() {
        return EXPANSION.get();//0.013f;//0.008f;//0.011f;
    }

}
