package phanastrae.mirthdew_encore.fabric.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreFluidTags;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;applyGravity()V", ordinal = 0))
    private void mirthdewEncore$vesperbileGravity(CallbackInfo ci) {
        if(this.getFluidHeight(MirthdewEncoreFluidTags.VESPERBILE) > 0.0) {
            float gravityMultiplier = 0.25F;

            double gravity = this.getGravity();
            if (gravity != 0.0) {
                // include a gravity * +1 to counteract the baseline gravity
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, gravity * (1 - gravityMultiplier), 0.0));
            }
        }
    }
}
