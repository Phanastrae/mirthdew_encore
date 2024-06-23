package phanastrae.mirthdew_encore.entity;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class MirthdewEncoreDamageTypes {

    public static RegistryKey<DamageType> DREAMSNARE_TONGUE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, MirthdewEncore.id("dreamsnare_tongue"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
}
