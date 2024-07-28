package phanastrae.mirthdew_encore.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlWorldAttachment;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlWorldBorder;
import phanastrae.mirthdew_encore.duck.WorldDuckInterface;
import phanastrae.mirthdew_encore.world.dimension.MirthdewEncoreDimensions;

import java.util.function.Supplier;

@Mixin(Level.class)
public class LevelMixin implements WorldDuckInterface {

    @Shadow
    @Final
    private Holder<DimensionType> dimensionTypeRegistration;
    @Mutable
    @Shadow @Final private WorldBorder worldBorder;
    @Unique
    private DreamtwirlWorldAttachment mirthdew_encore$dreamtwirlAttachment;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mirthdew_encore$init(WritableLevelData properties, ResourceKey registryRef, RegistryAccess registryManager, Holder dimensionEntry, Supplier profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates, CallbackInfo ci) {
        Level thisWorld = (Level)(Object)this;
        if(this.dimensionTypeRegistration.is(MirthdewEncoreDimensions.DREAMTWIRL_DIM_TYPE)) {
            this.mirthdew_encore$dreamtwirlAttachment = new DreamtwirlWorldAttachment(thisWorld);
            this.worldBorder = new DreamtwirlWorldBorder(mirthdew_encore$dreamtwirlAttachment);
        } else {
            this.mirthdew_encore$dreamtwirlAttachment = null;
        }
    }

    @Override
    @Nullable
    public DreamtwirlWorldAttachment mirthdew_encore$getDreamtwirlAttachment() {
        return this.mirthdew_encore$dreamtwirlAttachment;
    }
}
