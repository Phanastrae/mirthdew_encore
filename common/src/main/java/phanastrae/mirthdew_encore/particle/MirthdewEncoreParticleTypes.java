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

    public static void init(BiConsumer<ResourceLocation, ParticleType<?>> r) {
        r.accept(id("decidrheum_leaves"), DECIDRHEUM_LEAVES);
        r.accept(id("sunfleck"), SUNFLECK);
    }

    private static ResourceLocation id(String path) {
        return MirthdewEncore.id(path);
    }

    private static SimpleParticleType simple(boolean overrideLimiter) {
        return SimpleParticleTypeAccessor.invokeInit(overrideLimiter);
    }
}
