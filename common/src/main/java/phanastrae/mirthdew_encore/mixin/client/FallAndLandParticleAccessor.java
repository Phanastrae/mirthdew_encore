package phanastrae.mirthdew_encore.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DripParticle.FallAndLandParticle.class)
public interface FallAndLandParticleAccessor {

    @Invoker("<init>")
    static DripParticle.FallAndLandParticle invokeInit(ClientLevel level, double x, double y, double z, Fluid type, ParticleOptions fallingParticle) {
        throw new AssertionError();
    }
}
