package phanastrae.mirthlight_encore.dreamtwirl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthlight_encore.duck.EntityDuckInterface;
import phanastrae.mirthlight_encore.mixin.ProjectileEntityAccessor;
import phanastrae.mirthlight_encore.util.RegionPos;

public class DreamtwirlEntityAttachment {

    private final Entity entity;

    boolean inDreamtwirl = false;

    public DreamtwirlEntityAttachment(Entity entity) {
        this.entity = entity;
    }

    public void tick() {
        World world = this.entity.getWorld();
        DreamtwirlWorldAttachment DTWA = DreamtwirlWorldAttachment.fromWorld(world);
        boolean nowInDreamtwirl = DTWA != null;

        if(this.inDreamtwirl != nowInDreamtwirl) {
            this.inDreamtwirl = nowInDreamtwirl;
        }

        if(!inDreamtwirl) return;

        if(!shouldIgnoreBorder()) {
            DreamtwirlBorder dreamtwirlBorder = DTWA.getDreamtwirlBorder(RegionPos.fromEntity(this.entity));

            boolean touchingBorder = dreamtwirlBorder.entityTouchingBorder(this.entity);

            if (touchingBorder) {
                if(this.canLeave()) {
                    if (tryToLeave()) {
                        return;
                    }
                }

                if(this.entity instanceof ProjectileEntity projectileEntity) {
                    HitResult hitResult = dreamtwirlBorder.voxelShape.raycast(entity.getPos(), entity.getPos().add(entity.getVelocity()), BlockPos.ofFloored(entity.getPos()));
                    if(hitResult != null && !hitResult.getType().equals(HitResult.Type.MISS)) {
                        ((ProjectileEntityAccessor) projectileEntity).invokeOnCollision(hitResult);
                    }
                }
            }
        }
    }

    public boolean tryToLeave() {
        World currentWorld = this.entity.getWorld();
        if(!(currentWorld instanceof ServerWorld serverWorld)) {
            return false;
        }
        RegistryKey<World> targetKey = World.OVERWORLD;
        MinecraftServer server = currentWorld.getServer();
        ServerWorld targetWorld = server.getWorld(targetKey);
        if(targetWorld == null) {
            return false;
        }
        BlockPos blockPos = targetWorld.getSpawnPos();
        Vec3d vec3d = entity.getWorldSpawnPos(targetWorld, blockPos).toBottomCenterPos();
        float f = entity.getYaw();

        TeleportTarget teleportTarget = new TeleportTarget(
                targetWorld,
                vec3d,
                entity.getVelocity(),
                f,
                entity.getPitch(),
                TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)
        );

        ServerWorld targetServerWorld = teleportTarget.world();
        if (server.isWorldAllowed(targetServerWorld)
                && (targetServerWorld.getRegistryKey() == serverWorld.getRegistryKey() || this.entity.canTeleportBetween(serverWorld, targetServerWorld))) {
            this.leaveDreamtwirl(teleportTarget);
            return true;
        } else {
            return false;
        }
    }

    public boolean shouldIgnoreBorder() {
        return this.entity.isSpectator();
    }

    public boolean canLeave() {
        return this.entity.canUsePortals(true) && this.entity instanceof PlayerEntity;
    }

    public void leaveDreamtwirl(TeleportTarget teleportTarget) {
        this.inDreamtwirl = false;
        this.entity.teleportTo(teleportTarget);
    }

    public static DreamtwirlEntityAttachment fromEntity(Entity entity) {
        return ((EntityDuckInterface)entity).mirthlight_encore$getDreamtwirlAttachment();
    }

    public static VoxelShape addCollisionsTo(@Nullable VoxelShape original, Entity entity) {
        DreamtwirlWorldAttachment DTWA = DreamtwirlWorldAttachment.fromWorld(entity.getWorld());
        if(DTWA == null) {
            return original;
        }
        RegionPos regionPos = RegionPos.fromEntity(entity);
        DreamtwirlBorder dreamtwirlBorder = DTWA.getDreamtwirlBorder(regionPos);
        VoxelShape newShape = dreamtwirlBorder.voxelShape;

        if(original == null) {
            return newShape;
        } else {
            return VoxelShapes.combine(original, newShape, BooleanBiFunction.OR);
        }
    }
}
