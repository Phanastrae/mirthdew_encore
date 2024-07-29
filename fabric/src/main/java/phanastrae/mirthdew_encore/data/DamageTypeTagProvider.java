package phanastrae.mirthdew_encore.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

import java.util.concurrent.CompletableFuture;

import static phanastrae.mirthdew_encore.entity.MirthdewEncoreDamageTypes.DREAMSNARE_TONGUE;

public class DamageTypeTagProvider extends FabricTagProvider<DamageType> {

    public DamageTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, Registries.DAMAGE_TYPE, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_ARMOR)
                .addOptional(DREAMSNARE_TONGUE);
        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                .addOptional(DREAMSNARE_TONGUE);
        getOrCreateTagBuilder(DamageTypeTags.IS_PROJECTILE)
                .addOptional(DREAMSNARE_TONGUE);
        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_WOLF_ARMOR)
                .addOptional(DREAMSNARE_TONGUE);
        getOrCreateTagBuilder(DamageTypeTags.PANIC_CAUSES)
                .addOptional(DREAMSNARE_TONGUE);
    }
}
