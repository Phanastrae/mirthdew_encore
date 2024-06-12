package phanastrae.mirthdew_encore.world.dimension;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class MirthdewEncoreDimensions {
    public static final Identifier DREAMTWIRL_ID = MirthdewEncore.id("dreamtwirl");
    public static final RegistryKey<World> DREAMTWIRL_WORLD = RegistryKey.of(RegistryKeys.WORLD, DREAMTWIRL_ID);
    public static final RegistryKey<DimensionType> DREAMTWIRL_DIM_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, DREAMTWIRL_ID);
}
