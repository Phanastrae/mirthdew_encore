package phanastrae.mirthdew_encore.network.packet;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlDebug;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.StageDesignData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph.DirectedEdge;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph.DoorNode;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;

import java.util.List;
import java.util.Map;

public class DreamtwirlDebugPayload implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, DreamtwirlDebugPayload> PACKET_CODEC = CustomPacketPayload.codec(DreamtwirlDebugPayload::write, DreamtwirlDebugPayload::new);
    public static final CustomPacketPayload.Type<DreamtwirlDebugPayload> PACKET_ID = new CustomPacketPayload.Type<>(MirthdewEncore.id("dreamtwirl_debug"));

    private final DreamtwirlDebug.DebugInfo debugInfo;

    public DreamtwirlDebugPayload(FriendlyByteBuf buf) {
        this(DreamtwirlDebug.DebugInfo.read(buf));
    }

    public DreamtwirlDebugPayload(DreamtwirlDebug.DebugInfo debugInfo) {
        this.debugInfo = debugInfo;
    }

    public static DreamtwirlDebug.DebugInfo createDebugInfo(StageDesignData stageDesignData, long dreamtwirlId) {
        List<DoorNode> nodes = stageDesignData.getRoomGraph().getDoorNodes();
        List<DirectedEdge> edges = stageDesignData.getRoomGraph().getDirectedEdges();

        Map<DoorNode, DreamtwirlDebug.DebugNode> nodeMap = new Object2ObjectOpenHashMap<>();
        List<DreamtwirlDebug.DebugEdge> debugEdges = new ObjectArrayList<>();

        int id = 0;
        for(DoorNode node : nodes) {
            RoomDoor door = node.getDoor();
            DreamtwirlDebug.DebugNode debugNode = new DreamtwirlDebug.DebugNode(id, door.getPos(), door.getDoorType());
            nodeMap.put(node, debugNode);
            id++;
        }

        for(DirectedEdge edge : edges) {
            int startId = nodeMap.get(edge.getStart()).id();
            int endId = nodeMap.get(edge.getEnd()).id();
            DreamtwirlDebug.DebugEdge debugEdge = new DreamtwirlDebug.DebugEdge(startId, endId);
            debugEdges.add(debugEdge);
        }

        return new DreamtwirlDebug.DebugInfo(dreamtwirlId, nodeMap.values().toArray(new DreamtwirlDebug.DebugNode[0]), debugEdges.toArray(new DreamtwirlDebug.DebugEdge[0]));
    }

    public void write(FriendlyByteBuf buf) {
        this.debugInfo.write(buf);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

    public DreamtwirlDebug.DebugInfo getDebugInfo() {
        return debugInfo;
    }
}
