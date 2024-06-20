package phanastrae.mirthdew_encore.registry;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class MirthdewEncoreEntityTypeTags {
    public static final TagKey<EntityType<?>> DREAMSPECK_OPAQUE = of("dreamspeck_opaque");
    public static final TagKey<EntityType<?>> USES_DREAMSPECK_COLLISION = of("uses_dreamspeck_collision");

    private static TagKey<EntityType<?>> of(String id) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, MirthdewEncore.id(id));
    }
}
