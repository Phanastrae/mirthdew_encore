package phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room;

import net.minecraft.resources.ResourceLocation;
import phanastrae.mirthdew_encore.MirthdewEncore;

public interface RoomTypeStructureIds {
    ResourceLocation ENTRANCE = id("test/entrance");
    ResourceLocation FOURWAY = id("test/fourway");
    ResourceLocation FOURWAY_MINI = id("test/fourway/mini");
    ResourceLocation FOURWAY_CROSSROAD = id("test/fourway_crossroad");
    ResourceLocation TOWER = id("test/tower");
    ResourceLocation BRIDGE = id("test/bridge");
    ResourceLocation LARGE_PATH = id("test/large_path");
    ResourceLocation TUFF_SPIRAL = id("test/tuff_spiral");

    ResourceLocation DECIDRHEUM_RING = id("test/decidrheum_ring");
    ResourceLocation DECIDRHEUM_FOURWAY = id("test/decidrheum_fourway");
    ResourceLocation DECIDRHEUM_CORNER = id("test/decidrheum_corner");
    ResourceLocation DECIDRHEUM_TWISTING_PATH = id("test/decidrheum_twisting_path");
    ResourceLocation CLINKERA_TWIRL_PATH = id("test/clinkera_twirl_path");
    ResourceLocation CLINKERA_VESPERBILE_FOUNTAIN = id("test/clinkera_vesperbile_fountain");

    static ResourceLocation id(String path) {
        return MirthdewEncore.id(path);
    }
}
