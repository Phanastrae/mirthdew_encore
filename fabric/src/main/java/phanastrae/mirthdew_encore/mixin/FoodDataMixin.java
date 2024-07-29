package phanastrae.mirthdew_encore.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.duck.HungerManagerDuckInterface;

@Mixin(FoodData.class)
public class FoodDataMixin implements HungerManagerDuckInterface {

    @Unique private int mirthdew_encore$foodLevelDebt = 0;

    @Inject(method = "add", at = @At("HEAD"))
    private void mirthdew_encore$collectFoodDebt(int nutrition, float saturation, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) LocalIntRef nutritionRef) {
        if(this.mirthdew_encore$foodLevelDebt > 0) {
            int consumedByDebt = Math.min(nutrition, mirthdew_encore$foodLevelDebt);
            this.mirthdew_encore$foodLevelDebt -= consumedByDebt;
            nutritionRef.set(nutrition - consumedByDebt);
        }
    }

    @Override
    public int mirthdew_encore$getFoodLevelDebt() {
        return this.mirthdew_encore$foodLevelDebt;
    }

    @Override
    public void mirthdew_encore$setFoodLevelDebt(int foodLevelDebt) {
        this.mirthdew_encore$foodLevelDebt = foodLevelDebt;
    }
}
