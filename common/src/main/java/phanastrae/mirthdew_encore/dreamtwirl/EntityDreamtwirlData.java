package phanastrae.mirthdew_encore.dreamtwirl;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.play.DreamtwirlBorder;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityAttachment;
import phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects;
import phanastrae.mirthdew_encore.mixin.ProjectileAccessor;
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
        Level world = this.entity.level();
        if(!world.isClientSide()) {
            DreamtwirlLevelAttachment DTWA = DreamtwirlLevelAttachment.fromLevel(world);
            boolean nowInDreamtwirl = DTWA != null;

            RegionPos entityRegion = nowInDreamtwirl ? RegionPos.fromEntity(this.entity) : null;
            if (this.inDreamtwirl != nowInDreamtwirl) {
                this.setDreamtwirlRegion(entityRegion);
            }

            if (!nowInDreamtwirl) return;
            if (world.getGameTime() % 80L == 0L) {
                this.applyDreamtwirlEffects();
            }

            if (!shouldIgnoreBorder()) {
                DreamtwirlBorder dreamtwirlBorder = DTWA.getDreamtwirlBorder(this.dreamtwirlRegion);
                boolean touchingBorder = dreamtwirlBorder != null && dreamtwirlBorder.entityTouchingBorder(this.entity);

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

                    if (this.entity instanceof Projectile projectileEntity) {
                        HitResult hitResult = dreamtwirlBorder.voxelShape.clip(entity.position(), entity.position().add(entity.getDeltaMovement()), BlockPos.containing(entity.position()));
                        if (hitResult != null && !hitResult.getType().equals(HitResult.Type.MISS)) {
                            ((ProjectileAccessor) projectileEntity).invokeOnHit(hitResult);
                        }
                    }
                }
            }
        }
    }

    public void applyDreamtwirlEffects() {
        if (this.entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY, 200, 0, true, true));
            livingEntity.addEffect(new MobEffectInstance(MirthdewEncoreStatusEffects.MIRTHFUL_ENTRY, 200, 0, true, true));
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
            Vec3 vec3d = new Vec3(dreamtwirlRegion.getCenterX(), 64, dreamtwirlRegion.getCenterZ());
            DimensionTransition teleportTarget = createTeleportTarget(MirthdewEncoreDimensions.DREAMTWIRL_WORLD, vec3d);
            if(teleportTarget == null) {
                return false;
            }

            if(teleportEntity(this.entity, teleportTarget)) {
                if(this.entity instanceof Player) {
                    MirthdewEncore.LOGGER.info("Player {} was sent to the Dreamtwirl ({}, {})", this.entity.getName().getString(), dreamtwirlRegion.regionX, dreamtwirlRegion.regionZ);
                }
                this.setDreamtwirlRegion(dreamtwirlRegion);
                this.applyDreamtwirlEffects();
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
            DimensionTransition teleportTarget;
            if(this.entity instanceof ServerPlayer serverPlayerEntity) {
                teleportTarget = serverPlayerEntity.findRespawnPositionAndUseSpawnBlock(false, DimensionTransition.DO_NOTHING);
            } else {
                Level currentWorld = this.entity.level();
                if (!(currentWorld instanceof ServerLevel)) {
                    return false;
                }
                ResourceKey<Level> targetKey = Level.OVERWORLD;
                MinecraftServer server = currentWorld.getServer();
                ServerLevel targetWorld = server.getLevel(targetKey);
                if (targetWorld == null) {
                    return false;
                }

                BlockPos blockPos = targetWorld.getSharedSpawnPos();
                Vec3 vec3d = this.entity.adjustSpawnLocation(targetWorld, blockPos).getBottomCenter();
                teleportTarget = createTeleportTarget(targetWorld, vec3d);
            }

            if (teleportEntity(this.entity, teleportTarget)) {
                if(this.entity instanceof Player) {
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
        return this.entity.canUsePortal(true) && this.entity instanceof Player;
    }

    public void setDreamtwirlRegion(@Nullable RegionPos region) {
        this.dreamtwirlRegion = region;
        this.inDreamtwirl = !(region == null);
    }

    public static VoxelShape addCollisionsTo(@Nullable VoxelShape original, Entity entity) {
        DreamtwirlLevelAttachment DTWA = DreamtwirlLevelAttachment.fromLevel(entity.level());
        if(DTWA == null) {
            return original;
        }
        RegionPos regionPos = RegionPos.fromEntity(entity);
        DreamtwirlBorder dreamtwirlBorder = DTWA.getDreamtwirlBorder(regionPos);
        if(dreamtwirlBorder == null) {
            return original;
        } else {
            VoxelShape newShape = dreamtwirlBorder.voxelShape;

            if (original == null) {
                return newShape;
            } else {
                return Shapes.joinUnoptimized(original, newShape, BooleanOp.OR);
            }
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

    public static boolean teleportEntity(Entity entity, DimensionTransition teleportTarget) {
        Level currentWorld = entity.level();
        if(!(currentWorld instanceof ServerLevel serverWorld)) {
            return false;
        }
        MinecraftServer server = currentWorld.getServer();
        ServerLevel targetWorld = teleportTarget.newLevel();

        if (server.isLevelEnabled(targetWorld)
                && (targetWorld.dimension() == serverWorld.dimension() || entity.canChangeDimensions(serverWorld, targetWorld))) {
            entity.fallDistance = 0;
            entity.changeDimension(teleportTarget);
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public DimensionTransition createTeleportTarget(ResourceKey<Level> worldKey, Vec3 position) {
        Level currentWorld = entity.level();
        if(!(currentWorld instanceof ServerLevel serverWorld)) {
            return null;
        }
        MinecraftServer server = currentWorld.getServer();
        ServerLevel targetWorld = server.getLevel(worldKey);
        if(targetWorld == null) {
            return null;
        }

        return createTeleportTarget(targetWorld, position);
    }

    public DimensionTransition createTeleportTarget(ServerLevel targetWorld, Vec3 position) {
        return new DimensionTransition(
                targetWorld,
                position,
                Vec3.ZERO,
                entity.getYRot(),
                entity.getXRot(),
                DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET)
        );
    }
}
