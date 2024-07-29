package phanastrae.mirthdew_encore.compat;

import net.fabricmc.loader.api.FabricLoader;

public class Compat {

    // mod loaded flags
    public static boolean LITHIUM_LOADED;

    // feature toggle flags
    public static boolean USE_DREAMSPECK_COLLISION_COMPAT;

    public static void init() {
        setupModLoadedFlags(FabricLoader.getInstance());
        setupToggleFlags();
    }

    public static void setupModLoadedFlags(FabricLoader fabricLoader) {
        LITHIUM_LOADED = fabricLoader.isModLoaded("lithium");
    }

    public static void setupToggleFlags() {
        USE_DREAMSPECK_COLLISION_COMPAT = LITHIUM_LOADED;
    }
}
