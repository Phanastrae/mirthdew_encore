package phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RoomGraph {

    private final List<DoorNode> doorNodes = new ObjectArrayList<>();
    private final List<DirectedEdge> directedEdges = new ObjectArrayList<>();

    private final Map<RoomDoor, DoorNode> doorMap = new Object2ObjectOpenHashMap<>();

    public void addRoomToGraph(Room room) {
        // add nodes
        List<DoorNode> newNodes = new ObjectArrayList<>();
        for(RoomDoor door : room.getDoors()) {
            DoorNode doorNode = this.addNode(door);
            newNodes.add(doorNode);
        }

        // add edges
        for(DoorNode start : newNodes) {
            if(start.getDoorType().isEntrance) {
                for(DoorNode end : newNodes) {
                    if(end.getDoorType().isExit && start != end) {
                        this.addEdge(start, end);
                    }
                }
            }
        }
    }

    public DoorNode addNode(RoomDoor door) {
        if(!this.doorMap.containsKey(door)) {
            DoorNode node = new DoorNode(door);
            this.doorNodes.add(node);
            this.doorMap.put(door, node);

            return node;
        } else {
            return this.doorMap.get(door);
        }
    }

    public void addNodesWithEdge(RoomDoor start, RoomDoor end) {
        DoorNode startNode = this.addNode(start);
        DoorNode endNode = this.addNode(end);
        this.addEdge(startNode, endNode);
    }

    public void addEdge(DoorNode start, DoorNode end) {
        DirectedEdge edge = new DirectedEdge(start, end);
        this.directedEdges.add(edge);
        edge.attachToNodes();
    }

    public List<DoorNode> getDoorNodes() {
        return doorNodes;
    }

    public List<DirectedEdge> getDirectedEdges() {
        return directedEdges;
    }

    public Optional<DoorNode> getNode(RoomDoor door) {
        if(this.doorMap.containsKey(door)) {
            return Optional.of(this.doorMap.get(door));
        } else {
            return Optional.empty();
        }
    }

    public Optional<DoorNode> getRandomUnfilledExitDoorNode(RandomSource random) {
        // TODO optimise
        List<DoorNode> emptyDoors = this.doorNodes.stream().filter(doorNode -> !doorNode.getDoor().isConnected() && doorNode.getDoor().getDoorType().isExit).toList();
        if(emptyDoors.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(emptyDoors.get(random.nextInt(emptyDoors.size())));
        }
    }
}
