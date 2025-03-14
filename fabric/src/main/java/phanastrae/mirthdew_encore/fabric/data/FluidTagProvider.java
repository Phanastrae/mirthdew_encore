package phanastrae.mirthdew_encore.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import phanastrae.mirthdew_encore.fluid.MirthdewEncoreFluids;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreFluidTags;

import java.util.concurrent.CompletableFuture;

public class FluidTagProvider extends FabricTagProvider.FluidTagProvider {

    public FluidTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(MirthdewEncoreFluidTags.VESPERBILE)
                .add(
                        MirthdewEncoreFluids.VESPERBILE,
                        MirthdewEncoreFluids.FLOWING_VESPERBILE
                );
    }
}
