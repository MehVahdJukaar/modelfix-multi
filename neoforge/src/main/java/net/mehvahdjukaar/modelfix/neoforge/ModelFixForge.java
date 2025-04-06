package net.mehvahdjukaar.modelfix.neoforge;

import net.mehvahdjukaar.modelfix.ModelFix;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

/**
 * Author: MehVahdJukaar
 */
@Mod(value = ModelFix.MOD_ID, dist = Dist.CLIENT)
public class ModelFixForge {

    public ModelFixForge() {
        ModelFix.init(false);
    }


}
