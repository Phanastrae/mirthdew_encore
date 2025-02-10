package phanastrae.mirthdew_encore.dreamtwirl.stage.plan.vista;

import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomType;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomTypeStructureIds;

public interface VistaTypes {

    VistaType DECIDRHEUM_FOREST = createDecidrheumForest();

    private static VistaType createDecidrheumForest() {
        return new VistaType.Builder()
                .addRoomType(new RoomType(RoomTypeStructureIds.DECIDRHEUM_ACHERUNE_ENTRANCE, RoomType.Category.ENTRANCE))

                .addRoomType(new RoomType(RoomTypeStructureIds.DECIDRHEUM_FOURWAY, RoomType.Category.PATH))
                .addRoomType(new RoomType(RoomTypeStructureIds.DECIDRHEUM_CORNER, RoomType.Category.PATH))
                .addRoomType(new RoomType(RoomTypeStructureIds.DECIDRHEUM_TWISTING_PATH, RoomType.Category.PATH))
                .addRoomType(new RoomType(RoomTypeStructureIds.CLINKERA_TWIRL_PATH, RoomType.Category.PATH))

                .addRoomType(new RoomType(RoomTypeStructureIds.DECIDRHEUM_RING, RoomType.Category.ROOM))
                .addRoomType(new RoomType(RoomTypeStructureIds.CLINKERA_VESPERBILE_FOUNTAIN, RoomType.Category.ROOM))
                .build();
    }
}
