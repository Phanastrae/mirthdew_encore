package phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;

import java.util.List;

public class PlaceReadyRoom {

    private final Room prefab;
    private boolean isPlaced = false;
    private boolean canPlace = false;
    private List<PlaceReadyRoom> placeAfter = new ObjectArrayList<>();

    public PlaceReadyRoom(Room prefab) {
        this.prefab = prefab;
    }

    public void place(ServerLevel level, BoundingBox stageBB) {
        if(!this.isPlaced) {
            RoomPlacer.placeStructure(this.prefab, level, stageBB);
            this.isPlaced = true;
        }
    }

    public void setNeighborsCanSpawn() {
        this.placeAfter.forEach(prr -> prr.setCanPlace(true));
    }

    public void addToPlaceAfter(PlaceReadyRoom room) {
        this.placeAfter.add(room);
    }

    public Room getPrefab() {
        return prefab;
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public boolean canPlace() {
        return canPlace;
    }

    public void setCanPlace(boolean canPlace) {
        this.canPlace = canPlace;
    }
}
