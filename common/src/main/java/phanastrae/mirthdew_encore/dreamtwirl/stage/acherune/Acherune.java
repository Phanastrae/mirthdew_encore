package phanastrae.mirthdew_encore.dreamtwirl.stage.acherune;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.entity.SlumbersocketBlockEntity;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.component.type.LinkedAcheruneComponent;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;
import phanastrae.mirthdew_encore.util.BlockPosDimensional;

public class Acherune {
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_ID = "id";
    public static final String KEY_POS_X = "x";
    public static final String KEY_POS_Y = "y";
    public static final String KEY_POS_Z = "z";
    public static final String KEY_LINKED_POS = "linked_pos";

    private final AcheruneId id;
    private BlockPos pos;

    private @Nullable BlockPosDimensional linkedPos = null;

    public Acherune(BlockPos pos, AcheruneId id) {
        this.pos = pos;
        this.id = id;
    }

    public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        nbt.putLong(KEY_TIMESTAMP, this.id.timestamp());
        nbt.putLong(KEY_ID, this.id.id());

        nbt.putInt(KEY_POS_X, this.pos.getX());
        nbt.putInt(KEY_POS_Y, this.pos.getY());
        nbt.putInt(KEY_POS_Z, this.pos.getZ());

        RegistryOps<Tag> registryops = registries.createSerializationContext(NbtOps.INSTANCE);
        if(this.linkedPos != null) {
            BlockPosDimensional.CODEC
                    .encodeStart(registryops, this.linkedPos)
                    .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to encode location for Acherune: '{}'", st))
                    .ifPresent(bpdTag -> nbt.put(KEY_LINKED_POS, bpdTag));
        }

        return nbt;
    }

    public static Acherune fromNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        long timestamp = nbt.getLong(KEY_TIMESTAMP);
        long id = nbt.getLong(KEY_ID);
        AcheruneId aId = new AcheruneId(timestamp, id);

        int x = nbt.getInt(KEY_POS_X);
        int y = nbt.getInt(KEY_POS_Y);
        int z = nbt.getInt(KEY_POS_Z);
        BlockPos pos = new BlockPos(x, y, z);

        Acherune acherune = new Acherune(pos, aId);

        RegistryOps<Tag> registryops = registries.createSerializationContext(NbtOps.INSTANCE);
        BlockPosDimensional.CODEC
                .parse(registryops, nbt.get(KEY_LINKED_POS))
                .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to parse location for Acherune: '{}'", st))
                .ifPresent(acherune::setLinkedPos);

        return acherune;
    }

    public AcheruneId getId() {
        return id;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setLinkedPos(@Nullable BlockPosDimensional linkedPos) {
        this.linkedPos = linkedPos;
    }

    public @Nullable BlockPosDimensional getLinkedPos() {
        return linkedPos;
    }

    public boolean isLinkedTo(BlockPos pos, @Nullable Level level) {
        if(this.linkedPos == null || level == null) {
            return false;
        } else {
            return pos.equals(this.linkedPos.getPos()) && level.dimension().location().equals(this.linkedPos.dimensionId());
        }
    }

    public boolean validateLinkedPos(MinecraftServer server, StageAcherunes stageAcherunes) {
        if(this.linkedPos == null) return false;

        Level level = this.linkedPos.getLevel(server);
        if(!(level instanceof ServerLevel)) return false;

        BlockPos linkedBlockPos = linkedPos.getPos();
        if(level.getBlockEntity(linkedBlockPos) instanceof SlumbersocketBlockEntity blockEntity) {
            ItemStack eye = blockEntity.getHeldItem();
            if(eye.is(MirthdewEncoreItems.SLUMBERING_EYE) && eye.has(MirthdewEncoreDataComponentTypes.LINKED_ACHERUNE)) {
                LinkedAcheruneComponent lac = eye.get(MirthdewEncoreDataComponentTypes.LINKED_ACHERUNE);

                if(this.getId().equals(lac.getAcheruneId())) {
                    return true;
                }
            }
        }

        this.setLinkedPos(null);
        stageAcherunes.setDirty();
        return false;
    }

    public record AcheruneId(long timestamp, long id) {
        @Override
        public boolean equals(Object o) {
            if(o instanceof AcheruneId ot) {
                return this.timestamp == ot.timestamp && this.id == ot.id;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Long.hashCode(this.timestamp ^ this.id);
        }
    }
}
