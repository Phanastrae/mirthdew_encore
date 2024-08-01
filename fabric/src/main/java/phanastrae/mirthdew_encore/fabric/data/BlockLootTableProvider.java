package phanastrae.mirthdew_encore.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;

import java.util.concurrent.CompletableFuture;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {
    protected BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        dropSelf(MirthdewEncoreBlocks.SLUMBERSOCKET);
        dropWhenSilkTouch(MirthdewEncoreBlocks.DREAMSEED);
        dropWhenSilkTouch(MirthdewEncoreBlocks.VERIC_DREAMSNARE);

        add(MirthdewEncoreBlocks.SLUMBERVEIL, noDrop());
    }
}
