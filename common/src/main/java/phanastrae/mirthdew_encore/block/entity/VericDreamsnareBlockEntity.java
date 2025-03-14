package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.entity.DreamspeckEntity;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreDamageTypes;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

import static phanastrae.mirthdew_encore.entity.MirthdewEncoreDamageTypes.DREAMSNARE_TONGUE;

public class VericDreamsnareBlockEntity extends BlockEntity implements GameEventListener.Provider<VibrationSystem.Listener>, VibrationSystem {
    private static final Vec3 CENTER_POS = new Vec3(0.5, 0.5, 0.5);
    private static final Logger LOGGER = MirthdewEncore.LOGGER;
    private final VibrationSystem.User vibrationCallback = new VericDreamsnareBlockEntity.VibrationCallback();
    private VibrationSystem.Data vibrationListenerData = new VibrationSystem.Data();
    private final VibrationSystem.Listener vibrationListener = new VibrationSystem.Listener(this);

    private Vec3 baseOffset = CENTER_POS;
    private Vec3 tongueBaseOffset = CENTER_POS;

    private Vec3 tongueTargetOffset = CENTER_POS;
    private Vec3 tongueOffset = CENTER_POS;

    private boolean tongueExtended = false;
    private boolean entitySnared = false;

    @Nullable
    private Entity snaredEntity = null;

    private final NonNullList<ItemStack> heldItem = NonNullList.withSize(1, ItemStack.EMPTY);

    private double tongueDistance = 0;
    private double prevTongueDistance = 0;

    public VericDreamsnareBlockEntity(BlockPos pos, BlockState state) {
        super(MirthdewEncoreBlockEntityTypes.VERIC_DREAMSNARE, pos, state);
        this.updateCachedPositions(state);
    }

    @Override
    public VibrationSystem.Data getVibrationData() {
        return this.vibrationListenerData;
    }

    @Override
    public VibrationSystem.User getVibrationUser() {
        return this.vibrationCallback;
    }

    public VibrationSystem.Listener getListener() {
        return this.vibrationListener;
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);

        RegistryOps<Tag> registryOps = registryLookup.createSerializationContext(NbtOps.INSTANCE);
        if (nbt.contains("listener", Tag.TAG_COMPOUND)) {
            VibrationSystem.Data.CODEC
                    .parse(registryOps, nbt.getCompound("listener"))
                    .resultOrPartial(string -> LOGGER.error("Failed to parse vibration listener for Sculk Shrieker: '{}'", string))
                    .ifPresent(vibrationListener -> this.vibrationListenerData = vibrationListener);
        }

        if(nbt.contains("tongue_target_offset", Tag.TAG_LIST)) {
            ListTag nbtList = nbt.getList("tongue_target_offset", Tag.TAG_DOUBLE);
            if (nbtList != null && nbtList.size() == 3) {
                double x = nbtList.getDouble(0);
                double y = nbtList.getDouble(1);
                double z = nbtList.getDouble(2);
                this.tongueTargetOffset = new Vec3(x, y, z);
            }
        }
        if(nbt.contains("tongue_extended", Tag.TAG_BYTE)) {
            this.tongueExtended = nbt.getBoolean("tongue_extended");
        }
        if(nbt.contains("entity_snared", Tag.TAG_BYTE)) {
            this.entitySnared = nbt.getBoolean("entity_snared");
        }
        if(nbt.contains("tongue_distance", Tag.TAG_DOUBLE)) {
            this.tongueDistance = nbt.getDouble("tongue_distance");
        }

        this.heldItem.clear();
        ContainerHelper.loadAllItems(nbt, this.heldItem, registryLookup);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        RegistryOps<Tag> registryOps = registryLookup.createSerializationContext(NbtOps.INSTANCE);
        VibrationSystem.Data.CODEC
                .encodeStart(registryOps, this.vibrationListenerData)
                .resultOrPartial(string -> LOGGER.error("Failed to encode vibration listener for Sculk Shrieker: '{}'", string))
                .ifPresent(nbtElement -> nbt.put("listener", nbtElement));

        nbt.put("tongue_target_offset", this.toNbtList(this.tongueTargetOffset.x, this.tongueTargetOffset.y, this.tongueTargetOffset.z));
        nbt.putBoolean("tongue_extended", this.tongueExtended);
        nbt.putBoolean("entity_snared", this.entitySnared);
        nbt.putDouble("tongue_distance", this.tongueDistance);

