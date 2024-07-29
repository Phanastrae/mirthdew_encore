package phanastrae.mirthdew_encore.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class MirthdewEncoreDamageTypes {

    public static ResourceKey<DamageType> DREAMSNARE_TONGUE = ResourceKey.create(Registries.DAMAGE_TYPE, MirthdewEncore.id("dreamsnare_tongue"));

    public static DamageSource of(Level world, ResourceKey<DamageType> key) {
        return new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key));
    }
}
