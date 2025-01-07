package phanastrae.mirthdew_encore.fabric.client.fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import phanastrae.mirthdew_encore.client.fluid.MirthdewEncoreClientFluids;

public class MirthdewEncoreFluidRenderHandlers {

    public static void init() {
        MirthdewEncoreClientFluids.init();

        MirthdewEncoreClientFluids.forEachXPGCF(xpgcf -> {
            FluidRenderHandlerRegistry.INSTANCE.register(xpgcf.getStill(), xpgcf.getFlow(), getFluidRenderHandler(xpgcf));
        });
    }

    public static FluidRenderHandler getFluidRenderHandler(MirthdewEncoreClientFluids.XPlatGenericClientFluid xpgcf) {
        return new SimpleFluidRenderHandler(
                xpgcf.getStillTexture(),
                xpgcf.getFlowTexture(),
                xpgcf.getOverlayTexture(),
                xpgcf.getTint()
        );
    }
}
