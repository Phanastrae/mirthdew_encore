package phanastrae.mirthdew_encore.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.particle.MirthdewEncoreParticleTypes;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreFluidTags;

@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlockMixin extends Block implements Fallable, SimpleWaterloggedBlock {
    public PointedDripstoneBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "spawnDripParticle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), cancellable = true)
    private static void mirthdewEncore$vesperbileParticle(Level level, BlockPos pos, BlockState state, Fluid p_fluid, CallbackInfo ci, @Local(ordinal = 0) Vec3 vec3, @Local(ordinal = 1) Fluid fluid) {
        if(p_fluid.is(MirthdewEncoreFluidTags.VESPERBILE)) {
            double d1 = (double)pos.getX() + 0.5 + vec3.x;
            double d2 = (double)((float)(pos.getY() + 1) - 0.6875F) - 0.0625;
            double d3 = (double)pos.getZ() + 0.5 + vec3.z;
            ParticleOptions particleoptions = MirthdewEncoreParticleTypes.DRIPPING_DRIPSTONE_VESPERBILE;
            level.addParticle(particleoptions, d1, d2, d3, 0.0, 0.0, 0.0);
            ci.cancel();
        }
    }
}
