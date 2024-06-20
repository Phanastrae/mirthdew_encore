package phanastrae.mirthdew_encore.card_spell.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Optional;

public record FireEntityEffect(RegistryEntryList<EntityType<?>> entityTypes) {
    public static final MapCodec<FireEntityEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE).fieldOf("entity").forGetter(FireEntityEffect::entityTypes)
                    )
                    .apply(instance, FireEntityEffect::new)
    );

    public void castSpell(World world, Entity user) {
        Random random = user.getRandom();

        Optional<RegistryEntry<EntityType<?>>> OREET = this.entityTypes().getRandom(random);
        if(OREET.isPresent()) {
            Entity entity = OREET.get().value().create(world);
            if(entity != null) {
                if(entity instanceof ProjectileEntity projectileEntity) {
                    projectileEntity.setOwner(user);
                    if(entity instanceof PersistentProjectileEntity persistentProjectileEntity) {
                        persistentProjectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                    }
                }

                entity.setPosition(user.getEyePos());
                entity.setVelocity(user.getVelocity().add(user.getRotationVec(1).addRandom(random, 0.5f).multiply(2.4)));

                world.spawnEntity(entity);
            }
        }
    }
}