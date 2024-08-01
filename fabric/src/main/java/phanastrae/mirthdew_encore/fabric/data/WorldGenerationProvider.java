package phanastrae.mirthdew_encore.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.dimension.DimensionType;
import phanastrae.mirthdew_encore.world.biome.MirthdewEncoreBiomes;
import phanastrae.mirthdew_encore.world.dimension.MirthdewEncoreDimensions;

import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;

public class WorldGenerationProvider extends FabricDynamicRegistryProvider {

    public WorldGenerationProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(Registries.BIOME));
        entries.addAll(registries.lookupOrThrow(Registries.DIMENSION_TYPE));
    }

    @Override
    public String getName() {
        return "World Generation";
    }

    public static void bootstrapBiomes(BootstrapContext<Biome> context) {
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder()
                .fogColor(0x031717)
                .waterColor(0x0FEFFF)
                .waterFogColor(0x021414)
                .skyColor(0x074F4F)
                .foliageColorOverride(0xFF165E6E)
                .grassColorOverride(0xFF328BA6)
                .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SOUL_SAND_VALLEY));

        context.register(
                MirthdewEncoreBiomes.DREAMTWIRL,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.5F)
                        .downfall(0.0F)
                        .specialEffects(builder.build())
                        .mobSpawnSettings(MobSpawnSettings.EMPTY)
                        .generationSettings(BiomeGenerationSettings.EMPTY)
                        .build()
        );
    }

    public static void bootstrapDimensionTypes(BootstrapContext<DimensionType> context) {
        context.register(
                MirthdewEncoreDimensions.DREAMTWIRL_DIM_TYPE,
                new DimensionType(
                        OptionalLong.of(18000L),
                        false,
                        false,
                        false,
                        false,
                        1.0,
                        false,
                        false,
                        0,
                        256,
                        256,
                        BlockTags.INFINIBURN_OVERWORLD,
                        MirthdewEncoreDimensions.DREAMTWIRL_ID,
                        0.2F,
                        new DimensionType.MonsterSettings(true, false, UniformInt.of(0, 7), 0)
                ));
    }
}
