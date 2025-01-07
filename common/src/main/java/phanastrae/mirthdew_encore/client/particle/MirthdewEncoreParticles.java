package phanastrae.mirthdew_encore.client.particle;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import phanastrae.mirthdew_encore.particle.MirthdewEncoreParticleTypes;

public class MirthdewEncoreParticles {

    public static void init(ClientParticleRegistrar r) {
        r.register(MirthdewEncoreParticleTypes.DECIDRHEUM_LEAVES, LeavesParticle.Provider::new);
        r.register(MirthdewEncoreParticleTypes.SUNFLECK, SunfleckParticle.Provider::new);
        r.register(MirthdewEncoreParticleTypes.DRIPPING_VESPERBILE, MirthdewEncoreDripParticle::createVesperbileHangParticle);
        r.register(MirthdewEncoreParticleTypes.FALLING_VESPERBILE, MirthdewEncoreDripParticle::createVesperbileFallParticle);
        r.register(MirthdewEncoreParticleTypes.DRIPPING_DRIPSTONE_VESPERBILE, MirthdewEncoreDripParticle::createDripstoneVesperbileHangParticle);
        r.register(MirthdewEncoreParticleTypes.FALLING_DRIPSTONE_VESPERBILE, MirthdewEncoreDripParticle::createDripstoneVesperbileFallParticle);
    }

    public interface ClientParticleRegistrar {
        <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider);
        <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider.Sprite<T> provider);
        <T extends ParticleOptions> void register(ParticleType<T> type, ParticleRegistration<T> registration);
    }

    @FunctionalInterface
    public interface ParticleRegistration<T extends ParticleOptions> {
        ParticleProvider<T> create(SpriteSet sprites);
    }
}
