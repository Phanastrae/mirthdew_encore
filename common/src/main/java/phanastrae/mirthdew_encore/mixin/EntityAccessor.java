package phanastrae.mirthdew_encore.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Invoker
    static Vec3 invokeGetInputVector(Vec3 relative, float motionScaler, float facing) {
        throw new AssertionError();
    };
}
