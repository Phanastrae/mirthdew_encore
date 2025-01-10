package phanastrae.mirthdew_encore.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FoodProperties.PossibleEffect.class)
public interface FoodPropertiesPossibleEffectAccessor {
    // neoforge makes the constructor private because it wants a supplier instead

    @Invoker("<init>")
    static FoodProperties.PossibleEffect invokeInit(MobEffectInstance effect, float probability) {
        throw new AssertionError();
    }
}
