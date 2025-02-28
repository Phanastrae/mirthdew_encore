package phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room;

import net.minecraft.resources.ResourceLocation;

public class RoomType {

    private final ResourceLocation resourceLocation;
    private final Category category;

    public RoomType(ResourceLocation resourceLocation, Category category) {
        this.resourceLocation = resourceLocation;
        this.category = category;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEntrance() {
        return this.category == Category.ENTRANCE;
    }

    public boolean isPath() {
        return this.category == Category.PATH;
    }

    public boolean isRoom() {
        return this.category == Category.ROOM;
    }

    public boolean isGate() {
        return this.category == Category.GATE;
    }

    public enum Category {
        ENTRANCE,
        PATH,
        ROOM,
        GATE
    }
}
