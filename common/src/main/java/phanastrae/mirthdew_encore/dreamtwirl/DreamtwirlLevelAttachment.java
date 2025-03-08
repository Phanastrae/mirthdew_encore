package phanastrae.mirthdew_encore.dreamtwirl;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
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

    public DreamtwirlBorder getDreamtwirlBorder(RegionPos regionPos) {
        return new DreamtwirlBorder(regionPos);
    }

    @Nullable
    public static DreamtwirlLevelAttachment fromLevel(Level level) {
        return ((LevelDuckInterface) level).mirthdew_encore$getDreamtwirlAttachment();
    }

    public static void findBorderCollision(@Nullable Entity entity, Level level, ImmutableList.Builder<VoxelShape> builder) {
        if(entity == null) return;
        if(entity.level() != level) return;

        DreamtwirlLevelAttachment DTWA = DreamtwirlLevelAttachment.fromLevel(level);
        if(DTWA == null) return;

        RegionPos regionPos = RegionPos.fromEntity(entity);
        DreamtwirlBorder border = DTWA.getDreamtwirlBorder(regionPos);
        builder.add(border.voxelShape);
    }

    public static void tickLevel(Level level) {
        DreamtwirlLevelAttachment DTWA = DreamtwirlLevelAttachment.fromLevel(level);
        if(DTWA == null) return;

        DTWA.tick(level.tickRateManager().runsNormally());
    }

    public static boolean positionsAreInSeperateDreamtwirls(Level level, Vec3 pos1, Vec3 pos2) {
        DreamtwirlLevelAttachment DTWA = DreamtwirlLevelAttachment.fromLevel(level);
        if(DTWA == null) {
            return false;
        } else {
            RegionPos region1 = RegionPos.fromVec3d(pos1);
            RegionPos region2 = RegionPos.fromVec3d(pos2);
            return !region1.equals(region2);
        }
    }
}
