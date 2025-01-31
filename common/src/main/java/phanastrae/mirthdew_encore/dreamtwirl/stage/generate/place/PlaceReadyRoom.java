package phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;

import java.util.List;

public class PlaceReadyRoom {

    private boolean isEntrance = false;
    private final Room prefab;
    private boolean isPlaced = false;
    private boolean canPlace = false;
    private List<PlaceReadyRoom> placeAfter = new ObjectArrayList<>();

    public PlaceReadyRoom(Room prefab) {
        this.prefab = prefab;
    }

    public boolean place(ServerLevel level, BoundingBox stageBB) {
        if(!this.isPlaced) {
            if(RoomPlacer.placeStructure(this.prefab, level, stageBB, this.isEntrance)) {
                this.isPlaced = true;
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void setIsEntrance(boolean entrance) {
        isEntrance = entrance;
    }

    public boolean isEntrance() {
        return isEntrance;
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
