package phanastrae.mirthdew_encore.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.mixin.SimpleParticleTypeAccessor;

import java.util.function.BiConsumer;

public class MirthdewEncoreParticleTypes {

    public static final SimpleParticleType DECIDRHEUM_LEAVES = simple(false);
    public static final SimpleParticleType SUNFLECK = simple(false);
    public static final SimpleParticleType DRIPPING_VESPERBILE = simple(false);
    public static final SimpleParticleType FALLING_VESPERBILE = simple(false);
    public static final SimpleParticleType DRIPPING_DRIPSTONE_VESPERBILE = simple(false);
    public static final SimpleParticleType FALLING_DRIPSTONE_VESPERBILE = simple(false);

    public static void init(BiConsumer<ResourceLocation, ParticleType<?>> r) {
        r.accept(id("decidrheum_leaves"), DECIDRHEUM_LEAVES);
        r.accept(id("sunfleck"), SUNFLECK);
        r.accept(id("dripping_vesperbile"), DRIPPING_VESPERBILE);
        r.accept(id("falling_vesperbile"), FALLING_VESPERBILE);
        r.accept(id("dripping_dripstone_vesperbile"), DRIPPING_DRIPSTONE_VESPERBILE);
        r.accept(id("falling_dripstone_vesperbile"), FALLING_DRIPSTONE_VESPERBILE);
    }

    private static ResourceLocation id(String path) {
        return MirthdewEncore.id(path);
    }

    private static SimpleParticleType simple(boolean overrideLimiter) {
        return SimpleParticleTypeAccessor.invokeInit(overrideLimiter);
    }
}
