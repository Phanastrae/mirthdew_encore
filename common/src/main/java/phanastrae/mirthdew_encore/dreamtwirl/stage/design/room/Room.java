package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.StageDesignData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomType;

import java.util.List;
import java.util.Optional;

public class Room {
    public static final String KEY_STRUCTURE = "structure";
    public static final String KEY_ROOM_TYPE = "room_type";
    public static final String KEY_STRUCTURE_PIECES = "structure_pieces";
    public static final String KEY_ROOM_OBJECTS = "room_objects";

    private final Structure structure;
    private final RoomType roomType;

    private final PiecesContainer piecesContainer;
    private final RoomObjects roomObjects;

    private BoundingBox boundingBox;
    private boolean bbNeedsRecalc;

    public Room(Structure structure, RoomType roomType, PiecesContainer piecesContainer, RoomObjects roomObjects) {
        this.structure = structure;
        this.roomType = roomType;
        this.piecesContainer = piecesContainer;
        this.roomObjects = roomObjects;

        this.boundingBox = this.piecesContainer.calculateBoundingBox();
        this.bbNeedsRecalc = false;
    }

    public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
        RegistryOps<Tag> registryops = registries.createSerializationContext(NbtOps.INSTANCE);

        Structure.DIRECT_CODEC
                .encodeStart(registryops, this.structure)
                .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to encode structure for Room: '{}'", st))
                .ifPresent(bpdTag -> nbt.put(KEY_STRUCTURE, bpdTag));

        RoomType.CODEC
                .encodeStart(registryops, this.roomType)
                .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to encode room type for Room: '{}'", st))
                .ifPresent(bpdTag -> nbt.put(KEY_ROOM_TYPE, bpdTag));

        nbt.put(KEY_STRUCTURE_PIECES, this.piecesContainer.save(spsContext));

        RoomObjects.CODEC
                .encodeStart(registryops, this.roomObjects)
                .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to encode room objects for Room: '{}'", st))
                .ifPresent(bpdTag -> nbt.put(KEY_ROOM_OBJECTS, bpdTag));

        return nbt;
    }

    public static @Nullable Room fromNbt(CompoundTag nbt, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
        RegistryOps<Tag> registryops = registries.createSerializationContext(NbtOps.INSTANCE);

        if(!nbt.contains(KEY_STRUCTURE, Tag.TAG_COMPOUND)) {
            return null;
        }
        Optional<Structure> structureOptional = Structure.DIRECT_CODEC
                .parse(registryops, nbt.get(KEY_STRUCTURE))
                .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to parse structure for Room: '{}'", st));
        if(structureOptional.isEmpty()) {
            return null;
        }
        Structure structure = structureOptional.get();

        if(!nbt.contains(KEY_ROOM_TYPE, Tag.TAG_COMPOUND)) {
            return null;
        }
        Optional<RoomType> roomTypeOptional = RoomType.CODEC
                .parse(registryops, nbt.get(KEY_ROOM_TYPE))
                .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to parse room type for Room: '{}'", st));
        if(roomTypeOptional.isEmpty()) {
            return null;
        }
        RoomType roomType = roomTypeOptional.get();

        PiecesContainer pieces;
        if(!nbt.contains(KEY_STRUCTURE_PIECES, Tag.TAG_LIST)) {
            return null;
        }
        pieces = PiecesContainer.load(nbt.getList(KEY_STRUCTURE_PIECES, Tag.TAG_COMPOUND), spsContext);

        if(!nbt.contains(KEY_ROOM_OBJECTS, Tag.TAG_COMPOUND)) {
            return null;
        }
        Optional<RoomObjects> roomObjectsOptional = RoomObjects.CODEC
                .parse(registryops, nbt.get(KEY_ROOM_OBJECTS))
                .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to parse room object for Room: '{}'", st));
        if(roomObjectsOptional.isEmpty()) {
            return null;
        }
        RoomObjects roomObjects = roomObjectsOptional.get();

        return new Room(structure, roomType, pieces, roomObjects);
    }

    public void translateToMatchDoor(RoomDoor thisDoor, RoomDoor targetDoor, StageDesignData designData) {
        BlockPos targetPos = targetDoor.getPos().relative(targetDoor.getOrientation().front());
        BlockPos currentPos = thisDoor.getPos();
        this.translate(targetPos.subtract(currentPos), designData);
    }

    public void centerAt(Vec3i vec3i, StageDesignData designData) {
        this.translate(vec3i.subtract(this.getBoundingBox().getCenter()), designData);
    }

    public void translate(Vec3i vec3i, StageDesignData designData) {
        this.translate(vec3i.getX(), vec3i.getY(), vec3i.getZ(), designData);
    }

    public void translate(int x, int y, int z, StageDesignData designData) {
        this.translate(x, y, z);
        designData.getCollisionMap().updateRoom(this);
    }

    public void translate(int x, int y, int z) {
        this.getPiecesContainer().pieces().forEach(structurePiece -> structurePiece.move(x, y, z));
        this.roomObjects.translate(x, y, z);

        // TODO is the recalc the best way to do this? is it cheaper to just calc here?
        //this.bbNeedsRecalc = true;
        this.boundingBox = this.boundingBox.moved(x, y, z);
    }

    public PiecesContainer getPiecesContainer() {
        return piecesContainer;
    }

    public BoundingBox getBoundingBox() {
        if(this.bbNeedsRecalc) {
            this.boundingBox = this.piecesContainer.calculateBoundingBox();
            this.bbNeedsRecalc = false;
        }
        return this.boundingBox;
    }

    public List<RoomDoor> getDoors() {
        return this.roomObjects.doors;
    }

    public List<RoomLychseal> getLychseals() {
        return this.roomObjects.seals;
    }

    public Optional<RoomLychseal> getUnplacedLychseal(BlockPos pos) {
        return this.roomObjects.getUnplacedLychseal(pos);
    }

    public RoomObjects getRoomObjects() {
        return roomObjects;
    }

    public Structure getStructure() {
        return structure;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public static class RoomObjects {
        public static final Codec<RoomObjects> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                RoomDoor.CODEC.listOf().fieldOf("doors").forGetter(RoomObjects::getDoors),
                                RoomLychseal.CODEC.listOf().fieldOf("lychseals").forGetter(RoomObjects::getLychseals)
                        )
                        .apply(instance, RoomObjects::new)
        );

        private final List<RoomDoor> doors;
        private final List<RoomLychseal> seals;

        public RoomObjects(List<RoomDoor> doors, List<RoomLychseal> seals) {
            this.doors = doors;
            this.seals = seals;
        }

        public void translate(int x, int y, int z) {
            this.getDoors().forEach(door -> door.translate(x, y, z));
            this.getLychseals().forEach(door -> door.translate(x, y, z));
        }

        public List<RoomDoor> getDoors() {
            return doors;
        }

        public List<RoomLychseal> getLychseals() {
            return seals;
        }

        public Optional<RoomLychseal> getUnplacedLychseal(BlockPos pos) {
            for(RoomLychseal lychseal : this.seals) {
                if(!lychseal.isPlaced()) {
                    if (lychseal.getPos().equals(pos)) {
                        lychseal.setPlaced(true);
                        return Optional.of(lychseal);
                    }
                }
            }
            return Optional.empty();
        }
    }
}
