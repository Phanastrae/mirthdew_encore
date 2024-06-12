package phanastrae.mirthdew_encore.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.MusicType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.biome.*;
import net.minecraft.world.dimension.DimensionType;
import phanastrae.mirthdew_encore.world.biome.MirthdewEncoreBiomes;
import phanastrae.mirthdew_encore.world.dimension.MirthdewEncoreDimensions;

import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;

public class WorldGenerationProvider extends FabricDynamicRegistryProvider {

    public WorldGenerationProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.addAll(registries.getWrapperOrThrow(RegistryKeys.BIOME));
        entries.addAll(registries.getWrapperOrThrow(RegistryKeys.DIMENSION_TYPE));
    }

    @Override
    public String getName() {
        return "World Generation";
    }

    public static void bootstrapBiomes(Registerable<Biome> context) {
        BiomeEffects.Builder builder = new BiomeEffects.Builder()
                .fogColor(0x031717)
                .waterColor(0x0FEFFF)
                .waterFogColor(0x021414)
                .skyColor(0x074F4F)
                .foliageColor(FoliageColors.getColor(0.5, 0.5))
                .grassColor(GrassColors.getColor(0.5, 0.5))
                .music(MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_SOUL_SAND_VALLEY));

        context.register(
                MirthdewEncoreBiomes.DREAMTWIRL,
                new Biome.Builder()
                        .precipitation(false)
                        .temperature(0.5F)
                        .downfall(0.0F)
                        .effects(builder.build())
                        .spawnSettings(SpawnSettings.INSTANCE)
                        .generationSettings(GenerationSettings.INSTANCE)
                        .build()
        );
    }

    public static void bootstrapDimensionTypes(Registerable<DimensionType> context) {
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
                        new DimensionType.MonsterSettings(true, false, UniformIntProvider.create(0, 7), 0)
                ));
    }
}
