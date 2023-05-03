package net.mehvahdjukaar.modelfix.forge;

import net.minecraftforge.fml.ModLoader;

public class PlatStuffImpl {

    public static boolean isModStateValid() {
        return ModLoader.isLoadingStateValid();
    }
}
