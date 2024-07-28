package phanastrae.mirthdew_encore.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class MirthdewEncoreEntityTypes {

    public static final EntityType<DreamspeckEntity> DREAM_SPECK =
            createBuilder(DreamspeckEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .fireImmune()
                    .build();

    public static void init() {
        registerWithAttributes(DREAM_SPECK, "dreamspeck", DreamspeckEntity.createDreamspeckAttributes());
    }

    private static void registerWithAttributes(EntityType<? extends LivingEntity> type, String name, AttributeSupplier.Builder builder) {
        register(type, name);
        registerAttributes(type, builder);
    }

    private static void register(EntityType<? extends Entity> type, String name) {
        ResourceLocation identifier = MirthdewEncore.id(name);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, identifier, type);
    }

    private static void registerAttributes(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
        FabricDefaultAttributeRegistry.register(type, builder);
    }

    private static <T extends Entity> EntityType.Builder<T> createBuilder(EntityType.EntityFactory<T> factory, MobCategory spawnGroup) {
        return EntityType.Builder.of(factory, spawnGroup);
    }
}
