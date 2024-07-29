package phanastrae.mirthdew_encore.compat;

import phanastrae.mirthdew_encore.services.Services;
import phanastrae.mirthdew_encore.services.XPlatInterface;

public class Compat {

    // mod loaded flags
    public static boolean LITHIUM_LOADED;

    // feature toggle flags
    public static boolean USE_DREAMSPECK_COLLISION_COMPAT;

    public static void init() {
        setupModLoadedFlags();
        setupToggleFlags();
    }

    public static void setupModLoadedFlags() {
        XPlatInterface XPLAT = Services.XPLAT;
        LITHIUM_LOADED = XPLAT.isModLoaded("lithium");
    }

    public static void setupToggleFlags() {
        USE_DREAMSPECK_COLLISION_COMPAT = LITHIUM_LOADED;
    }
}
