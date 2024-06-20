package phanastrae.mirthdew_encore.component.type;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SpellDeckContentsComponent {
    public static final Codec<SpellDeckContentsComponent> CODEC = ItemStack.CODEC.listOf().xmap(SpellDeckContentsComponent::new, component -> component.stacks);
    public static final PacketCodec<RegistryByteBuf, SpellDeckContentsComponent> PACKET_CODEC = ItemStack.PACKET_CODEC
            .collect(PacketCodecs.toList())
            .xmap(SpellDeckContentsComponent::new, component -> component.stacks);

    final List<ItemStack> stacks;

    SpellDeckContentsComponent(List<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public ItemStack get(int index) {
        return this.stacks.get(index);
    }

    public Stream<ItemStack> stream() {
        return this.stacks.stream().map(ItemStack::copy);
    }

    public Iterable<ItemStack> iterate() {
        return this.stacks;
    }

    public Iterable<ItemStack> iterateCopy() {
        return Lists.transform(this.stacks, ItemStack::copy);
    }

    public int size() {
        return this.stacks.size();
    }
    public boolean isEmpty() {
        return this.stacks.isEmpty();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof SpellDeckContentsComponent)) {
            return false;
        } else {
            return ItemStack.stacksEqual(this.stacks, ((SpellDeckContentsComponent)o).stacks);
        }
    }

    public int hashCode() {
        return ItemStack.listHashCode(this.stacks);
    }

    public String toString() {
        return "SpellDeckContents" + this.stacks;
    }

    public static class Builder {
        private final List<ItemStack> stacks;

        public Builder() {
            this.stacks = new ArrayList<>();
        }

        public Builder(SpellDeckContentsComponent base) {
            this.stacks = new ArrayList<>(base.stacks);
        }

        public SpellDeckContentsComponent.Builder clear() {
            this.stacks.clear();
            return this;
        }

        public void addStackToBase(ItemStack stack) {
            this.stacks.add(stack);
        }

        @Nullable
        public ItemStack removeStackFromTop() {
            if(this.stacks.isEmpty()) {
                return null;
            } else {
                return this.stacks.removeFirst().copy();
            }
        }

        @Nullable
        public ItemStack removeStackFromBase() {
            if(this.stacks.isEmpty()) {
                return null;
            } else {
                return this.stacks.removeLast().copy();
            }
        }

        public boolean isEmpty() {
            return this.stacks.isEmpty();
        }

        @Nullable
        public SpellDeckContentsComponent build() {
            return new SpellDeckContentsComponent(List.copyOf(this.stacks));
        }
    }
}
