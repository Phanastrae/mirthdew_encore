package phanastrae.mirthdew_encore.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import phanastrae.mirthdew_encore.world.biome.MirthdewEncoreBiomes;

import java.util.concurrent.CompletableFuture;

public class BiomeTagProvider extends FabricTagProvider<Biome> {

    public BiomeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BIOME, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        tag(BiomeTags.WITHOUT_PATROL_SPAWNS)
                .add(MirthdewEncoreBiomes.DREAMTWIRL);
        tag(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS)
                .add(MirthdewEncoreBiomes.DREAMTWIRL);
        tag(BiomeTags.WITHOUT_ZOMBIE_SIEGES)
                .add(MirthdewEncoreBiomes.DREAMTWIRL);

        tag(ConventionalBiomeTags.NO_DEFAULT_MONSTERS)
                .add(MirthdewEncoreBiomes.DREAMTWIRL);
        tag(ConventionalBiomeTags.IS_VOID)
                .add(MirthdewEncoreBiomes.DREAMTWIRL);
    }
}
