package phanastrae.mirthdew_encore.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.dreamtwirl.EntityDreamtwirlData;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityAttachment;
import phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/food/FoodProperties;)V", shift = At.Shift.AFTER))
    private void mirthdew_encore$applySpectralCandyEffect(Level world, ItemStack stack, FoodProperties foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        if(stack.is(MirthdewEncoreItems.SPECTRAL_CANDY)) { // TODO make this an item component in the future
            LivingEntity thisEntity = (LivingEntity) (Object) this;
            if (thisEntity instanceof Player player) {
                if (player.getFoodData().getFoodLevel() == 20) {
                    thisEntity.addEffect(new MobEffectInstance(MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY, 3000, 2));
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
}
