package phanastrae.mirthdew_encore.fabric.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.fluid.MirthdewEncoreFluids;

public class MirthdewEncoreFluidVariantAttributes {

    public static void init() {
        MirthdewEncoreFluids.forEachXPGF(xpgf -> {
            FluidVariantAttributes.register(xpgf.getFluid(), getFVAH(xpgf));
        });
    }

    public static FluidVariantAttributeHandler getFVAH(MirthdewEncoreFluids.XPlatGenericFluid xpgf) {
        return new FluidVariantAttributeHandler() {
            @Override
            public int getLuminance(FluidVariant variant) {
                return xpgf.getLuminance();
            }

            @Override
            public int getTemperature(FluidVariant variant) {
                return xpgf.getTemperature();
            }

            @Override
            public int getViscosity(FluidVariant variant, @Nullable Level world) {
                return xpgf.getViscosity();
            }
        };
    }
}
