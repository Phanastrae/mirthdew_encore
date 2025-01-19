package phanastrae.mirthdew_encore.dreamtwirl;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.dreamtwirl.stage.play.DreamtwirlBorder;
import phanastrae.mirthdew_encore.duck.WorldDuckInterface;
import phanastrae.mirthdew_encore.util.RegionPos;

public class DreamtwirlLevelAttachment {

    public final Level world;
    @Nullable
    private DreamtwirlStageManager dreamtwirlStageManager;

    public DreamtwirlLevelAttachment(Level world) {
        this.world = world;
    }

    public void tick() {
        if(!this.world.isClientSide && this.world instanceof ServerLevel serverWorld) {
            tickServer(serverWorld);
        }
    }

    private void tickServer(ServerLevel serverWorld) {
        //EntityList serverEntityList = ((ServerLevelAccessor)serverWorld).getEntityList();

        //serverEntityList.forEach(this::tickEntity);

        if(this.dreamtwirlStageManager != null) {
            this.dreamtwirlStageManager.tick();
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

    public void setDreamtwirlStageManager(ServerLevel serverWorld) {
        this.dreamtwirlStageManager = serverWorld.getDataStorage().computeIfAbsent(
                DreamtwirlStageManager.getPersistentStateType(serverWorld),
                DreamtwirlStageManager.nameFor(serverWorld.dimensionTypeRegistration())
        );
    }

    public DreamtwirlBorder getDreamtwirlBorder(RegionPos regionPos) {
        return new DreamtwirlBorder(regionPos);
    }

    @Nullable
    public static DreamtwirlLevelAttachment fromLevel(Level world) {
        return ((WorldDuckInterface)world).mirthdew_encore$getDreamtwirlAttachment();
    }

    public static void findBorderCollision(@Nullable Entity entity, Level world, ImmutableList.Builder<VoxelShape> builder) {
        if(entity == null) return;
        if(entity.level() != world) return;

        DreamtwirlLevelAttachment DTWA = DreamtwirlLevelAttachment.fromLevel(world);
        if(DTWA == null) return;

        RegionPos regionPos = RegionPos.fromEntity(entity);
        DreamtwirlBorder border = DTWA.getDreamtwirlBorder(regionPos);
        builder.add(border.voxelShape);
    }

    public static void tickWorld(Level world) {
        DreamtwirlLevelAttachment DTWA = DreamtwirlLevelAttachment.fromLevel(world);
        if(DTWA == null) return;

        DTWA.tick();
    }

    public static boolean positionsAreInSeperateDreamtwirls(Level world, Vec3 pos1, Vec3 pos2) {
        DreamtwirlLevelAttachment DTWA = DreamtwirlLevelAttachment.fromLevel(world);
        if(DTWA == null) {
            return false;
        } else {
            RegionPos region1 = RegionPos.fromVec3d(pos1);
            RegionPos region2 = RegionPos.fromVec3d(pos2);
            return !region1.equals(region2);
        }
    }
}
