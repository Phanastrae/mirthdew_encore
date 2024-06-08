package phanastrae.mirthlight_encore.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class MirthlightEncoreDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(BlockTagProvider::new);
		pack.addProvider(ModelProvider::new);
		pack.addProvider(WorldGenerationProvider::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		registryBuilder.addRegistry(RegistryKeys.BIOME, WorldGenerationProvider::bootstrapBiomes);
		registryBuilder.addRegistry(RegistryKeys.DIMENSION_TYPE, WorldGenerationProvider::bootstrapDimensionTypes);
	}
}
