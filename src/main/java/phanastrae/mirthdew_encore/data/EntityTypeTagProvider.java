package phanastrae.mirthdew_encore.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EntityTypeTags;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreEntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public EntityTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(EntityTypeTags.FALL_DAMAGE_IMMUNE)
                .add(MirthdewEncoreEntityTypes.DREAM_SPECK);

        getOrCreateTagBuilder(MirthdewEncoreEntityTypeTags.DREAMSPECK_OPAQUE)
                .add(MirthdewEncoreEntityTypes.DREAM_SPECK)
                .add(EntityType.ALLAY)
                .add(EntityType.VEX)
                .add(EntityType.GHAST)
                .add(EntityType.WARDEN)
                .add(EntityType.WITHER)
                .add(EntityType.SHULKER);

        getOrCreateTagBuilder(MirthdewEncoreEntityTypeTags.USES_DREAMSPECK_COLLISION)
                .add(MirthdewEncoreEntityTypes.DREAM_SPECK);
    }
}
