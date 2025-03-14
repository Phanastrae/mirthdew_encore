package phanastrae.mirthdew_encore.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class BaccheniteGlimmerParticle extends TextureSheetParticle {

    BaccheniteGlimmerParticle(
            ClientLevel level, SpriteSet sprites, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
    ) {
        super(level, x, y - 0.125, z, xSpeed, ySpeed, zSpeed);
        this.setSize(0.01F, 0.01F);
        this.pickSprite(sprites);
        this.quadSize = this.quadSize * (this.random.nextFloat() * 0.2F + 1.3F) * 1.3F;
        this.lifetime = (int)(48.0 / (Math.random() * 0.8 + 0.2));
        this.hasPhysics = false;
        this.friction = 1.0F;
        this.gravity = 0.0F;

        this.setParticleSpeed(xSpeed, ySpeed, zSpeed);
    }

    @Override
    public int getLightColor(float partialTick) {
        float f = ((float)this.age + partialTick) / (float)this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(partialTick);
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        j += (int)(f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class BaccheniteGlimmerProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public BaccheniteGlimmerProvider(SpriteSet sprites) {
            this.sprite = sprites;
        }

        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            BaccheniteGlimmerParticle glimmerParticle = new BaccheniteGlimmerParticle(level, this.sprite, x, y, z, xSpeed, ySpeed, zSpeed);
            glimmerParticle.setColor(0.9F, 0.4F, 0.9F);
            return glimmerParticle;
        }
    }
}
