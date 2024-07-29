package phanastrae.mirthdew_encore.world.dimension;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class MirthdewEncoreDimensions {
    public static final ResourceLocation DREAMTWIRL_ID = MirthdewEncore.id("dreamtwirl");
    public static final ResourceKey<Level> DREAMTWIRL_WORLD = ResourceKey.create(Registries.DIMENSION, DREAMTWIRL_ID);
    public static final ResourceKey<DimensionType> DREAMTWIRL_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, DREAMTWIRL_ID);
}
