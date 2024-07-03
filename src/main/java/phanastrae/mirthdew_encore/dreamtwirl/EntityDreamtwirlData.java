package phanastrae.mirthdew_encore.dreamtwirl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
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
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityAttachment;
import phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects;
import phanastrae.mirthdew_encore.mixin.ProjectileEntityAccessor;
import phanastrae.mirthdew_encore.util.RegionPos;
import phanastrae.mirthdew_encore.world.dimension.MirthdewEncoreDimensions;

public class EntityDreamtwirlData {

    private final Entity entity;

    boolean inDreamtwirl = false;
    @Nullable
    RegionPos dreamtwirlRegion;

    public EntityDreamtwirlData(Entity entity) {
        this.entity = entity;
    }

    public void tick() {
        World world = this.entity.getWorld();
        if(!world.isClient()) {
            DreamtwirlWorldAttachment DTWA = DreamtwirlWorldAttachment.fromWorld(world);
            boolean nowInDreamtwirl = DTWA != null;

            RegionPos entityRegion = nowInDreamtwirl ? RegionPos.fromEntity(this.entity) : null;
            if (this.inDreamtwirl != nowInDreamtwirl) {
                this.setDreamtwirlRegion(entityRegion);
            }

            if (!nowInDreamtwirl) return;
            if (world.getTime() % 80L == 0L) {
                if (this.entity instanceof LivingEntity livingEntity) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY, 200, 0, true, true));
                }
                if (this.entity instanceof LivingEntity livingEntity) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(MirthdewEncoreStatusEffects.MIRTHFUL_ENTRY, 200, 0, true, true));
                }
            }

            if (!shouldIgnoreBorder()) {
                DreamtwirlBorder dreamtwirlBorder = DTWA.getDreamtwirlBorder(this.dreamtwirlRegion);
                boolean touchingBorder = dreamtwirlBorder.entityTouchingBorder(this.entity);

                if (this.dreamtwirlRegion != entityRegion) {
                    DreamtwirlStageManager dreamtwirlStageManager = DreamtwirlStageManager.getDreamtwirlStageManager(world);
                    if (dreamtwirlStageManager != null) {
                        DreamtwirlStage currentStage = dreamtwirlStageManager.getDreamtwirlIfPresent(entityRegion);
                        DreamtwirlStage newStage = dreamtwirlStageManager.getDreamtwirlIfPresent(entityRegion);
                        if (newStage != null) {
                            if (this.canJoinRegion(entityRegion)) {
                                this.setDreamtwirlRegion(entityRegion);
                            } else if (currentStage == null) {
                                touchingBorder = true;
                            }
                        } else {
                            touchingBorder = true;
                        }
                    }
                }

                if (touchingBorder) {
                    if (this.canLeave()) {
                        if (leaveDreamtwirl()) {
                            return;
                        }
                    }

                    if (this.entity instanceof ProjectileEntity projectileEntity) {
                        HitResult hitResult = dreamtwirlBorder.voxelShape.raycast(entity.getPos(), entity.getPos().add(entity.getVelocity()), BlockPos.ofFloored(entity.getPos()));
                        if (hitResult != null && !hitResult.getType().equals(HitResult.Type.MISS)) {
                            ((ProjectileEntityAccessor) projectileEntity).invokeOnCollision(hitResult);
                        }
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

    public boolean canJoinRegion(RegionPos regionPos) {
        return true;
    }

    public boolean joinDreamtwirl(RegionPos dreamtwirlRegion) {
        if(this.isInDreamtwirlRegion(dreamtwirlRegion)) {
            return false;
        } else {
            Vec3d vec3d = new Vec3d(dreamtwirlRegion.getCenterX(), 64, dreamtwirlRegion.getCenterZ());
            TeleportTarget teleportTarget = createTeleportTarget(MirthdewEncoreDimensions.DREAMTWIRL_WORLD, vec3d);
            if(teleportTarget == null) {
                return false;
            }

            if(teleportEntity(this.entity, teleportTarget)) {
                if(this.entity instanceof PlayerEntity) {
                    MirthdewEncore.LOGGER.info("Player {} was sent to the Dreamtwirl ({}, {})", this.entity.getName().getString(), dreamtwirlRegion.regionX, dreamtwirlRegion.regionZ);
                }
                this.setDreamtwirlRegion(dreamtwirlRegion);
                if(this.entity instanceof LivingEntity livingEntity) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY, 200, 0, true, true));
                }
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
            TeleportTarget teleportTarget;
            if(this.entity instanceof ServerPlayerEntity serverPlayerEntity) {
                teleportTarget = serverPlayerEntity.getRespawnTarget(false, TeleportTarget.NO_OP);
            } else {
                World currentWorld = this.entity.getWorld();
                if (!(currentWorld instanceof ServerWorld)) {
                    return false;
                }
                RegistryKey<World> targetKey = World.OVERWORLD;
                MinecraftServer server = currentWorld.getServer();
                ServerWorld targetWorld = server.getWorld(targetKey);
                if (targetWorld == null) {
                    return false;
                }

                BlockPos blockPos = targetWorld.getSpawnPos();
                Vec3d vec3d = this.entity.getWorldSpawnPos(targetWorld, blockPos).toBottomCenterPos();
                teleportTarget = createTeleportTarget(targetWorld, vec3d);
            }

            if (teleportEntity(this.entity, teleportTarget)) {
                if(this.entity instanceof PlayerEntity) {
                    MirthdewEncore.LOGGER.info("Player {} was ejected from a Dreamtwirl", this.entity.getName().getString());
                }
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
        MirthdewEncoreEntityAttachment MEA = MirthdewEncoreEntityAttachment.fromEntity(entity);
        return MEA.getDreamtwirlEntityData().joinDreamtwirl(dreamtwirlRegion);
    }

    public static boolean leaveDreamtwirl(Entity entity) {
        MirthdewEncoreEntityAttachment MEA = MirthdewEncoreEntityAttachment.fromEntity(entity);
        return MEA.getDreamtwirlEntityData().leaveDreamtwirl();
    }

    public static boolean teleportEntity(Entity entity, TeleportTarget teleportTarget) {
        World currentWorld = entity.getWorld();
        if(!(currentWorld instanceof ServerWorld serverWorld)) {
            return false;
        }
        MinecraftServer server = currentWorld.getServer();
        ServerWorld targetWorld = teleportTarget.world();

        if (server.isWorldAllowed(targetWorld)
                && (targetWorld.getRegistryKey() == serverWorld.getRegistryKey() || entity.canTeleportBetween(serverWorld, targetWorld))) {
            entity.fallDistance = 0;
            entity.teleportTo(teleportTarget);
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public TeleportTarget createTeleportTarget(RegistryKey<World> worldKey, Vec3d position) {
        World currentWorld = entity.getWorld();
        if(!(currentWorld instanceof ServerWorld serverWorld)) {
            return null;
        }
        MinecraftServer server = currentWorld.getServer();
        ServerWorld targetWorld = server.getWorld(worldKey);
        if(targetWorld == null) {
            return null;
        }

        return createTeleportTarget(targetWorld, position);
    }

    public TeleportTarget createTeleportTarget(ServerWorld targetWorld, Vec3d position) {
        return new TeleportTarget(
                targetWorld,
                position,
                Vec3d.ZERO,
                entity.getYaw(),
                entity.getPitch(),
                TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)
        );
    }
}
