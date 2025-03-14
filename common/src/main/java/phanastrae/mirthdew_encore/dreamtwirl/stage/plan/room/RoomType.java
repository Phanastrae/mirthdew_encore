package phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public record RoomType(ResourceLocation templatePool, int maxDepth, Category category) {
    public static final StringRepresentable.StringRepresentableCodec<Category> CATEGORY_CODEC = StringRepresentable.fromEnum(Category::values);

    public static final Codec<RoomType> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ResourceLocation.CODEC.fieldOf("template_pool").forGetter(RoomType::templatePool),
                            Codec.INT.fieldOf("max_depth").forGetter(RoomType::maxDepth),
                            CATEGORY_CODEC.fieldOf("category").forGetter(RoomType::category)
                    )
                    .apply(instance, RoomType::new)
    );

    public boolean isEntrance() {
        return this.category == Category.ENTRANCE;
    }

    public boolean isPath() {
        return this.category == Category.PATH;
    }

    public boolean isRoom() {
        return this.category == Category.ROOM;
    }

    public boolean isGate() {
        return this.category == Category.GATE;
    }

    public enum Category implements StringRepresentable {
        ENTRANCE("entrance"),
        PATH("path"),
        ROOM("room"),
        GATE("gate");

        private String name;

        Category(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
