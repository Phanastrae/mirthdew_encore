package phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.StageDesignData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;
import phanastrae.mirthdew_encore.util.graph.DirectedEdge;
import phanastrae.mirthdew_encore.util.graph.DirectedGraph;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class RoomGraph {
    public static final String KEY_GRAPH = "graph";
    public static final String KEY_ROOM_IDS = "room_ids";
    public static final String KEY_DOOR_IDS = "door_ids";
    public static final String KEY_NODE_IDS = "node_ids";

    private DirectedGraph graph = new DirectedGraph();
    private final Map<RoomDoor.RoomDoorId, Long> doorToIdMap = new Object2ObjectOpenHashMap<>();
    private final Map<Long, RoomDoor.RoomDoorId> idToDoorMap = new Object2ObjectOpenHashMap<>();

    public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        RegistryOps<Tag> registryops = registries.createSerializationContext(NbtOps.INSTANCE);

        DirectedGraph.CODEC
                .encodeStart(registryops, this.graph)
                .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to encode directed graph for Room Graph: '{}'", st))
                .ifPresent(tag -> nbt.put(KEY_GRAPH, tag));

        List<Long> roomIds = new ObjectArrayList<>();
        List<Integer> doorIds = new ObjectArrayList<>();
        List<Long> nodeIds = new ObjectArrayList<>();
        this.doorToIdMap.forEach((roomDoorId, nodeId) -> {
            roomIds.add(roomDoorId.roomId());
            doorIds.add(roomDoorId.doorId());
            nodeIds.add(nodeId);
        });
        nbt.putLongArray(KEY_ROOM_IDS, roomIds);
        nbt.putIntArray(KEY_DOOR_IDS, doorIds);
        nbt.putLongArray(KEY_NODE_IDS, nodeIds);

        return nbt;
    }

    public CompoundTag readNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        RegistryOps<Tag> registryops = registries.createSerializationContext(NbtOps.INSTANCE);

        if(nbt.contains(KEY_GRAPH, Tag.TAG_COMPOUND)) {
            DirectedGraph.CODEC
                    .parse(registryops, nbt.get(KEY_GRAPH))
                    .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to parse directed graph for Room Graph: '{}'", st))
                    .ifPresent(graph -> this.graph = graph);
        }

        this.clearPairs();
        if(nbt.contains(KEY_ROOM_IDS, Tag.TAG_LONG_ARRAY) && nbt.contains(KEY_DOOR_IDS, Tag.TAG_INT_ARRAY) && nbt.contains(KEY_NODE_IDS, Tag.TAG_LONG_ARRAY)) {
            long[] rooms = nbt.getLongArray(KEY_ROOM_IDS);
            int[] doors = nbt.getIntArray(KEY_DOOR_IDS);
            long[] nodes = nbt.getLongArray(KEY_NODE_IDS);

            if(rooms.length == doors.length && doors.length == nodes.length) {
                for(int i = 0; i < rooms.length; i++) {
                    this.addIdPair(nodes[i], new RoomDoor.RoomDoorId(doors[i], rooms[i]));
                }
            }
        }

        return nbt;
    }

    public void addRoom(StageDesignData designData, Room room, RandomSource random) {
        // add nodes
        List<Long> newNodes = new ObjectArrayList<>();
        for(RoomDoor door : room.getDoors()) {
            long id = this.graph.getNextNodeId(random);

            newNodes.add(id);
            this.addIdPair(id, door.getRoomDoorId());
        }

        // add edges
        for(long start : newNodes) {
            RoomDoor.RoomDoorId node = this.idToDoorMap.get(start);
            if(node != null) {
                RoomDoor door = designData.getDoor(node);
                if(door != null && door.getDoorType().isEntrance) {
                    for(long end : newNodes) {
                        if (start == end) continue;

                        RoomDoor.RoomDoorId node2 = this.idToDoorMap.get(end);
                        if (node2 != null) {
                            RoomDoor door2 = designData.getDoor(node2);

                            if(door2 != null && door2.getDoorType().isExit) {
                                this.addNodesWithEdge(node, node2, random);
                            }
                        }
                    }
                }
            }
        }
    }

    public void removeRoom(Room room) {
        for(RoomDoor door : room.getDoors()) {
            if(this.doorToIdMap.containsKey(door.getRoomDoorId())) {
                long id = this.doorToIdMap.get(door.getRoomDoorId());

                this.graph.removeNode(id);
                this.removePair(id);
            }
        }
    }

    public void removePair(long id) {
        if(this.idToDoorMap.containsKey(id)) {
            RoomDoor.RoomDoorId rdId = this.idToDoorMap.remove(id);
            this.doorToIdMap.remove(rdId);
        }
    }

    public void clearPairs() {
        this.idToDoorMap.clear();
        this.doorToIdMap.clear();
    }

    public void addIdPair(long id, RoomDoor.RoomDoorId roomDoorId) {
        if(!this.idToDoorMap.containsKey(id)) {
            this.idToDoorMap.put(id, roomDoorId);
        }
        if(!this.doorToIdMap.containsKey(roomDoorId)) {
            this.doorToIdMap.put(roomDoorId, id);
        }
    }

    public @Nullable RoomDoor.RoomDoorId getDoorNode(long id) {
        return this.idToDoorMap.getOrDefault(id, null);
    }

    public void addEdge(long start, long end, RandomSource random) {
        this.graph.addEdge(start, end, random);
    }

    public void addNodesWithEdge(RoomDoor.RoomDoorId start, RoomDoor.RoomDoorId end, RandomSource random) {
        if(this.doorToIdMap.containsKey(start) && this.doorToIdMap.containsKey(end)) {
            this.addEdge(this.doorToIdMap.get(start), this.doorToIdMap.get(end), random);
        }
    }

    public Optional<RoomDoor.RoomDoorId> getNode(RoomDoor door) {
        if(this.doorToIdMap.containsKey(door.getRoomDoorId())) {
            long id = this.doorToIdMap.get(door.getRoomDoorId());
            if(this.idToDoorMap.containsKey(id)) {
                RoomDoor.RoomDoorId node = this.idToDoorMap.get(id);
                return Optional.of(node);
            }
        }
        return Optional.empty();
    }

    public Optional<RoomDoor.RoomDoorId> getRandomUnfilledExitDoorNode(StageDesignData designData, RandomSource random) {
        // TODO optimise
        List<RoomDoor.RoomDoorId> emptyDoors = this.idToDoorMap.values().stream().filter(doorNode -> {
            RoomDoor door = designData.getDoor(doorNode);
            return door != null && !door.isConnected() && door.getDoorType().isExit;
        }).toList();
        if(emptyDoors.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(emptyDoors.get(random.nextInt(emptyDoors.size())));
        }
    }

    public DirectedGraph getGraph() {
        return graph;
    }

    public void forEachConnectedEndpoint(RoomDoor.RoomDoorId startDoor, Consumer<RoomDoor.RoomDoorId> endNodeConsumer) {
        if(!this.doorToIdMap.containsKey(startDoor)) return;
        long startId = this.doorToIdMap.get(startDoor);
        List<Long> out = this.graph.getOrCreateNode(startId).getOutgoingEdges();
        if(out != null) {
            out.forEach(edgeId -> {
                DirectedEdge edge = this.graph.getEdge(edgeId);
                if(edge != null) {
                    long endId = edge.getEndId();
                    RoomDoor.RoomDoorId endNode = this.getDoorNode(endId);
                    if (endNode != null) {
                        endNodeConsumer.accept(endNode);
                    }
                }
            });
        }
    }

    public void forEachConnectedStartpoint(RoomDoor.RoomDoorId endDoor, Consumer<RoomDoor.RoomDoorId> startNodeConsumer) {
        if(!this.doorToIdMap.containsKey(endDoor)) return;
        long endId = this.doorToIdMap.get(endDoor);
        List<Long> in = this.graph.getOrCreateNode(endId).getIncomingEdges();
        if(in != null) {
            in.forEach(edgeId -> {
                DirectedEdge edge = this.graph.getEdge(edgeId);
                if(edge != null) {
                    long startId = edge.getStartId();
                    RoomDoor.RoomDoorId startNode = this.getDoorNode(startId);
                    if (startNode != null) {
                        startNodeConsumer.accept(startNode);
                    }
                }
            });
        }
    }

    public Map<RoomDoor.RoomDoorId, Long> getDoorToIdMap() {
        return doorToIdMap;
    }

    public Map<Long, RoomDoor.RoomDoorId> getIdToDoorMap() {
        return idToDoorMap;
    }
}
