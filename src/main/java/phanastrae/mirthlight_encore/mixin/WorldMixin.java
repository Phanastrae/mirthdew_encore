package phanastrae.mirthlight_encore.mixin;

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
import phanastrae.mirthlight_encore.dreamtwirl.DreamtwirlWorldAttachment;
import phanastrae.mirthlight_encore.dreamtwirl.DreamtwirlWorldBorder;
import phanastrae.mirthlight_encore.duck.WorldDuckInterface;
import phanastrae.mirthlight_encore.world.dimension.MirthlightEncoreDimensions;

import java.util.function.Supplier;

@Mixin(World.class)
public class WorldMixin implements WorldDuckInterface {

    @Shadow
    @Final
    private RegistryEntry<DimensionType> dimensionEntry;
    @Mutable
    @Shadow @Final private WorldBorder border;
    @Unique
    private DreamtwirlWorldAttachment mirthlight_encore$dreamtwirlAttachment;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mirthlight_encore$init(MutableWorldProperties properties, RegistryKey registryRef, DynamicRegistryManager registryManager, RegistryEntry dimensionEntry, Supplier profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates, CallbackInfo ci) {
        World thisWorld = (World)(Object)this;
        if(this.dimensionEntry.matchesKey(MirthlightEncoreDimensions.DREAMTWIRL_DIM_TYPE)) {
            this.mirthlight_encore$dreamtwirlAttachment = new DreamtwirlWorldAttachment(thisWorld);
            this.border = new DreamtwirlWorldBorder(mirthlight_encore$dreamtwirlAttachment);
        } else {
            this.mirthlight_encore$dreamtwirlAttachment = null;
        }
    }

    @Override
    @Nullable
    public DreamtwirlWorldAttachment mirthlight_encore$getDreamtwirlAttachment() {
        return this.mirthlight_encore$dreamtwirlAttachment;
    }
}
