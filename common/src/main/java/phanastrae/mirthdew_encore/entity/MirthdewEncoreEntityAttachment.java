package phanastrae.mirthdew_encore.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.AcherunePoweredBlock;
import phanastrae.mirthdew_encore.component.type.LinkedAcheruneComponent;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.EntityDreamtwirlData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.Acherune;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.StageAcherunes;
import phanastrae.mirthdew_encore.duck.EntityDuckInterface;
import phanastrae.mirthdew_encore.network.packet.EntityAcheruneWarpingPayload;
import phanastrae.mirthdew_encore.services.XPlatInterface;
import phanastrae.mirthdew_encore.util.BlockPosDimensional;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MirthdewEncoreEntityAttachment {
    public static final String KEY_ACHERUNE_WARPING = "acherune_warping";
    public static final String KEY_SLEEPDATA = "SleepData";
    public static final String KEY_TARGET_ACHERUNE = "target_acherune";

    public static final int WARP_TIME = 80;

    private final Entity entity;
    private final EntityDreamtwirlData entityDreamtwirlData;
    @Nullable private final EntitySleepData entitySleepData;

    private boolean warping = false;
    private @Nullable LinkedAcheruneComponent targetAcherune = null;
    private int warpTicks = 0;

    public MirthdewEncoreEntityAttachment(Entity entity) {
        this.entity = entity;
        this.entityDreamtwirlData = new EntityDreamtwirlData(entity);
        this.entitySleepData = (entity instanceof LivingEntity livingEntity) ? new EntitySleepData(livingEntity) : null;
    }

    public void writeNbt(CompoundTag nbt) {
        RegistryOps<Tag> registryops = this.entity.level().registryAccess().createSerializationContext(NbtOps.INSTANCE);

        if(this.entitySleepData != null) {
            CompoundTag sleepData = new CompoundTag();
            this.entitySleepData.writeNbt(sleepData);
            nbt.put(KEY_SLEEPDATA, sleepData);
        }

        nbt.putBoolean(KEY_ACHERUNE_WARPING, this.warping);
        if(this.targetAcherune != null) {
            LinkedAcheruneComponent.CODEC
                    .encodeStart(registryops, this.targetAcherune)
                    .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to encode linked Acherune data for Entity: '{}'", st))
                    .ifPresent(bpdTag -> nbt.put(KEY_TARGET_ACHERUNE, bpdTag));
        }
    }

    public void readNbt(CompoundTag nbt) {
        RegistryOps<Tag> registryops = this.entity.level().registryAccess().createSerializationContext(NbtOps.INSTANCE);

        if(this.entitySleepData != null) {
            if(nbt.contains(KEY_SLEEPDATA, Tag.TAG_COMPOUND)) {
                CompoundTag sleepData = nbt.getCompound(KEY_SLEEPDATA);
                this.entitySleepData.readNbt(sleepData);
            }
        }

        if(nbt.contains(KEY_ACHERUNE_WARPING, Tag.TAG_BYTE)) {
            this.warping = nbt.getBoolean(KEY_ACHERUNE_WARPING);
        }

        if(nbt.contains(KEY_ACHERUNE_WARPING, Tag.TAG_COMPOUND)) {
            LinkedAcheruneComponent.CODEC
                    .parse(registryops, nbt.get(KEY_ACHERUNE_WARPING))
                    .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to parse linked Acherune data for Entity: '{}'", st))
                    .ifPresent(this::startWarp);
        } else {
            this.stopWarping();
        }
    }

    public void sendPairingData(ServerPlayer player) {
        if(this.warping) {
            XPlatInterface.INSTANCE.sendPayload(player, new EntityAcheruneWarpingPayload(this.entity.getId(), this.warping));
        }
    }

    public void tick() {
        Level level = this.entity.level();
        boolean clientSide = level.isClientSide;
        RandomSource random = this.entity.getRandom();

        this.entityDreamtwirlData.tick();
        if(this.entitySleepData != null) {
            this.entitySleepData.tick();
        }

        if(this.warping) {
            if(keepWarping()) {
                this.warpTicks++;

                this.entity.setDeltaMovement(this.entity.getDeltaMovement().lerp(new Vec3(0, 0.025, 0), 0.13));

                for(int i = 0; i < 3; i++) {
                    level.addParticle(
                            ParticleTypes.ENCHANT,
                            this.entity.getX() + this.entity.getBbWidth() * (random.nextFloat() - 0.5),
                            this.entity.getY() + this.entity.getBbHeight() * random.nextFloat(),
                            this.entity.getZ() + this.entity.getBbWidth() * (random.nextFloat() - 0.5),
                            (random.nextFloat() - 0.5) * 0.1,
                            (random.nextFloat() - 0.5) * 0.1,
                            (random.nextFloat() - 0.5) * 0.1
                    );
                }

                if(this.warpTicks > WARP_TIME && !clientSide) {
                    LinkedAcheruneComponent lac = this.targetAcherune;
                    this.stopWarping();

                    this.warp(lac);
                }
            } else {
                if(!clientSide) {
                    this.entity.fallDistance = -5;
                    this.stopWarping();
                }
            }
        }
    }

    public void warp(LinkedAcheruneComponent lac) {
        if(!(this.entity.level() instanceof ServerLevel serverLevel)) return;
        MinecraftServer server = serverLevel.getServer();

        DreamtwirlStage stage = lac.getStage(server);
        if(stage == null) return;
        StageAcherunes stageAcherunes = stage.getStageAcherunes();

        Acherune acherune = lac.getAcherune(server);
        if(acherune == null) return;

        if (acherune.validateLinkedPos(server, stageAcherunes)) {
            BlockPosDimensional linkedPos = acherune.getLinkedPos();
            if (linkedPos != null) {
                if (linkedPos.getLevel(server) instanceof ServerLevel linkedLevel) {
                    Vec3 targetPos = linkedPos.getPos().above().getBottomCenter();

                    this.entity.level().playSound(null, this.entity, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 0.4F, 1F);

                    this.entity.teleportTo(
                            linkedLevel,
                            targetPos.x,
                            targetPos.y,
                            targetPos.z,
                            Set.of(),
                            this.entity.getYRot(),
                            this.entity.getXRot()
                    );

                    linkedLevel.playSound(null, this.entity, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 0.4F, 1F);
                }
            }
        }
    }

    public boolean keepWarping() {
        return !this.entity.isCrouching() && !(this.entity instanceof Player player && player.getAbilities().flying);
    }

    public void onJump() {
        Level level = this.entity.level();
        if(!level.isClientSide) {
            if(!this.warping) {
                BlockPos floorPos = this.entity.getBlockPosBelowThatAffectsMyMovement();
                BlockState floorState = level.getBlockState(floorPos);
                int power = AcherunePoweredBlock.getPower(floorState);
                if (power > 0) {
                    this.tryStartWarp(floorPos, floorState);
                }
            }
        }
    }

    public void tryStartWarp(BlockPos floorPos, BlockState floorState) {
        Level level = this.entity.level();
        Optional<BlockPos> candidatePosOptional = getAcherunePos(level, level.getRandom(), floorPos, 0);

        if(candidatePosOptional.isEmpty()) return;
        BlockPos candidatePos = candidatePosOptional.get();

        DreamtwirlStage stage = DreamtwirlStageManager.getStage(level, RegionPos.fromBlockPos(candidatePos));
        if(stage == null) return;

        Acherune ac = stage.getStageAcherunes().getAcherune(candidatePos);
        if(ac == null) return;

        LinkedAcheruneComponent lac = LinkedAcheruneComponent.fromAcheruneAndStage(stage, ac);
        this.startWarp(lac);
    }

    public void startWarp(LinkedAcheruneComponent acherune) {
        this.targetAcherune = acherune;
        this.warpTicks = 0;
        this.setWarping(true);
    }

    public void stopWarping() {
        this.targetAcherune = null;
        this.warpTicks = 0;
        this.setWarping(false);
    }

    public Optional<BlockPos> getAcherunePos(Level level, RandomSource random, BlockPos pos, int depth) {
        if(depth > 32) return Optional.empty();

        int power = AcherunePoweredBlock.getPower(level.getBlockState(pos));
        if(power == 32) {
            return Optional.of(pos);
        }

        List<BlockPos> next = new ObjectArrayList<>();
        for(Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos adjPos = pos.relative(direction);
            BlockState adjState = level.getBlockState(adjPos);
            int adjPower = AcherunePoweredBlock.getPower(adjState);
            if(adjPower > power) {
                next.add(adjPos);
            }
        }

        if(next.isEmpty()) {
            return Optional.empty();
        } else {
            BlockPos nextPos = next.get(random.nextInt(next.size()));
            return getAcherunePos(level, random, nextPos, depth + 1);
        }
    }

    public boolean shouldCancelGravity() {
        return this.warping;
    }

    public float getWarpStartProgress(float partialTicks) {
        int warpTicks = this.getWarpTicks();
        float warpTime = warpTicks + partialTicks;
        return Math.clamp(1.4F * warpTime / (float)MirthdewEncoreEntityAttachment.WARP_TIME, 0, 1);
    }

    public float getWarpEndProgress(float partialTicks) {
        int warpTicks = this.getWarpTicks();
        float warpTime = warpTicks + partialTicks;
        return Math.clamp((1.4F * warpTime / (float)MirthdewEncoreEntityAttachment.WARP_TIME - 1F) * 2.5F, 0, 1F);
    }

    public EntityDreamtwirlData getDreamtwirlEntityData() {
        return this.entityDreamtwirlData;
    }

    @Nullable
    public EntitySleepData getEntitySleepData() {
        return this.entitySleepData;
    }

    public void setWarping(boolean warping) {
        boolean wasWarping = this.warping;
        this.warping = warping;

        if(warping != wasWarping) {
            Level level = this.entity.level();
            if(!level.isClientSide) {
                EntityAcheruneWarpingPayload payload = new EntityAcheruneWarpingPayload(this.entity.getId(), warping);

                XPlatInterface.INSTANCE.sendToPlayersTrackingEntity(this.entity, payload);
                if(this.entity instanceof ServerPlayer player && player.connection != null) {
                    XPlatInterface.INSTANCE.sendPayload(player, payload);
                }
            }
        }
    }

    public boolean isWarping() {
        return warping;
    }

    public void setWarpTicks(int warpTicks) {
        this.warpTicks = warpTicks;
    }

    public int getWarpTicks() {
        return warpTicks;
    }

    public static MirthdewEncoreEntityAttachment fromEntity(Entity entity) {
        return ((EntityDuckInterface)entity).mirthdew_encore$getAttachment();
    }
}
