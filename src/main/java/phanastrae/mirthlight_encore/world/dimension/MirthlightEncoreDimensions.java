package phanastrae.mirthlight_encore.world.dimension;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.dimension.DimensionType;
import phanastrae.mirthlight_encore.MirthlightEncore;

public class MirthlightEncoreDimensions {
    public static final RegistryKey<DimensionType> DREAMTWIRL_DIM_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            MirthlightEncore.id("dreamtwirl"));
}
