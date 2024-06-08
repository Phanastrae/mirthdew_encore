package phanastrae.mirthlight_encore.world.dimension;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import phanastrae.mirthlight_encore.MirthlightEncore;

public class MirthlightEncoreDimensions {
    public static final Identifier DREAMTWIRL_ID = MirthlightEncore.id("dreamtwirl");
    public static final RegistryKey<World> DREAMTWIRL_WORLD = RegistryKey.of(RegistryKeys.WORLD, DREAMTWIRL_ID);
    public static final RegistryKey<DimensionType> DREAMTWIRL_DIM_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, DREAMTWIRL_ID);
}
