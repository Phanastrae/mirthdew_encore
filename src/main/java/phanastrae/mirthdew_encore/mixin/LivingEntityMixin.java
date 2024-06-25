package phanastrae.mirthdew_encore.mixin;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyFoodEffects(Lnet/minecraft/component/type/FoodComponent;)V", shift = At.Shift.AFTER))
    private void mirthdew_encore$applySpectralCandyEffect(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        if(stack.isOf(MirthdewEncoreItems.SPECTRAL_CANDY)) { // TODO make this an item component in the future
            LivingEntity thisEntity = (LivingEntity) (Object) this;
            if (thisEntity instanceof PlayerEntity player) {
                if (player.getHungerManager().getFoodLevel() == 20) {
                    thisEntity.addStatusEffect(new StatusEffectInstance(MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY, 3000, 2));
                }
            }
        }
    }
}
