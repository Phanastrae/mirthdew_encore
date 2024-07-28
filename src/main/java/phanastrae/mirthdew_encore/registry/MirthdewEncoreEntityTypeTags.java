package phanastrae.mirthdew_encore.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class MirthdewEncoreEntityTypeTags {
    public static final TagKey<EntityType<?>> DREAMSPECK_OPAQUE = of("dreamspeck_opaque");
    public static final TagKey<EntityType<?>> USES_DREAMSPECK_COLLISION = of("uses_dreamspeck_collision");

    private static TagKey<EntityType<?>> of(String id) {
        return TagKey.create(Registries.ENTITY_TYPE, MirthdewEncore.id(id));
    }
}
