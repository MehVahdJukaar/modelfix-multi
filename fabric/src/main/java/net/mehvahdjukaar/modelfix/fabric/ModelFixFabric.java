package net.mehvahdjukaar.modelfix.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.mehvahdjukaar.modelfix.ModelFix;

public class ModelFixFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelFix.init(true);
    }


}
