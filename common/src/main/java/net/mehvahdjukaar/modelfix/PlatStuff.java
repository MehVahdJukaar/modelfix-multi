package net.mehvahdjukaar.modelfix;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class PlatStuff {
    @ExpectPlatform
    public static boolean isModStateValid() {
        throw new AssertionError();
    }
}
