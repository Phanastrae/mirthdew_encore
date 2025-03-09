package phanastrae.mirthdew_encore.dreamtwirl;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.play.DreamtwirlBorder;
import phanastrae.mirthdew_encore.duck.LevelDuckInterface;
import phanastrae.mirthdew_encore.util.RegionPos;

public class DreamtwirlLevelAttachment {

    public final Level level;
    @Nullable
    private DreamtwirlStageManager dreamtwirlStageManager;

    public DreamtwirlLevelAttachment(Level level) {
        this.level = level;
    }

    public void tick(boolean runsNormally) {
        if(!this.level.isClientSide && this.level instanceof ServerLevel serverLevel) {
            tickServerLevel(serverLevel, runsNormally);
        }
    }

    private void tickServerLevel(ServerLevel level, boolean runsNormally) {
        //EntityList serverEntityList = ((ServerLevelAccessor)level).getEntityList();

        //serverEntityList.forEach(this::tickEntity);

        if(this.dreamtwirlStageManager != null) {
            this.dreamtwirlStageManager.tick(runsNormally);
        }
    }

    /*
    public void tickEntity(Entity entity) {

    }
    */

    @Nullable
    public DreamtwirlStageManager getDreamtwirlStageManager() {
        return this.dreamtwirlStageManager;
    }

    public void setDreamtwirlStageManager(ServerLevel level) {
        this.dreamtwirlStageManager = level.getDataStorage().computeIfAbsent(
                DreamtwirlStageManager.getPersistentStateType(level),
                DreamtwirlStageManager.nameFor(level.dimensionTypeRegistration())
        );
    }

    @Nullable
    public DreamtwirlBorder getDreamtwirlBorder(RegionPos regionPos) {
        if(this.level.isClientSide) {
            // TODO cache this somewhere
            return new DreamtwirlBorder(regionPos);
        } else {
            DreamtwirlStageManager dsm = this.getDreamtwirlStageManager();
            if (dsm == null) return null;

            DreamtwirlStage stage = dsm.getDreamtwirlIfPresent(regionPos);
            if (stage == null) return null;

            return stage.getDreamtwirlBorder();
        }
    }

    @Nullable
    public static DreamtwirlLevelAttachment fromLevel(Level level) {
        return ((LevelDuckInterface) level).mirthdew_encore$getDreamtwirlAttachment();
    }

    public static void findBorderCollision(@Nullable Entity entity, Level level, ImmutableList.Builder<VoxelShape> builder) {
        if(entity == null) return;
        if(entity.level() != level) return;

        DreamtwirlLevelAttachment DTLA = DreamtwirlLevelAttachment.fromLevel(level);
        if(DTLA == null) return;

        RegionPos regionPos = RegionPos.fromEntity(entity);
        DreamtwirlBorder border = DTLA.getDreamtwirlBorder(regionPos);
        if(border != null) {
            builder.add(border.voxelShape);
        }
    }

    public static void tickLevel(Level level) {
        DreamtwirlLevelAttachment DTLA = DreamtwirlLevelAttachment.fromLevel(level);
        if(DTLA == null) return;

        DTLA.tick(level.tickRateManager().runsNormally());
    }

    public static boolean positionsAreInSeparateDreamtwirls(Level level, Vec3 pos1, Vec3 pos2) {
        DreamtwirlLevelAttachment DTLA = DreamtwirlLevelAttachment.fromLevel(level);
        if(DTLA == null) {
            return false;
        } else {
            RegionPos region1 = RegionPos.fromVec3(pos1);
            RegionPos region2 = RegionPos.fromVec3(pos2);
            return !region1.equals(region2);
        }
    }

    public static boolean posInUnstableDreamtwirl(Level level, Vec3 pos) {
        DreamtwirlLevelAttachment DTLA = DreamtwirlLevelAttachment.fromLevel(level);
        if(DTLA == null) return false;

        DreamtwirlStageManager dsm = DTLA.getDreamtwirlStageManager();
        if(dsm == null) return false;

        RegionPos region = RegionPos.fromVec3(pos);
        DreamtwirlStage stage = dsm.getDreamtwirlIfPresent(region);

        return stage == null || stage.isDeletingSelf();
    }

    public static boolean positionsAreInSeparateOrUnstableDreamtwirls(Level level, Vec3 pos1, Vec3 pos2) {
        if(posInUnstableDreamtwirl(level, pos1)) {
            return true;
        } else if(posInUnstableDreamtwirl(level, pos2)) {
            return true;
        } else {
            return positionsAreInSeparateDreamtwirls(level, pos1, pos2);
        }
    }
}
