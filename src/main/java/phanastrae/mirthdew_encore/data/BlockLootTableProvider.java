package phanastrae.mirthdew_encore.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;

import java.util.concurrent.CompletableFuture;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {
    protected BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(MirthdewEncoreBlocks.SLUMBERSOCKET);
        addDropWithSilkTouch(MirthdewEncoreBlocks.DREAMSEED);
        addDropWithSilkTouch(MirthdewEncoreBlocks.VERIC_DREAMSNARE);

        addDrop(MirthdewEncoreBlocks.SLUMBERVEIL, dropsNothing());
    }
}
