package phanastrae.mirthdew_encore.neoforge.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidType;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.fluid.MirthdewEncoreFluids;

import java.util.function.BiConsumer;

public class MirthdewEncoreFluidTypes {

    public static final FluidType VESPERBILE = new FluidType(propertiesFromXPlat(MirthdewEncoreFluids.VESPERBILE_XPGF)
            .descriptionId("block.mirthdew_encore.vesperbile")
    ) {
        @Override
        public void setItemMovement(ItemEntity entity) {
            float gravityMultiplier = 0.25F;

            double gravity = entity.getGravity();
            if (gravity != 0.0) {
                entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, gravity * -gravityMultiplier, 0.0));
            }
        }
    };

    public static void init(BiConsumer<ResourceLocation, FluidType> r) {
        r.accept(id("vesperbile"), VESPERBILE);
    }

    private static ResourceLocation id(String path) {
        return MirthdewEncore.id(path);
    }


    public static void registerFluidInteractions() {
        // Lava + Vesperbile = Obsidian (Source Lava) / Scarabrim (Flowing Lava)
        FluidInteractionRegistry.addInteraction(
                NeoForgeMod.LAVA_TYPE.value(),
                new FluidInteractionRegistry.InteractionInformation(
                        MirthdewEncoreFluidTypes.VESPERBILE,
                        fluidState -> fluidState.isSource() ? Blocks.OBSIDIAN.defaultBlockState() : MirthdewEncoreBlocks.SCARABRIM.defaultBlockState()
                )
        );

        // Vesperbile + Water = Sunslaked Chalktissue
        FluidInteractionRegistry.addInteraction(
                MirthdewEncoreFluidTypes.VESPERBILE,
                new FluidInteractionRegistry.InteractionInformation(
                        NeoForgeMod.WATER_TYPE.value(),
                        MirthdewEncoreBlocks.SUNSLAKED_CHALKTISSUE.defaultBlockState())
        );
    }

    private static FluidType.Properties propertiesFromXPlat(MirthdewEncoreFluids.XPlatGenericFluid xpgf) {
        return FluidType.Properties.create()
                .canSwim(false)
                .canDrown(false)
                .pathType(null)
                .adjacentPathType(null)

                .motionScale(xpgf.getMotionScale())
                .canPushEntity(true)
                .fallDistanceModifier(xpgf.getFallDistanceModifier())
                .canExtinguish(xpgf.canExtinguish())
                .density(xpgf.getDensity())
                .temperature(xpgf.getTemperature())
                .viscosity(xpgf.getViscosity())
                .lightLevel(xpgf.getLuminance());
    }
}
