package phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place;

import it.unimi.dsi.fastutil.Pair;
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
    private List<Pair<String, PlaceReadyRoom>> placeAfter = new ObjectArrayList<>();
    private final int roomId;

    public PlaceReadyRoom(Room prefab, int roomId) {
        this.prefab = prefab;
        this.roomId = roomId;
    }

    public boolean place(ServerLevel level, BoundingBox stageBB) {
        if(!this.isPlaced) {
            if(RoomPlacer.placeStructure(this.prefab, level, stageBB, this.isEntrance, this.roomId)) {
                this.isPlaced = true;
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void setEmptySealNeighborsCanSpawn() {
        this.openLychseal("");
    }

    public void openLychseal(String lychsealName) {
        for(Pair<String, PlaceReadyRoom> pairs : placeAfter) {
            if(pairs.left().equals(lychsealName)) {
                pairs.right().setCanPlace(true);
            }
        }
    }

    public void setIsEntrance(boolean entrance) {
        isEntrance = entrance;
    }

    public boolean isEntrance() {
        return isEntrance;
    }

    public void addToPlaceAfter(String lychseal, PlaceReadyRoom room) {
        this.placeAfter.add(Pair.of(lychseal, room));
    }

    public Room getRoom() {
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

    public int getRoomId() {
        return roomId;
    }
}
