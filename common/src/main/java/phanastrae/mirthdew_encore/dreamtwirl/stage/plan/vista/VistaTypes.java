package phanastrae.mirthdew_encore.dreamtwirl.stage.plan.vista;

import net.minecraft.core.Registry;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomType;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomTypeStructureIds;

public interface VistaTypes {

    static VistaType createDecidrheumForest(Registry<RoomType> roomTypeRegistry) {
        return new VistaType.Builder()
                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.DECIDRHEUM_ACHERUNE_ENTRANCE)

                // TODO weighted probabilities
                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.LYCHSEAL_GATE)
                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.LYCHSEAL_GATE)
                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.LYCHSEAL_GATE)
                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.LYCHSEAL_GATE)

                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.DECIDRHEUM_FOURWAY)
                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.DECIDRHEUM_CORNER)
                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.DECIDRHEUM_TWISTING_PATH)
                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.CLINKERA_TWIRL_PATH)

                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.DECIDRHEUM_RING)
                .addRoomType(roomTypeRegistry, RoomTypeStructureIds.CLINKERA_VESPERBILE_FOUNTAIN)
                .build();
    }
}
