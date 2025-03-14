package phanastrae.mirthdew_encore.dreamtwirl.stage.plan.vista;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomType;

import java.util.List;

public record VistaType(List<Entry> roomTypeEntries) {
    public static final Codec<VistaType> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Entry.CODEC.listOf().fieldOf("entries").forGetter(VistaType::roomTypeEntries)
                    )
                    .apply(instance, VistaType::new)
    );

    public record Entry(Holder<RoomType> roomType, int weight) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                RoomType.REGISTRY_CODEC.fieldOf("room_type").forGetter(Entry::roomType),
                                Codec.INT.fieldOf("weight").forGetter(Entry::weight)
                        )
                        .apply(instance, Entry::new)
        );
    }
}
