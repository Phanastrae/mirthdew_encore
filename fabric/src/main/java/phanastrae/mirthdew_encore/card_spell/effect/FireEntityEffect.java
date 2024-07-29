package phanastrae.mirthdew_encore.card_spell.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record FireEntityEffect(HolderSet<EntityType<?>> entityTypes, float speed, float divergence, CompoundTag nbt) {
    public static final MapCodec<FireEntityEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("entity").forGetter(FireEntityEffect::entityTypes),
                            Codec.FLOAT.optionalFieldOf("speed", 1.0F).forGetter(FireEntityEffect::speed),
                            Codec.FLOAT.optionalFieldOf("divergence", 1.0F).forGetter(FireEntityEffect::divergence),
                            CompoundTag.CODEC.optionalFieldOf("nbt", new CompoundTag()).forGetter(FireEntityEffect::nbt)
                    )
                    .apply(instance, FireEntityEffect::new)
    );

    public void castSpell(Level world, Entity user) {
        RandomSource random = user.getRandom();

        Optional<Holder<EntityType<?>>> OREET = this.entityTypes().getRandomElement(random);
        if(OREET.isPresent()) {
            Entity entity = OREET.get().value().create(world);
            if(entity != null) {
                entity.load(this.nbt);
                if(entity instanceof Projectile projectileEntity) {
                    projectileEntity.setOwner(user);
                }

                entity.setPos(user.getEyePosition());
                Vec3 relativeVelocity = user.getViewVector(1)
                        .add(
                                random.triangle(0.0, 0.0172275 * divergence),
                                random.triangle(0.0, 0.0172275 * divergence),
                                random.triangle(0.0, 0.0172275 * divergence)
                        ).scale(speed);
                entity.setDeltaMovement(user.getDeltaMovement().add(relativeVelocity));

                world.addFreshEntity(entity);
            }
        }
    }
}