package phanastrae.mirthdew_encore.dreamtwirl.stage.plan.vista;

import net.minecraft.resources.ResourceLocation;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomType;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomTypeStructureIds;

public interface VistaTypes {

    VistaType DECIDRHEUM_FOREST = createDecidrheumForest();

    private static VistaType createDecidrheumForest() {
        return new VistaType.Builder()
                .addRoomType(new RoomType(RoomTypeStructureIds.DECIDRHEUM_ACHERUNE_ENTRANCE.withSuffix("/starts"), 1, RoomType.Category.ENTRANCE))

                // TODO weighted probabilities
                .addRoomType(of(RoomTypeStructureIds.LYCHSEAL_GATE.withSuffix("/starts"), 1, RoomType.Category.GATE))
                .addRoomType(of(RoomTypeStructureIds.LYCHSEAL_GATE.withSuffix("/starts"), 1, RoomType.Category.GATE))
                .addRoomType(of(RoomTypeStructureIds.LYCHSEAL_GATE.withSuffix("/starts"), 1, RoomType.Category.GATE))
                .addRoomType(of(RoomTypeStructureIds.LYCHSEAL_GATE.withSuffix("/starts"), 1, RoomType.Category.GATE))

                .addRoomType(of(RoomTypeStructureIds.DECIDRHEUM_FOURWAY.withSuffix("/starts"), 1, RoomType.Category.PATH))
                .addRoomType(of(RoomTypeStructureIds.DECIDRHEUM_CORNER.withSuffix("/starts"), 1, RoomType.Category.PATH))
                .addRoomType(of(RoomTypeStructureIds.DECIDRHEUM_TWISTING_PATH.withSuffix("/starts"), 1, RoomType.Category.PATH))
                .addRoomType(of(RoomTypeStructureIds.CLINKERA_TWIRL_PATH.withSuffix("/starts"), 1, RoomType.Category.PATH))

                .addRoomType(of(RoomTypeStructureIds.DECIDRHEUM_RING.withSuffix("/starts"), 1, RoomType.Category.ROOM))
                .addRoomType(of(RoomTypeStructureIds.CLINKERA_VESPERBILE_FOUNTAIN.withSuffix("/starts"), 1, RoomType.Category.ROOM))
                .build();
    }

    private static RoomType of(ResourceLocation templatePool, int maxDepth, RoomType.Category category) {
        return new RoomType(templatePool, maxDepth, category);
    }
}
