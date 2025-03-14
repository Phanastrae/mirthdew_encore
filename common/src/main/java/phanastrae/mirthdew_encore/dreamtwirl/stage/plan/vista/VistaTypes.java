package phanastrae.mirthdew_encore.dreamtwirl.stage.plan.vista;

import net.minecraft.core.Registry;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomType;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomTypeStructureIds;

public interface VistaTypes {

    static VistaType createDecidrheumForest(Registry<RoomType> roomTypeRegistry) {
        return new VistaType.Builder()
                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.DECIDRHEUM_ACHERUNE_ENTRANCE, 1)

                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.LYCHSEAL_GATE, 4)

                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.DECIDRHEUM_FOURWAY, 1)
                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.DECIDRHEUM_CORNER, 1)
                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.DECIDRHEUM_TWISTING_PATH, 1)
                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.CLINKERA_TWIRL_PATH, 1)

                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.DECIDRHEUM_RING, 1)
                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.CLINKERA_VESPERBILE_FOUNTAIN, 1)
                .build();
    }
}
