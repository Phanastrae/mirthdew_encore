package phanastrae.mirthdew_encore.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;

import java.util.concurrent.CompletableFuture;

import static phanastrae.mirthdew_encore.entity.MirthdewEncoreDamageTypes.DREAMSNARE_TONGUE;

public class DamageTypeTagProvider extends FabricTagProvider<DamageType> {

    public DamageTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, RegistryKeys.DAMAGE_TYPE, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
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
