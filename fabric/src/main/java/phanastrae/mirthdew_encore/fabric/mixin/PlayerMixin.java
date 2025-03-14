package phanastrae.mirthdew_encore.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.duck.PlayerDuckInterface;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreFluidTags;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerDuckInterface {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow protected abstract float getFlyingSpeed();

    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void mirthdew_encore$fluidStepSounds(BlockPos pos, BlockState state, CallbackInfo ci) {
        if(this.getFluidHeight(MirthdewEncoreFluidTags.VESPERBILE) > 0.0) {
            this.waterSwimSound();
            this.playMuffledStepSound(state);
            ci.cancel();
        }
    }
}
