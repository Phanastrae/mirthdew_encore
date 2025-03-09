package phanastrae.mirthdew_encore.dreamtwirl.stage.acherune;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class StageAcherunes {
    public static String KEY_ACHERUNE_LIST = "acherune_list";

    private final DreamtwirlStage stage;
    private final AcheruneStorage map = new AcheruneStorage();

    public StageAcherunes(DreamtwirlStage stage) {
        this.stage = stage;
    }

    public void setDirty() {
        this.stage.setDirty();
    }

    public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        ListTag list = new ListTag();

        int index = 0;
        for(Acherune acherune : map.values()) {
            CompoundTag acTag = new CompoundTag();
            acherune.writeNbt(acTag, registries);
            list.add(index, acTag);

            index++;
        }
        nbt.put(KEY_ACHERUNE_LIST, list);

        return nbt;
    }

    public CompoundTag readNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        this.map.clear();

        if(nbt.contains(KEY_ACHERUNE_LIST)) {
            ListTag listTag = nbt.getList(KEY_ACHERUNE_LIST, Tag.TAG_COMPOUND);

            for(int i = 0; i < listTag.size(); i++) {
                CompoundTag acTag = listTag.getCompound(i);
                Acherune acherune = Acherune.fromNbt(acTag, registries);

                this.map.put(acherune);
            }
        }

        return nbt;
    }

    public boolean reset() {
        if(this.map.isEmpty()) {
            return false;
        } else {
            this.map.clear();
            return true;
        }
    }

    public @Nullable Acherune create(BlockPos pos, Level level) {
        if(this.map.containsKey(pos)) {
            // if acherune already exists at this position, return it
            return this.map.get(pos);
        }

        if(this.stage.isDeletingSelf()) {
            // no creating acherunes in self-deleting stages
            return null;
        }

        long time = level.getGameTime();
        RandomSource random = level.random;
        for(int i = 0; i < 5; i++) {
            // this should, in practice, basically always succeed on the first try,
            // but theoretically if you place an absurd number of acherunes in a single tick it might not
            // as in, if you fill the entire empty dreamtwirl space with acherunes in a single tick there is roughly a 0.00000014% chance you might need to reroll at least once
            // (assuming i did the math right)

            long id = random.nextLong();
            Acherune.AcheruneId aId = new Acherune.AcheruneId(time, id);
            Acherune acherune = new Acherune(pos, aId);
            if(!this.map.containsKey(aId)) {
                this.map.put(acherune);
                this.setDirty();
                return acherune;
            }
        }

        return null;
    }

    public void remove(BlockPos pos) {
        if(this.map.remove(pos)) {
            this.setDirty();
        }
    }

    public boolean moveAcherune(BlockPos oldPos, BlockPos newPos) {
        if(this.map.containsKey(newPos)) {
            return false;
        }

        Acherune ac = this.map.get(oldPos);
        if(ac == null) {
            return false;
        }

        this.remove(oldPos);
        ac.setPos(newPos);
        this.map.put(ac);

        this.setDirty();
        return true;
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public @Nullable Acherune getRandomEmptyEntranceAcherune(RandomSource random) {
        // TODO
        return getRandomAcheruneMatching(random, ac -> true);
    }

    public @Nullable Acherune getRandomAcheruneMatching(RandomSource random, Predicate<Acherune> predicate) {
        List<Acherune> valid = this.map.values().stream().filter(predicate).toList();
        if(valid.isEmpty()) {
            return null;
        } else {
            return valid.get(random.nextInt(valid.size()));
        }
    }

    public @Nullable Acherune getNearestAcheruneToPos(BlockPos target, RandomSource random) {
        List<Acherune> nearest = new ObjectArrayList<>();
        long nearestDistSqr = Long.MAX_VALUE;
        for(Acherune acherune : this.map.values()) {
            BlockPos acPos = acherune.getPos();
            BlockPos offset = acPos.subtract(target);
            long distSqr = (long)offset.getX()*offset.getX() + (long)offset.getY()*offset.getY() + (long)offset.getZ()*offset.getZ();

            if(distSqr < nearestDistSqr) {
                nearestDistSqr = distSqr;
                nearest.clear();
            }

            if(distSqr == nearestDistSqr) {
                nearest.add(acherune);
            }
        }

        if(nearest.isEmpty()) {
            return null;
        } else {
            return nearest.get(random.nextInt(nearest.size()));
        }
    }

    public @Nullable Acherune getAcherune(Acherune.AcheruneId id) {
        return this.map.idMap.getOrDefault(id, null);
    }

    public @Nullable Acherune getAcherune(BlockPos pos) {
        return this.map.posMap.getOrDefault(pos, null);
    }

    public int getAcheruneCount() {
        return this.map.idMap.size();
    }

    public List<Acherune> getAcherunes() {
        return this.map.acherunes;
    }

    public static class AcheruneStorage {
        private final List<Acherune> acherunes = new ObjectArrayList<>();
        private final Map<Acherune.AcheruneId, Acherune> idMap = new Object2ObjectOpenHashMap<>();
        private final Map<BlockPos, Acherune> posMap = new Object2ObjectOpenHashMap<>();

        public Collection<Acherune> values() {
            return this.acherunes;
        }

        public boolean isEmpty() {
            return this.acherunes.isEmpty();
        }

        public void put(Acherune acherune) {
            this.acherunes.add(acherune);
            this.idMap.put(acherune.getId(), acherune);
            this.posMap.put(acherune.getPos(), acherune);
        }

        public boolean remove(BlockPos blockPos) {
            if(this.posMap.containsKey(blockPos)) {
                Acherune acherune = this.posMap.remove(blockPos);
                this.idMap.remove(acherune.getId());
                this.acherunes.remove(acherune);
                return true;
            } else {
                return false;
            }
        }

        public void clear() {
            this.acherunes.clear();
            this.idMap.clear();
            this.posMap.clear();
        }

        public boolean containsKey(Acherune.AcheruneId id) {
            return this.idMap.containsKey(id);
        }

        public boolean containsKey(BlockPos pos) {
            return this.posMap.containsKey(pos);
        }

        public Acherune get(BlockPos pos) {
            return this.posMap.get(pos);
        }
    }
}
