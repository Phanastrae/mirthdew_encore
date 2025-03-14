package phanastrae.mirthdew_encore.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class MirthdewEncoreFluidTags {
    public static final TagKey<Fluid> VESPERBILE = of("vesperbile");

    private static TagKey<Fluid> of(String id) {
        return TagKey.create(Registries.FLUID, MirthdewEncore.id(id));
    }
}
