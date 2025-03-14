package phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room;

import net.minecraft.util.StringRepresentable;

public enum RoomCategory implements StringRepresentable {
    ENTRANCE("entrance"),
    PATH("path"),
    LARGE("large"),
    GATE("gate");

    private String name;

    RoomCategory(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
