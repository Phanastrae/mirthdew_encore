package phanastrae.mirthdew_encore.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.Biome;
import phanastrae.mirthdew_encore.world.biome.MirthdewEncoreBiomes;

import java.util.concurrent.CompletableFuture;

public class BiomeTagProvider extends FabricTagProvider<Biome> {

    public BiomeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BIOME, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BiomeTags.WITHOUT_PATROL_SPAWNS)
                .add(MirthdewEncoreBiomes.DREAMTWIRL);
        getOrCreateTagBuilder(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS)
                .add(MirthdewEncoreBiomes.DREAMTWIRL);
        getOrCreateTagBuilder(BiomeTags.WITHOUT_ZOMBIE_SIEGES)
                .add(MirthdewEncoreBiomes.DREAMTWIRL);

        getOrCreateTagBuilder(ConventionalBiomeTags.NO_DEFAULT_MONSTERS)
                .add(MirthdewEncoreBiomes.DREAMTWIRL);
        getOrCreateTagBuilder(ConventionalBiomeTags.IS_VOID)
                .add(MirthdewEncoreBiomes.DREAMTWIRL);
    }
}
