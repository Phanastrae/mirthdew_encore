package phanastrae.mirthlight_encore.dreamtwirl;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.EntityList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthlight_encore.duck.WorldDuckInterface;
import phanastrae.mirthlight_encore.mixin.ServerWorldAccessor;
import phanastrae.mirthlight_encore.util.RegionPos;

public class DreamtwirlWorldAttachment {

    public final World world;

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
    }

    public void tickEntity(Entity entity) {

    }

    public DreamtwirlBorder getDreamtwirlBorder(RegionPos regionPos) {
        return new DreamtwirlBorder(regionPos);
    }

    @Nullable
    public static DreamtwirlWorldAttachment fromWorld(World world) {
        return ((WorldDuckInterface)world).mirthlight_encore$getDreamtwirlAttachment();
    }

    public static boolean isDreamtwirl(World world) {
        return fromWorld(world) != null;
    }

    public static void findBorderCollision(@Nullable Entity entity, World world, ImmutableList.Builder<VoxelShape> builder) {
        if(entity == null) return;

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
