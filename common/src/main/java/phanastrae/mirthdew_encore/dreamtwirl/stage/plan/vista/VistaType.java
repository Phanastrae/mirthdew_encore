package phanastrae.mirthdew_encore.dreamtwirl.stage.plan.vista;

import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomType;

import java.util.ArrayList;
import java.util.List;

public class VistaType {

    public final List<RoomType> roomTypes;

    public VistaType(List<RoomType> roomTypes) {
        this.roomTypes = roomTypes;
    }

    public static class Builder {

        private final List<RoomType> roomTypes;

        public Builder() {
            this.roomTypes = new ArrayList<>();
        }

        public Builder addRoomType(RoomType roomType) {
            this.roomTypes.add(roomType);
            return this;
        }

        public VistaType build() {
            return new VistaType(this.roomTypes);
        }
    }
}
