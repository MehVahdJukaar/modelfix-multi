package net.mehvahdjukaar.modelfix.moonlight_configs.forge;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.mehvahdjukaar.modelfix.moonlight_configs.ConfigSpec;
import net.mehvahdjukaar.modelfix.moonlight_configs.ConfigType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class ConfigSpecWrapper extends ConfigSpec {

    private final ModConfigSpec spec;

    private final ModConfig modConfig;

    public ConfigSpecWrapper(ResourceLocation name, ModConfigSpec spec, ConfigType type, boolean synced, @javax.annotation.Nullable Runnable onChange) {
        super(name, FMLPaths.CONFIGDIR.get(), type, synced, onChange);
        this.spec = spec;

        if (this.isSynced()) {

            NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
            NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedOut);
        }

        ModConfig.Type t = this.getConfigType() == ConfigType.COMMON ? ModConfig.Type.COMMON : ModConfig.Type.CLIENT;

        ModContainer modContainer = ModLoadingContext.get().getActiveContainer();
        this.modConfig = new ModConfig(t, spec, modContainer, name.getNamespace() + "-" + name.getPath() + ".toml");
        //for event
        ConfigSpec.addTrackedSpec(this);
    }

    @Override
    public String getFileName() {
        return modConfig.getFileName();
    }

    @Override
    public Path getFullPath() {
        return FMLPaths.CONFIGDIR.get().resolve(this.getFileName());
        // return modConfig.getFullPath();
    }

    @Override
    public void register() {
        ModContainer modContainer = ModLoadingContext.get().getActiveContainer();
        modContainer.addConfig(this.modConfig);
    }

    @Override
    public void loadFromFile() {
        try {
            CommentedFileConfig configFile = CommentedFileConfig
                    .builder(this.getFullPath())
                    .sync()
                    .preserveInsertionOrder()
                    .writingMode(WritingMode.REPLACE)
                    .build();
            configFile.load();
            configFile.save();

            spec.setConfig(configFile);
        } catch (Exception e) {
            throw new RuntimeException(
                    new IOException("Failed to load " + this.getFileName() + " config. Try deleting it: " + e));
        }
    }

    public ModConfigSpec getSpec() {
        return spec;
    }

    @Nullable
    public ModConfig getModConfig() {
        return modConfig;
    }

    public ModConfig.Type getModConfigType() {
        return this.getConfigType() == ConfigType.CLIENT ? ModConfig.Type.CLIENT : ModConfig.Type.COMMON;
    }

    @Override
    public boolean isLoaded() {
        return spec.isLoaded();
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public Screen makeScreen(Screen parent, @Nullable ResourceLocation background) {
        var container = ModList.get().getModContainerById(this.getModId());
        if (container.isPresent()) {
            var factory = container.get().getCustomExtension(IConfigScreenFactory.class);
            if (factory.isPresent()) return factory.get().createScreen(Minecraft.getInstance(), parent);
        }
        return null;
    }

    @Override
    public boolean hasConfigScreen() {
        return ModList.get().getModContainerById(this.getModId())
                .map(container -> container.getCustomExtension(IConfigScreenFactory.class)
                        .isPresent()).orElse(false);
    }

    protected void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            //send this configuration to connected clients
            syncConfigsToPlayer(serverPlayer);
        }
    }

    protected void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity().level().isClientSide) {
            onRefresh();
        }
    }

    protected void onConfigChange(ModConfigEvent event) {
        if (event.getConfig().getSpec() == this.getSpec()) {
            //send this configuration to connected clients
            if (this.isSynced()) sendSyncedConfigsToAllPlayers();
            onRefresh();
        }
    }

    @Override
    public void loadFromBytes(InputStream stream) {
        // try { //this should work the same as below
        //      var b = stream.readAllBytes();
        //     this.modConfig.acceptSyncedConfig(b);
        // } catch (Exception ignored) {
        // }

        //using this isntead so we dont fire the config changes event otherwise this will loop
        this.getSpec().setConfig(TomlFormat.instance().createParser().parse(stream));
        this.onRefresh();
    }


}
