package phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;

import java.util.List;

public class DoorNode {

    private final RoomDoor door;
    private final List<DirectedEdge> edgesOut = new ObjectArrayList<>();
    private final List<DirectedEdge> edgesIn = new ObjectArrayList<>();

    public DoorNode(RoomDoor door) {
        this.door = door;
    }

    public RoomDoor getDoor() {
        return door;
    }

    public RoomDoor.DoorType getDoorType() {
        return this.door.getDoorType();
    }

    public List<DirectedEdge> getEdgesOut() {
        return edgesOut;
    }

    public List<DirectedEdge> getEdgesIn() {
        return edgesIn;
    }

    public void addEdgeOut(DirectedEdge edge) {
        this.edgesOut.add(edge);
    }

    public void addEdgeIn(DirectedEdge edge) {
        this.edgesIn.add(edge);
    }

    public void removeEdgeOut(DirectedEdge edge) {
        this.edgesOut.remove(edge);
    }

    public void removeEdgeIn(DirectedEdge edge) {
        this.edgesIn.remove(edge);
    }
}
