package net.mehvahdjukaar.modelfix.forge;

import net.neoforged.fml.ModLoader;

public class PlatStuffImpl {

    public static boolean isModStateValid() {
        return !ModLoader.hasErrors();
    }
}
