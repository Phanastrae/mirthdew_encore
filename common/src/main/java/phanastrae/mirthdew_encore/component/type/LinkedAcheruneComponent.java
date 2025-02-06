package phanastrae.mirthdew_encore.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.Acherune;

public record LinkedAcheruneComponent(long regionId, long timestamp, ResourceLocation dimensionId, long acId, long acTimestamp) {
    public static final Codec<LinkedAcheruneComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.LONG.fieldOf("id").forGetter(LinkedAcheruneComponent::regionId),
                            Codec.LONG.fieldOf("timestamp").forGetter(LinkedAcheruneComponent::timestamp),
                            ResourceLocation.CODEC.fieldOf("dimension").forGetter(LinkedAcheruneComponent::dimensionId),
                            Codec.LONG.fieldOf("ac_id").forGetter(LinkedAcheruneComponent::acId),
                            Codec.LONG.fieldOf("ac_timestamp").forGetter(LinkedAcheruneComponent::acTimestamp)
                    )
                    .apply(instance, LinkedAcheruneComponent::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, LinkedAcheruneComponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            LinkedAcheruneComponent::regionId,
            ByteBufCodecs.VAR_LONG,
            LinkedAcheruneComponent::timestamp,
            ResourceLocation.STREAM_CODEC,
            LinkedAcheruneComponent::dimensionId,
            ByteBufCodecs.VAR_LONG,
            LinkedAcheruneComponent::acId,
            ByteBufCodecs.VAR_LONG,
            LinkedAcheruneComponent::acTimestamp,
            LinkedAcheruneComponent::new
    );

    public static LinkedAcheruneComponent fromAcheruneAndStage(DreamtwirlStage stage, Acherune acherune) {
        return new LinkedAcheruneComponent(stage.getId(), stage.getTimestamp(), stage.getLevel().dimension().location(), acherune.getId().id(), acherune.getId().timestamp());
    }

    @Nullable
    public Level getLevel(MinecraftServer server) {
        return server.getLevel(ResourceKey.create(Registries.DIMENSION, dimensionId));
    }

    @Nullable
    public DreamtwirlStage getStage(MinecraftServer server) {
        Level linkedLevel = this.getLevel(server);
        if(linkedLevel == null) return null;

        DreamtwirlStageManager dreamtwirlStageManager = DreamtwirlStageManager.getDreamtwirlStageManager(linkedLevel);
        if(dreamtwirlStageManager == null) return null;

        DreamtwirlStage stage = dreamtwirlStageManager.getDreamtwirlIfPresent(this.regionId);
        if(stage == null) return null;

        if(stage.getTimestamp() == this.timestamp) {
            return stage;
        } else {
            return null;
        }
    }

    @Nullable
    public Acherune getAcherune(MinecraftServer server) {
        DreamtwirlStage stage = getStage(server);
        if(stage == null) return null;

        return stage.getStageAcherunes().getAcherune(new Acherune.AcheruneId(this.acTimestamp, this.acId));
    }
}
