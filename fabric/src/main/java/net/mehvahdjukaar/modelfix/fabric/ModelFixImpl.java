package net.mehvahdjukaar.modelfix.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ModelFixImpl implements ClientModInitializer {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    private static final File file = FabricLoader.getInstance().getConfigDir().resolve("model-fix.json").toFile();
    private static ModelFixImpl INSTANCE;

    @Expose
    public final double quad_expansion = 0.002;
    @Expose
    public final double quad_indent = 0.0001;
    @Expose
    public final String comment = "Tweak these two values to slightly move ot expand the item model quads to better hide all the tiny gaps for your system. Keep as close to 0 as possible";

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        loadFromFile();
    }

    public static double getExpansion() {
        return INSTANCE.quad_expansion;
    }

    public static double getRecess() {
        return INSTANCE.quad_indent;
    }


    public void loadFromFile() {

        if (file.exists() && file.isFile()) {
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

                INSTANCE = GSON.fromJson(bufferedReader, ModelFixImpl.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load config", e);
            }
        } else {
            saveConfig();
        }

    }

    public void saveConfig() {
        try (FileOutputStream stream = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException ignored) {
        }
    }

}
