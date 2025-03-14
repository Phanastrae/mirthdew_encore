package phanastrae.mirthdew_encore.fluid;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreFluidTags;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MirthdewEncoreFluids {

    private static final Map<TagKey<Fluid>, XPlatGenericFluid> XPGF_MAP = new Object2ObjectOpenHashMap<>();
    private static final Map<TagKey<Fluid>, XPlatGenericFluid> XPGF_ZERO_FALL_DAMAGE_MAP = new Object2ObjectOpenHashMap<>();

    public static final FlowingFluid VESPERBILE = new VesperbileFluid.Source();
    public static final FlowingFluid FLOWING_VESPERBILE = new VesperbileFluid.Flowing();
    public static final XPlatGenericFluid VESPERBILE_XPGF = new XPlatGenericFluid(MirthdewEncoreFluids.VESPERBILE, MirthdewEncoreFluidTags.VESPERBILE)
            .setMotionScale(0.036D)
            .setDensity(1010)
            .setTemperature(325)
            .setViscosity(3500)
            .setLuminance(9);

    public static void init(BiConsumer<ResourceLocation, Fluid> r) {
        r.accept(id("vesperbile"), VESPERBILE);
        r.accept(id("flowing_vesperbile"), FLOWING_VESPERBILE);

        addXPGFsToMaps(
                VESPERBILE_XPGF
        );
    }

    private static ResourceLocation id(String path) {
        return MirthdewEncore.id(path);
    }

    public static void forEachXPGF(Consumer<XPlatGenericFluid> consumer) {
        XPGF_MAP.values().forEach(consumer);
    }

    public static boolean fluidStateIsMirthdewZeroFallDamage(FluidState fluidState) {
        if(fluidState.is(FluidTags.WATER)) {
            return false;
        }

        for(XPlatGenericFluid xpgf : XPGF_ZERO_FALL_DAMAGE_MAP.values()) {
            if(fluidState.is(xpgf.fluidTag)) {
                return true;
            }
        }

        return false;
    }

    private static void addXPGFsToMaps(XPlatGenericFluid... xpgfs) {
        for(XPlatGenericFluid xpgf : xpgfs) {
            TagKey<Fluid> tag = xpgf.getFluidTag();
            XPGF_MAP.put(tag, xpgf);
            if(xpgf.getFallDistanceModifier() == 0) {
                XPGF_ZERO_FALL_DAMAGE_MAP.put(tag, xpgf);
            }
        }
    }

    // on NeoForge, use this class to create FluidType properties
    // on Fabric, use this class to control various fluid behaviours
    public static class XPlatGenericFluid {

        private final Fluid fluid;
        private final TagKey<Fluid> fluidTag;

        private double motionScale = 0.014D;
        private float fallDistanceModifier = 0F;
        private boolean canExtinguish = true;

        // air is 0/0/0
        // water is 1000/300/1000
        // lava is 3000/1300/6000
        private int density = 1000;
        private int temperature = 300;
        private int viscosity = 1000;

        private int luminance = 0;

        private boolean isWaterEsque = true; // makes the fluid act like water in certain ways

        public XPlatGenericFluid(Fluid fluid, TagKey<Fluid> fluidTag) {
            this.fluid = fluid;
            this.fluidTag = fluidTag;
        }

        public Fluid getFluid() {
            return fluid;
        }

        public TagKey<Fluid> getFluidTag() {
            return this.fluidTag;
        }

        public XPlatGenericFluid setMotionScale(double motionScale) {
            this.motionScale = motionScale;
            return this;
        }

        public double getMotionScale() {
            return this.motionScale;
        }

        public XPlatGenericFluid setFallDistanceModifier(float fallDistanceModifier) {
            this.fallDistanceModifier = fallDistanceModifier;
            return this;
        }

        public float getFallDistanceModifier() {
            return fallDistanceModifier;
        }

        public XPlatGenericFluid setCanExtinguish(boolean canExtinguish) {
            this.canExtinguish = canExtinguish;
            return this;
        }

        public boolean canExtinguish() {
            return canExtinguish;
        }

        public XPlatGenericFluid setDensity(int density) {
            this.density = density;
            return this;
        }

        public int getDensity() {
            return density;
        }

        public XPlatGenericFluid setTemperature(int temperature) {
            this.temperature = temperature;
            return this;
        }

        public int getTemperature() {
            return temperature;
        }

        public XPlatGenericFluid setViscosity(int viscosity) {
            this.viscosity = viscosity;
            return this;
        }

        public int getViscosity() {
            return viscosity;
        }

        public XPlatGenericFluid setIsWaterEsque(boolean value) {
            this.isWaterEsque = value;
            return this;
        }

        public int getLuminance() {
            return luminance;
        }

        public XPlatGenericFluid setLuminance(int luminance) {
            this.luminance = luminance;
            return this;
        }

        public boolean isWaterEsque() {
            return isWaterEsque;
        }
    }
}
