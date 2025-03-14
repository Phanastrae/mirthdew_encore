package phanastrae.mirthdew_encore.dreamtwirl.stage.plan.vista;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VistaType {

    public final List<Entry> roomTypes;

    public VistaType(List<Entry> roomTypes) {
        this.roomTypes = roomTypes;
    }

    public static class Builder {

        private final List<Entry> roomTypes;

        public Builder() {
            this.roomTypes = new ArrayList<>();
        }

        public Builder addRoomType(Registry<RoomType> registry, ResourceLocation resourceLocation, int weight) {
            Optional<RoomType> optional = registry.getOptional(resourceLocation);
            optional.ifPresent(roomType -> this.roomTypes.add(new Entry(roomType, weight)));
            return this;
        }

        public VistaType build() {
            return new VistaType(this.roomTypes);
        }
    }

    public record Entry(RoomType roomType, int weight) {
    }
}