        ContainerHelper.saveAllItems(nbt, this.heldItem, true, registryLookup);
    }

    protected ListTag toNbtList(double... values) {
        ListTag nbtList = new ListTag();

        for(double d : values) {
            nbtList.add(DoubleTag.valueOf(d));
        }

        return nbtList;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag nbtCompound = this.saveCustomOnly(registryLookup);
        nbtCompound.remove("listener");
        return nbtCompound;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        this.updateCachedPositions(state);
    }

    public void updateCachedPositions(BlockState state) {
        Direction facingDirection = state.hasProperty(BlockStateProperties.FACING) ? state.getValue(BlockStateProperties.FACING) : Direction.UP;
        Vec3 directionVector = new Vec3(facingDirection.step());

        this.baseOffset = CENTER_POS.add(directionVector.scale(-0.5));
        this.tongueBaseOffset = this.baseOffset.add(directionVector.scale(3/16F));
    }

    public void setTongueTargetOffset(Vec3 value) {
        this.tongueTargetOffset = value;
        this.setChanged();
    }

    public void setTongueExtended(boolean value) {
        this.tongueExtended = value;
        this.setChanged();
    }

    public void setEntitySnared(boolean value) {
        this.entitySnared = value;
        this.setChanged();
    }

    public void setTongueDistance(double value) {
        this.tongueDistance = value;
        this.setChanged();
    }

    public Vec3 getBaseOffset() {
        return this.baseOffset;
    }

    public Vec3 getTongueBaseOffset() {
        return this.tongueBaseOffset;
    }

    public Vec3 getTongueTargetOffset() {
        return this.tongueTargetOffset;
    }

    public double getTongueLength(float tickDelta) {
        return this.prevTongueDistance + tickDelta * (this.tongueDistance - this.prevTongueDistance);
    }

    public void setHeldItem(ItemStack itemStack) {
        this.heldItem.set(0, itemStack);
        this.setChanged();
    }

    public ItemStack getHeldItem() {
        return this.heldItem.getFirst();
    }

    public boolean isHoldingItem() {
        return !this.getHeldItem().isEmpty();
    }

    public NonNullList<ItemStack> getItems() {
        return this.heldItem;
    }

    public Entity getSnaredEntity() {
        return this.snaredEntity;
    }

    public void sendUpdate() {
        if(this.level != null && !this.level.isClientSide && this.level.getBlockState(this.getBlockPos()) == this.getBlockState()) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VericDreamsnareBlockEntity snare) {
        snare.tick(level, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(!level.isClientSide()) {
            VibrationSystem.Ticker.tick(level, this.getVibrationData(), this.getVibrationUser());
        }

        if(this.tongueDistance <= 0 && !this.tongueExtended) {
            this.setTongueTargetOffset(this.tongueBaseOffset);
        }

        this.prevTongueDistance = this.tongueDistance;
        Vec3 offset = this.tongueTargetOffset.subtract(this.tongueBaseOffset);
        double offsetLength = offset.length();
        if (this.tongueExtended) {
            this.setTongueDistance(Math.min(this.tongueDistance + 0.05 + 0.25 * (offsetLength - this.tongueDistance), offsetLength));
        } else {
            this.setTongueDistance(Math.max(this.tongueDistance - (this.entitySnared ? 0.1 : 0.3), 0));
        }

        if(!level.isClientSide() && level instanceof ServerLevel serverWorld) {
            if(this.tongueDistance != this.prevTongueDistance) {
                this.tongueOffset = this.tongueBaseOffset.add(offset.scale(this.tongueDistance / offsetLength));
            }

            if(this.tongueExtended) {
                Vec3 tonguePos = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(this.tongueOffset);
                Vec3 tMin = tonguePos.subtract(0.2, 0.2, 0.2);
                Vec3 tMax = tonguePos.add(0.2, 0.2, 0.2);
                AABB box = new AABB(tMin, tMax);
                for(Entity entity : level.getEntities(null, box)) {
                    if(entity instanceof DreamspeckEntity dreamspeckEntity && !dreamspeckEntity.isSnared()) {
                        dreamspeckEntity.setSnare(this.getBlockPos());
                        this.snaredEntity = entity;
                        this.setEntitySnared(true);
                        this.setTongueExtended(false);
                        this.sendUpdate();
                    } else {
                        if(!(entity instanceof ItemEntity)) {
                            entity.hurt(MirthdewEncoreDamageTypes.of(level, DREAMSNARE_TONGUE), 2);
                        }
                    }
                }
            }

            if(this.tongueDistance >= offsetLength) {
                this.setTongueExtended(false);
                this.sendUpdate();
            }

            if(this.entitySnared) {
                if(this.snaredEntity != null) {
                    this.snaredEntity.setPos(new Vec3(pos.getX(), pos.getY() - this.snaredEntity.getBbHeight() * 0.5, pos.getZ()).add(this.tongueOffset));
                }
                if(this.tongueDistance <= 0) {
                    if(this.snaredEntity instanceof DreamspeckEntity dreamspeckEntity) {
                        dreamspeckEntity.setSnare(null);
                    }

                    if(!this.isHoldingItem() && this.snaredEntity != null) {
                        this.eatEntity(serverWorld, this.snaredEntity);
                    }

                    this.snaredEntity = null;
                    this.setEntitySnared(false);
                    this.sendUpdate();
                }
            }
        }
    }

    public void eatEntity(ServerLevel level, Entity entity) {
        entity.discard();

        this.setHeldItem(MirthdewEncoreItems.DREAMSEED.getDefaultInstance());

        level.sendParticles(ParticleTypes.SCULK_CHARGE_POP, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, 100, 0.25, 0.25, 0.25, 0.02);
    }

    public void attack(ServerLevel level, @Nullable Entity entity) {
        if (entity == null) return;
        if (level.getDifficulty() == Difficulty.PEACEFUL && !(entity instanceof DreamspeckEntity)) return;

        BlockPos blockPos = this.getBlockPos();
        level.gameEvent(GameEvent.SHRIEK, blockPos, GameEvent.Context.of(entity));

        Vec3 target = entity.position().add(0, entity.getBbHeight() * 0.5, 0);
        this.attack(level, target);
    }

    public void attack(ServerLevel level, Vec3 target) {
        BlockPos pos = this.getBlockPos();
        Vec3 tongueBasePos = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(this.tongueBaseOffset);

        BlockHitResult blockHitResult = level.clip(new ClipContext(
                tongueBasePos,
                target,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                CollisionContext.empty()
        ));

        Vec3 offset = blockHitResult.getLocation().subtract(tongueBasePos);
        double offsetLength = offset.length();
        if(offsetLength > 9.0) {
            return;
        }
        BlockState state = this.getBlockState();
        Direction direction = state.hasProperty(BlockStateProperties.FACING) ? state.getValue(BlockStateProperties.FACING) : Direction.UP;

        double dot = new Vec3(direction.step()).dot(offset.normalize());
        if(dot < 0.6) {
            return;
        }

        this.setTongueTargetOffset(offset.add(this.tongueBaseOffset));
        this.tongueOffset = this.tongueTargetOffset;
        this.setTongueExtended(true);
        this.sendUpdate();

        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.SCULK_SENSOR_PLACE, SoundSource.HOSTILE, 4.0F, 1.4F);
    }

    public boolean canAttack(Entity entity) {
        if(this.tongueDistance > 0) return false;
        if(this.isHoldingItem()) return false;

        if(entity.isSpectator()) {
            return false;
        } else if(entity instanceof Player player && player.getAbilities().instabuild) {
            return false;
        } else {
            return true;
        }
    }

    class VibrationCallback implements VibrationSystem.User {
        private static final int RANGE = 8;
        private final PositionSource positionSource = new BlockPositionSource(VericDreamsnareBlockEntity.this.worldPosition);

        public VibrationCallback() {
        }

        @Override
        public int getListenerRadius() {
            return RANGE;
        }

        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.SHRIEKER_CAN_LISTEN;
        }

        @Override
        public boolean canReceiveVibration(ServerLevel world, BlockPos pos, Holder<GameEvent> event, GameEvent.Context emitter) {
            Entity entity = emitter.sourceEntity();
            if(entity == null) return false;
            return VericDreamsnareBlockEntity.this.canAttack(entity);
        }

        @Override
        public void onReceiveVibration(ServerLevel world, BlockPos pos, Holder<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
            VericDreamsnareBlockEntity.this.attack(world, sourceEntity);
        }

        @Override
        public void onDataChanged() {
            VericDreamsnareBlockEntity.this.setChanged();
        }

        @Override
        public boolean requiresAdjacentChunksToBeTicking() {
            return true;
        }
    }
}
