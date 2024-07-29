package phanastrae.mirthdew_encore.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class MirthdewEncoreDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModelProvider::new);


		pack.addProvider(BlockTagProvider::new);
		pack.addProvider(EntityTypeTagProvider::new);
		pack.addProvider(DamageTypeTagProvider::new);
		pack.addProvider(BiomeTagProvider::new);

		pack.addProvider(RecipeProvider::new);

		pack.addProvider(BlockLootTableProvider::new);

		pack.addProvider(WorldGenerationProvider::new);
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.BIOME, WorldGenerationProvider::bootstrapBiomes);
		registryBuilder.add(Registries.DIMENSION_TYPE, WorldGenerationProvider::bootstrapDimensionTypes);
	}
}
