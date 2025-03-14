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

public record LinkedDreamtwirlComponent(long regionId, long timestamp, ResourceLocation dimensionId) {
    public static final Codec<LinkedDreamtwirlComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.LONG.fieldOf("id").forGetter(LinkedDreamtwirlComponent::regionId),
                            Codec.LONG.fieldOf("timestamp").forGetter(LinkedDreamtwirlComponent::timestamp),
                            ResourceLocation.CODEC.fieldOf("dimension").forGetter(LinkedDreamtwirlComponent::dimensionId)
                    )
                    .apply(instance, LinkedDreamtwirlComponent::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, LinkedDreamtwirlComponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            LinkedDreamtwirlComponent::regionId,
            ByteBufCodecs.VAR_LONG,
            LinkedDreamtwirlComponent::timestamp,
            ResourceLocation.STREAM_CODEC,
            LinkedDreamtwirlComponent::dimensionId,
            LinkedDreamtwirlComponent::new
    );

    public static LinkedDreamtwirlComponent fromStage(DreamtwirlStage stage) {
        return new LinkedDreamtwirlComponent(stage.getId(), stage.getTimestamp(), stage.getLevel().dimension().location());
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
}
