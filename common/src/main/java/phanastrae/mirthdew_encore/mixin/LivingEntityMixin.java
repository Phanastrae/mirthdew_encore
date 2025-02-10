package phanastrae.mirthdew_encore.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.component.type.FoodWhenFullProperties;
import phanastrae.mirthdew_encore.dreamtwirl.EntityDreamtwirlData;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityAttachment;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreFluidTags;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {

    @Shadow
    public abstract float getSpeed();

    @Shadow public abstract boolean addEffect(MobEffectInstance effectInstance);

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/food/FoodProperties;)V", shift = At.Shift.AFTER))
    private void mirthdew_encore$applySpectralCandyEffect(Level world, ItemStack stack, FoodProperties foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        if (thisEntity instanceof Player player) {
            if (player.getFoodData().getFoodLevel() == 20) {
                FoodWhenFullProperties food = stack.get(MirthdewEncoreDataComponentTypes.FOOD_WHEN_FULL);
                if(food != null) {
                    for(FoodProperties.PossibleEffect possibleEffect : food.effects()) {
                        if (this.random.nextFloat() < possibleEffect.probability()) {
                            this.addEffect(possibleEffect.effect());
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "onBelowWorld", at = @At(value = "HEAD"), cancellable = true)
    private void mirthdew_encore$cancelVoidTick(CallbackInfo ci) {
        LivingEntity thisEntity = (LivingEntity)(Object)this;

        // do not teleport spectators (or other no-clipping players)
        if(thisEntity instanceof Player player && player.noPhysics) {
            return;
        }

        EntityDreamtwirlData dreamtwirlData = MirthdewEncoreEntityAttachment.fromEntity(thisEntity).getDreamtwirlEntityData();
        if(dreamtwirlData.isInDreamtwirl() && dreamtwirlData.canLeave()) {
            if(dreamtwirlData.leaveDreamtwirl()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "getFluidFallingAdjustedMovement", at = @At("HEAD"), cancellable = true)
    private void mirthdewEncore$vesperbileGravityPhysics(double gravity, boolean isFalling, Vec3 deltaMovement, CallbackInfoReturnable<Vec3> cir) {
        if(this.getFluidHeight(MirthdewEncoreFluidTags.VESPERBILE) > 0.0) {
            double dy = deltaMovement.y;
            if(isFalling) {
                dy -= gravity * 0.92;
            } else {
                dy -= gravity * 0.035;
                dy *= 0.96;
            }

            Vec3 out = new Vec3(deltaMovement.x, dy, deltaMovement.z);
            cir.setReturnValue(out);
        }
    }

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 1))
    private void mirthdewEncore$vesperbileMovementPhysics_1(Vec3 travelVector, CallbackInfo ci, @Local(ordinal = 1) LocalFloatRef LR_moveAmount) {
        if(this.getFluidHeight(MirthdewEncoreFluidTags.VESPERBILE) > 0.0) {
            // get horizontal parts of movement vector and input vector
            Vec3 dmH = this.getDeltaMovement().multiply(1, 0, 1);
            Vec3 iv = EntityAccessor.invokeGetInputVector(travelVector, 1, this.getYRot());

            // limit magnitudes, so that past a certain point higher velocities have no effect
            double dmHL = dmH.horizontalDistance();
            double dmHLMax = 0.5;
            if(dmHL > dmHLMax) {
                dmH.scale(dmHLMax / dmHL);
                dmHL = dmHLMax;
            }

            // take dot product
            double dot = dmH.dot(iv);
            // renormalise to get value in range [-1, 1]
            // a value of 1 is achieved by 1) moving sufficiently fast 2) inputting enough 3) looking directly the direction you are moving
            // a value of -1 is achieved by instead looking opposite to the direction you are moving
            // a value of 0 is achieved by either a) not moving b) not inputting anything or c) looking perpendicular to the direction you are moving
            double normDot = dot / dmHLMax;

            // transform to range [0, 1], with 0 -> 1, 1 -> 0, -1 -> 0
            double u = 1 - normDot * normDot;

            // multiply by dmHL and lvHL, so that max value requires a) moving fast b) inputting enough and c) looking perpendicular to movement
            double v = u * (dmHL / dmHLMax);

            double relSpeed = dmHL / 0.5;

            // calc speed multiplier
            //float speedMultiplier = (float)(0.6 * (1 - normDot) + w * 8.5);
            float speedMultiplier = 0.5F + (float)(v * 0.4 + relSpeed * relSpeed * relSpeed * 1.1);

            float speed = this.getSpeed() * speedMultiplier;
            LR_moveAmount.set(speed);
        }
    }

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V", ordinal = 0))
    private void mirthdewEncore$vesperbileMovementPhysics_2(Vec3 travelVector, CallbackInfo ci, @Local(ordinal = 0) LocalFloatRef LR_waterSlowdown) {
        if(this.getFluidHeight(MirthdewEncoreFluidTags.VESPERBILE) > 0.0) {
            if(this.getDeltaMovement().y <= 0 || this.onGround()) {
                LR_waterSlowdown.set(0.78F);
            } else {
                LR_waterSlowdown.set(0.83F);
            }
        }
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInWater()Z", ordinal = 0))
    private void mirthdewEncore$vesperbileJump(CallbackInfo ci) {
        double vesperbileHeight = this.getFluidHeight(MirthdewEncoreFluidTags.VESPERBILE);
        double fluidJumpThreshold = this.getFluidJumpThreshold();
        if(vesperbileHeight > 0.0) {
            if(!this.onGround() || vesperbileHeight > fluidJumpThreshold) {
                if(this.onGround()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.5F, 0.0));
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.18F, 0.0));
                }
            }
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"))
    private void mirthdewEncore$onJump(CallbackInfo ci) {
        MirthdewEncoreEntityAttachment.fromEntity(this).onJump();
    }
}
