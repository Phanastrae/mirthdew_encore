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
import phanastrae.mirthlight_encore.world.dimension.MirthlightEncoreDimensions;

public class DreamtwirlEntityAttachment {

    private final Entity entity;

    boolean inDreamtwirl = false;
    @Nullable
    RegionPos dreamtwirlRegion;

    public DreamtwirlEntityAttachment(Entity entity) {
        this.entity = entity;
    }

    public void tick() {
        World world = this.entity.getWorld();
        DreamtwirlWorldAttachment DTWA = DreamtwirlWorldAttachment.fromWorld(world);
        boolean nowInDreamtwirl = DTWA != null;

        RegionPos entityRegion = RegionPos.fromEntity(this.entity);
        if(this.inDreamtwirl != nowInDreamtwirl) {
            this.setDreamtwirlRegion(entityRegion);
        }

        if(DTWA == null || !this.inDreamtwirl) return;

        if(!shouldIgnoreBorder()) {
            DreamtwirlBorder dreamtwirlBorder = DTWA.getDreamtwirlBorder(RegionPos.fromEntity(this.entity));

            boolean touchingBorder = dreamtwirlBorder.entityTouchingBorder(this.entity);

            if (touchingBorder) {
                if(this.canLeave()) {
                    if (leaveDreamtwirl()) {
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

    public boolean isInDreamtwirl() {
        return this.inDreamtwirl;
    }

    @Nullable
    public RegionPos getDreamtwirlRegion() {
        return this.dreamtwirlRegion;
    }

    public boolean isInDreamtwirlRegion(RegionPos dreamtwirlRegion) {
        return dreamtwirlRegion.equals(this.dreamtwirlRegion);
    }

    public boolean joinDreamtwirl(RegionPos dreamtwirlRegion) {
        if(this.isInDreamtwirlRegion(dreamtwirlRegion)) {
            return false;
        } else {
            Vec3d vec3d = new Vec3d(dreamtwirlRegion.getCenterX(), 64, dreamtwirlRegion.getCenterZ());
            if(teleportEntity(entity, MirthlightEncoreDimensions.DREAMTWIRL_WORLD, vec3d)) {
                this.setDreamtwirlRegion(dreamtwirlRegion);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean leaveDreamtwirl() {
        if(!this.isInDreamtwirl()) {
            return false;
        } else {
            World currentWorld = this.entity.getWorld();
            if (!(currentWorld instanceof ServerWorld serverWorld)) {
                return false;
            }
            RegistryKey<World> targetKey = World.OVERWORLD;
            MinecraftServer server = currentWorld.getServer();
            ServerWorld targetWorld = server.getWorld(targetKey);
            if (targetWorld == null) {
                return false;
            }

            BlockPos blockPos = targetWorld.getSpawnPos();
            Vec3d vec3d = entity.getWorldSpawnPos(targetWorld, blockPos).toBottomCenterPos();

            if (teleportEntity(entity, World.OVERWORLD, vec3d)) {
                this.setDreamtwirlRegion(null);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean shouldIgnoreBorder() {
        return this.entity.isSpectator();
    }

    public boolean canLeave() {
        return this.entity.canUsePortals(true) && this.entity instanceof PlayerEntity;
    }

    public void setDreamtwirlRegion(@Nullable RegionPos region) {
        this.dreamtwirlRegion = region;
        this.inDreamtwirl = !(region == null);
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

    public static boolean joinDreamtwirl(Entity entity, RegionPos dreamtwirlRegion) {
        DreamtwirlEntityAttachment DTEA = DreamtwirlEntityAttachment.fromEntity(entity);
        return DTEA.joinDreamtwirl(dreamtwirlRegion);
    }

    public static boolean leaveDreamtwirl(Entity entity) {
        DreamtwirlEntityAttachment DTEA = DreamtwirlEntityAttachment.fromEntity(entity);
        return DTEA.leaveDreamtwirl();
    }

    public static boolean teleportEntity(Entity entity, RegistryKey<World> worldKey, Vec3d position) {
        World currentWorld = entity.getWorld();
        if(!(currentWorld instanceof ServerWorld serverWorld)) {
            return false;
        }
        MinecraftServer server = currentWorld.getServer();
        ServerWorld targetWorld = server.getWorld(worldKey);
        if(targetWorld == null) {
            return false;
        }

        TeleportTarget teleportTarget = new TeleportTarget(
                targetWorld,
                position,
                entity.getVelocity(),
                entity.getYaw(),
                entity.getPitch(),
                TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)
        );

        if (server.isWorldAllowed(targetWorld)
                && (targetWorld.getRegistryKey() == serverWorld.getRegistryKey() || entity.canTeleportBetween(serverWorld, targetWorld))) {
            entity.teleportTo(teleportTarget);
            return true;
        } else {
            return false;
        }
    }
}
