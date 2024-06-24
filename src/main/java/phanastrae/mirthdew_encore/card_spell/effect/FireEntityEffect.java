package phanastrae.mirthdew_encore.card_spell.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Optional;

public record FireEntityEffect(RegistryEntryList<EntityType<?>> entityTypes, float speed, float divergence, NbtCompound nbt) {
    public static final MapCodec<FireEntityEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE).fieldOf("entity").forGetter(FireEntityEffect::entityTypes),
                            Codec.FLOAT.optionalFieldOf("speed", 1.0F).forGetter(FireEntityEffect::speed),
                            Codec.FLOAT.optionalFieldOf("divergence", 1.0F).forGetter(FireEntityEffect::divergence),
                            NbtCompound.CODEC.optionalFieldOf("nbt", new NbtCompound()).forGetter(FireEntityEffect::nbt)
                    )
                    .apply(instance, FireEntityEffect::new)
    );

    public void castSpell(World world, Entity user) {
        Random random = user.getRandom();

        Optional<RegistryEntry<EntityType<?>>> OREET = this.entityTypes().getRandom(random);
        if(OREET.isPresent()) {
            Entity entity = OREET.get().value().create(world);
            if(entity != null) {
                entity.readNbt(this.nbt);
                if(entity instanceof ProjectileEntity projectileEntity) {
                    projectileEntity.setOwner(user);
                }

                entity.setPosition(user.getEyePos());
                Vec3d relativeVelocity = user.getRotationVec(1)
                        .add(
                                random.nextTriangular(0.0, 0.0172275 * divergence),
                                random.nextTriangular(0.0, 0.0172275 * divergence),
                                random.nextTriangular(0.0, 0.0172275 * divergence)
                        ).multiply(speed);
                entity.setVelocity(user.getVelocity().add(relativeVelocity));

                world.spawnEntity(entity);
            }
        }
    }
}