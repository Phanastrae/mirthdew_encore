package phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreRegistries;

public record RoomType(ResourceLocation templatePool, int maxDepth, RoomCategory category) {
    public static final StringRepresentable.StringRepresentableCodec<RoomCategory> CATEGORY_CODEC = StringRepresentable.fromEnum(RoomCategory::values);

    public static final Codec<Holder<RoomType>> REGISTRY_CODEC = RegistryFixedCodec.create(MirthdewEncoreRegistries.ROOM_TYPE_KEY);
    public static final Codec<RoomType> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ResourceLocation.CODEC.fieldOf("template_pool").forGetter(RoomType::templatePool),
                            Codec.INT.fieldOf("max_depth").forGetter(RoomType::maxDepth),
                            CATEGORY_CODEC.fieldOf("category").forGetter(RoomType::category)
                    )
                    .apply(instance, RoomType::new)
    );


    public boolean isEntrance() {
        return this.category == RoomCategory.ENTRANCE;
    }

    public boolean isPath() {
        return this.category == RoomCategory.PATH;
    }

    public boolean isLarge() {
        return this.category == RoomCategory.LARGE;
    }

    public boolean isGate() {
        return this.category == RoomCategory.GATE;
    }
}
