package phanastrae.mirthdew_encore.dreamtwirl;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.EntityList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.duck.WorldDuckInterface;
import phanastrae.mirthdew_encore.mixin.ServerWorldAccessor;
import phanastrae.mirthdew_encore.util.RegionPos;

public class DreamtwirlWorldAttachment {

    public final World world;
    @Nullable
    private DreamtwirlStageManager dreamtwirlStageManager;

    public DreamtwirlWorldAttachment(World world) {
        this.world = world;
    }

    public void tick() {
        if(!this.world.isClient && this.world instanceof ServerWorld serverWorld) {
            tickServer(serverWorld);
        }
    }

    private void tickServer(ServerWorld serverWorld) {
        EntityList serverEntityList = ((ServerWorldAccessor)serverWorld).getEntityList();

        serverEntityList.forEach(this::tickEntity);

        if(this.dreamtwirlStageManager != null) {
            this.dreamtwirlStageManager.tick();
        }
    }

    public void tickEntity(Entity entity) {

    }

    @Nullable
    public DreamtwirlStageManager getDreamtwirlStageManager() {
        return this.dreamtwirlStageManager;
    }

    public void setDreamtwirlStageManager(ServerWorld serverWorld) {
        this.dreamtwirlStageManager = serverWorld.getPersistentStateManager().getOrCreate(
                DreamtwirlStageManager.getPersistentStateType(serverWorld),
                DreamtwirlStageManager.nameFor(serverWorld.getDimensionEntry())
        );
    }

    public DreamtwirlBorder getDreamtwirlBorder(RegionPos regionPos) {
        return new DreamtwirlBorder(regionPos);
    }

    @Nullable
    public static DreamtwirlWorldAttachment fromWorld(World world) {
        return ((WorldDuckInterface)world).mirthdew_encore$getDreamtwirlAttachment();
    }

    public static void findBorderCollision(@Nullable Entity entity, World world, ImmutableList.Builder<VoxelShape> builder) {
        if(entity == null) return;
        if(entity.getWorld() != world) return;

        DreamtwirlWorldAttachment DTWA = DreamtwirlWorldAttachment.fromWorld(world);
        if(DTWA == null) return;

        RegionPos regionPos = RegionPos.fromEntity(entity);
        DreamtwirlBorder border = DTWA.getDreamtwirlBorder(regionPos);
        builder.add(border.voxelShape);
    }

    public static void tickWorld(World world) {
        DreamtwirlWorldAttachment DTWA = DreamtwirlWorldAttachment.fromWorld(world);
        if(DTWA == null) return;

        DTWA.tick();
    }

    public static boolean positionsAreInSeperateDreamtwirls(World world, Vec3d pos1, Vec3d pos2) {
        DreamtwirlWorldAttachment DTWA = DreamtwirlWorldAttachment.fromWorld(world);
        if(DTWA == null) {
            return false;
        } else {
            RegionPos region1 = RegionPos.fromVec3d(pos1);
            RegionPos region2 = RegionPos.fromVec3d(pos2);
            return !region1.equals(region2);
        }
    }
}
