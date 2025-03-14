package phanastrae.mirthdew_encore.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import phanastrae.mirthdew_encore.fluid.MirthdewEncoreFluids;
import phanastrae.mirthdew_encore.mixin.client.DripHangParticleAccessor;
import phanastrae.mirthdew_encore.mixin.client.DripstoneFallAndLandParticleAccessor;
import phanastrae.mirthdew_encore.mixin.client.FallAndLandParticleAccessor;
import phanastrae.mirthdew_encore.particle.MirthdewEncoreParticleTypes;

public class MirthdewEncoreDripParticle {

    public static TextureSheetParticle createVesperbileHangParticle(
            SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
    ) {
        DripParticle dripParticle = DripHangParticleAccessor.invokeInit(level, x, y, z, MirthdewEncoreFluids.VESPERBILE, MirthdewEncoreParticleTypes.FALLING_VESPERBILE);
        dripParticle.setColor(0.65F, 0.25F, 0.15F);
        return dripParticle;
    }

    public static TextureSheetParticle createVesperbileFallParticle(
            SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
    ) {
        DripParticle dripParticle = FallAndLandParticleAccessor.invokeInit(level, x, y, z, MirthdewEncoreFluids.VESPERBILE, MirthdewEncoreParticleTypes.SUNFLECK);
        dripParticle.setColor(0.65F, 0.25F, 0.15F);
        return dripParticle;
    }

    public static TextureSheetParticle createDripstoneVesperbileHangParticle(
            SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
    ) {
        DripParticle dripParticle = DripHangParticleAccessor.invokeInit(level, x, y, z, MirthdewEncoreFluids.VESPERBILE, MirthdewEncoreParticleTypes.FALLING_DRIPSTONE_VESPERBILE);
        dripParticle.setColor(0.65F, 0.25F, 0.15F);
        return dripParticle;
    }

    public static TextureSheetParticle createDripstoneVesperbileFallParticle(
            SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
    ) {
        DripParticle dripParticle = DripstoneFallAndLandParticleAccessor.invokeInit(level, x, y, z, MirthdewEncoreFluids.VESPERBILE, MirthdewEncoreParticleTypes.SUNFLECK);
        dripParticle.setColor(0.65F, 0.25F, 0.15F);
        return dripParticle;
    }
}
