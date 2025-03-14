package phanastrae.mirthdew_encore.network.packet;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlDebug;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.StageDesignData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph.RoomGraph;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;

import java.util.List;

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

    public static DreamtwirlDebug.DebugInfo createDebugInfo(StageDesignData designData, long dreamtwirlId) {
        RoomGraph graph = designData.getRoomGraph();

        List<DreamtwirlDebug.DebugNode> debugNodes = new ObjectArrayList<>();
        graph.getDoorToIdMap().forEach(((doorId, id) -> {
            RoomDoor door = designData.getDoor(doorId);
            if(door != null) {
                DreamtwirlDebug.DebugNode debugNode = new DreamtwirlDebug.DebugNode(id, door.getPos(), door.getDoorType());
                debugNodes.add(debugNode);
            }
        }));

        List<DreamtwirlDebug.DebugEdge> debugEdges = new ObjectArrayList<>();
        graph.getGraph().getEdges().forEach((id, directedEdge) -> {
            debugEdges.add(new DreamtwirlDebug.DebugEdge(directedEdge.getStartId(), directedEdge.getEndId()));
        });

        return new DreamtwirlDebug.DebugInfo(dreamtwirlId, debugNodes.toArray(new DreamtwirlDebug.DebugNode[0]), debugEdges.toArray(new DreamtwirlDebug.DebugEdge[0]));
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
