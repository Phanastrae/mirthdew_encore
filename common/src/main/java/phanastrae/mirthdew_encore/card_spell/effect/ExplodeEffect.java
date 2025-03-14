package phanastrae.mirthdew_encore.card_spell.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public record ExplodeEffect(
        boolean attributeToUser,
        Optional<Holder<DamageType>> damageType,
        float knockbackMultiplier,
        Optional<HolderSet<Block>> immuneBlocks,
        Vec3 offset,
        float radius,
        boolean createFire,
        Level.ExplosionInteraction blockInteraction,
        ParticleOptions smallParticle,
        ParticleOptions largeParticle,
        Holder<SoundEvent> sound
) {
    public static final MapCodec<ExplodeEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codec.BOOL.optionalFieldOf("attribute_to_user", Boolean.FALSE).forGetter(ExplodeEffect::attributeToUser),
                            DamageType.CODEC.optionalFieldOf("damage_type").forGetter(ExplodeEffect::damageType),
                            Codec.FLOAT.fieldOf("knockback_multiplier").forGetter(ExplodeEffect::knockbackMultiplier),
                            RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("immune_blocks").forGetter(ExplodeEffect::immuneBlocks),
                            Vec3.CODEC.optionalFieldOf("offset", Vec3.ZERO).forGetter(ExplodeEffect::offset),
                            Codec.FLOAT.fieldOf("radius").forGetter(ExplodeEffect::radius),
                            Codec.BOOL.optionalFieldOf("create_fire", Boolean.FALSE).forGetter(ExplodeEffect::createFire),
                            Level.ExplosionInteraction.CODEC.fieldOf("block_interaction").forGetter(ExplodeEffect::blockInteraction),
                            ParticleTypes.CODEC.fieldOf("small_particle").forGetter(ExplodeEffect::smallParticle),
                            ParticleTypes.CODEC.fieldOf("large_particle").forGetter(ExplodeEffect::largeParticle),
                            SoundEvent.CODEC.fieldOf("sound").forGetter(ExplodeEffect::sound)
                    )
                    .apply(instance, ExplodeEffect::new)
    );

    public void apply(ServerLevel world, Entity user) {
        Vec3 vec3d = user.position().add(this.offset);
        world.explode(
                this.attributeToUser ? user : null,
                this.getDamageSource(user, vec3d),
                new SimpleExplosionDamageCalculator(
                        this.blockInteraction != Level.ExplosionInteraction.NONE,
                        this.damageType.isPresent(),
                        Optional.of(this.knockbackMultiplier),
                        this.immuneBlocks
                ),
                vec3d.x(),
                vec3d.y(),
                vec3d.z(),
                Math.max(this.radius, 0.0F),
                this.createFire,
                this.blockInteraction,
                this.smallParticle,
                this.largeParticle,
                this.sound
        );
    }

    @Nullable
    private DamageSource getDamageSource(Entity user, Vec3 pos) {
        return this.damageType.map(damageTypeRegistryEntry -> this.attributeToUser
                ? new DamageSource(damageTypeRegistryEntry, user)
                : new DamageSource(damageTypeRegistryEntry, pos)).orElse(null);
    }
}
