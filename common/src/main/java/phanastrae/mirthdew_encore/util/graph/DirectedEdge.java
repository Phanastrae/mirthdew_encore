package phanastrae.mirthdew_encore.util.graph;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class DirectedEdge {
    public static final Codec<DirectedEdge> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.LONG.fieldOf("i").forGetter(DirectedEdge::getId),
                            Codec.LONG.fieldOf("s").forGetter(DirectedEdge::getStartId),
                            Codec.LONG.fieldOf("e").forGetter(DirectedEdge::getEndId)
                    )
                    .apply(instance, DirectedEdge::new)
    );

    private final long id;
    private final long startId;
    private final long endId;

    public DirectedEdge(long id, long startId, long endId) {
        this.id = id;
        this.startId = startId;
        this.endId = endId;
    }

    public long getId() {
        return id;
    }

    public long getStartId() {
        return startId;
    }

    public long getEndId() {
        return endId;
    }
}
