package phanastrae.mirthdew_encore.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public BlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS)
                .add(MirthdewEncoreBlocks.DREAMTWIRL_BARRIER);
        getOrCreateTagBuilder(BlockTags.DRAGON_IMMUNE)
                .add(MirthdewEncoreBlocks.DREAMTWIRL_BARRIER);
        getOrCreateTagBuilder(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)
                .add(MirthdewEncoreBlocks.DREAMTWIRL_BARRIER);
        getOrCreateTagBuilder(BlockTags.WITHER_IMMUNE)
                .add(MirthdewEncoreBlocks.DREAMTWIRL_BARRIER);
    }
}
