package phanastrae.mirthdew_encore.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class MirthdewEncoreEntityTypes {

    public static final EntityType<DreamspeckEntity> DREAM_SPECK =
            createBuilder(DreamspeckEntity::new, SpawnGroup.MISC)
                    .dimensions(0.4F, 0.4F)
                    .makeFireImmune()
                    .build();

    public static void init() {
        registerWithAttributes(DREAM_SPECK, "dreamspeck", DreamspeckEntity.createDreamspeckAttributes());
    }

    private static void registerWithAttributes(EntityType<? extends LivingEntity> type, String name, DefaultAttributeContainer.Builder builder) {
        register(type, name);
        registerAttributes(type, builder);
    }

    private static void register(EntityType<? extends Entity> type, String name) {
        Identifier identifier = MirthdewEncore.id(name);
        Registry.register(Registries.ENTITY_TYPE, identifier, type);
    }

    private static void registerAttributes(EntityType<? extends LivingEntity> type, DefaultAttributeContainer.Builder builder) {
        FabricDefaultAttributeRegistry.register(type, builder);
    }

    private static <T extends Entity> EntityType.Builder<T> createBuilder(EntityType.EntityFactory<T> factory, SpawnGroup spawnGroup) {
        return EntityType.Builder.create(factory, spawnGroup);
    }
}
