package phanastrae.mirthdew_encore.neoforge.mixin;

import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.common.extensions.IFluidExtension;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import phanastrae.mirthdew_encore.fluid.VesperbileFluid;
import phanastrae.mirthdew_encore.neoforge.fluid.MirthdewEncoreFluidTypes;

@Mixin(VesperbileFluid.class)
public abstract class VesperbileFluidMixin extends FlowingFluid implements IFluidExtension {
    // yeah self-mixin-ing is maybe kind of silly

    @Override
    public FluidType getFluidType() {
        return MirthdewEncoreFluidTypes.VESPERBILE;
    }
}
