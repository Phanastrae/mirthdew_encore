package phanastrae.mirthdew_encore.mixin;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
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

@Mixin(World.class)
public class WorldMixin implements WorldDuckInterface {

    @Shadow
    @Final
    private RegistryEntry<DimensionType> dimensionEntry;
    @Mutable
    @Shadow @Final private WorldBorder border;
    @Unique
    private DreamtwirlWorldAttachment mirthdew_encore$dreamtwirlAttachment;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mirthdew_encore$init(MutableWorldProperties properties, RegistryKey registryRef, DynamicRegistryManager registryManager, RegistryEntry dimensionEntry, Supplier profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates, CallbackInfo ci) {
        World thisWorld = (World)(Object)this;
        if(this.dimensionEntry.matchesKey(MirthdewEncoreDimensions.DREAMTWIRL_DIM_TYPE)) {
            this.mirthdew_encore$dreamtwirlAttachment = new DreamtwirlWorldAttachment(thisWorld);
            this.border = new DreamtwirlWorldBorder(mirthdew_encore$dreamtwirlAttachment);
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
