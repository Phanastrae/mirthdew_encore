package phanastrae.mirthdew_encore.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.apache.logging.log4j.util.BiConsumer;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.services.XPlatInterface;

public class MirthdewEncoreEntityTypes {

    public static final ResourceLocation DREAMSPECK_KEY = id("dreamspeck");
    public static final EntityType<DreamspeckEntity> DREAMSPECK =
            createBuilder(DreamspeckEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .fireImmune()
                    .build(getStr(DREAMSPECK_KEY));

    public static void init(java.util.function.BiConsumer<ResourceLocation, EntityType<?>> r) {
        r.accept(DREAMSPECK_KEY, DREAMSPECK);
    }

    private static ResourceLocation id(String path) {
        return MirthdewEncore.id(path);
    }

    @Nullable
    private static String getStr(ResourceLocation resourceLocation) {
        // sending null on neoforge crashes, but sending a string on fabric logs an error
        String loader = XPlatInterface.INSTANCE.getLoader();
        if(loader.equals("fabric")) {
            return null;
        } else {
            return resourceLocation.toString();
        }
    }

    public static void registerEntityAttributes(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> r) {
        r.accept(DREAMSPECK, DreamspeckEntity.createDreamspeckAttributes());
    }

    private static <T extends Entity> EntityType.Builder<T> createBuilder(EntityType.EntityFactory<T> factory, MobCategory spawnGroup) {
        return EntityType.Builder.of(factory, spawnGroup);
    }
}
