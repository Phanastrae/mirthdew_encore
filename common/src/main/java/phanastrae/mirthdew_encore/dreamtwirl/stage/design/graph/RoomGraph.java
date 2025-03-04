package phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.SourcedRoom;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.SourcedRoomDoor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RoomGraph {

    private final List<DoorNode> doorNodes = new ObjectArrayList<>();
    private final List<DirectedEdge> directedEdges = new ObjectArrayList<>();

    private final Map<RoomDoor, DoorNode> doorMap = new Object2ObjectOpenHashMap<>();

    public void addRoomToGraph(SourcedRoom room) {
        // add nodes
        List<DoorNode> newNodes = new ObjectArrayList<>();
        for(RoomDoor door : room.getRoom().getDoors()) {
            DoorNode doorNode = this.addNode(new SourcedRoomDoor(door, room));
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

    public void removeRoom(Room room) {
        for(RoomDoor door : room.getDoors()) {
            if(this.doorMap.containsKey(door)) {
                this.doorMap.get(door).setRemoved(true);
                this.doorMap.remove(door);
            }
        }

        // TODO optimise, this is not great
        this.doorNodes.removeIf(DoorNode::isRemoved);

        this.directedEdges.forEach(edge -> {
            if(edge.getStart().isRemoved() || edge.getEnd().isRemoved()) {
                edge.detachFromNodes();
            }
        });
        this.directedEdges.removeIf(edge -> !edge.isAttached());
    }

    public DoorNode addNode(SourcedRoomDoor door) {
        if(!this.doorMap.containsKey(door.getDoor())) {
            DoorNode node = new DoorNode(door);
            this.doorNodes.add(node);
            this.doorMap.put(door.getDoor(), node);

            node.update();

            return node;
        } else {
            return this.doorMap.get(door.getDoor());
        }
    }

    public void addNodesWithEdge(SourcedRoomDoor start, SourcedRoomDoor end) {
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
        List<DoorNode> emptyDoors = this.doorNodes.stream().filter(doorNode -> {
            RoomDoor door = doorNode.getSourcedDoor().getDoor();
            return !door.isConnected() && door.getDoorType().isExit;
        }).toList();
        if(emptyDoors.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(emptyDoors.get(random.nextInt(emptyDoors.size())));
        }
    }
}
