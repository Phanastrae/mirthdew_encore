package phanastrae.mirthdew_encore.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreEntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public EntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(EntityTypeTags.FALL_DAMAGE_IMMUNE)
                .add(MirthdewEncoreEntityTypes.DREAMSPECK);

        getOrCreateTagBuilder(MirthdewEncoreEntityTypeTags.DREAMSPECK_OPAQUE)
                .add(MirthdewEncoreEntityTypes.DREAMSPECK)
                .add(EntityType.ALLAY)
                .add(EntityType.VEX)
                .add(EntityType.GHAST)
                .add(EntityType.WARDEN)
                .add(EntityType.WITHER)
                .add(EntityType.SHULKER);

        getOrCreateTagBuilder(MirthdewEncoreEntityTypeTags.USES_DREAMSPECK_COLLISION)
                .add(MirthdewEncoreEntityTypes.DREAMSPECK);
    }
}
