package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.GameEventTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.entity.DreamspeckEntity;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreDamageTypes;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

import static phanastrae.mirthdew_encore.entity.MirthdewEncoreDamageTypes.DREAMSNARE_TONGUE;

public class VericDreamsnareBlockEntity extends BlockEntity implements GameEventListener.Holder<Vibrations.VibrationListener>, Vibrations {
    private static final Vec3d CENTER_POS = new Vec3d(0.5, 0.5, 0.5);
    private static final Logger LOGGER = MirthdewEncore.LOGGER;
    private final Vibrations.Callback vibrationCallback = new VericDreamsnareBlockEntity.VibrationCallback();
    private Vibrations.ListenerData vibrationListenerData = new Vibrations.ListenerData();
    private final Vibrations.VibrationListener vibrationListener = new Vibrations.VibrationListener(this);

    private Vec3d baseOffset = CENTER_POS;
    private Vec3d tongueBaseOffset = CENTER_POS;

    private Vec3d tongueTargetOffset = CENTER_POS;
    private Vec3d tongueOffset = CENTER_POS;

    private boolean tongueExtended = false;
    private boolean entitySnared = false;

    @Nullable
    private Entity snaredEntity = null;

    private final DefaultedList<ItemStack> heldItem = DefaultedList.ofSize(1, ItemStack.EMPTY);

    private double tongueDistance = 0;
    private double prevTongueDistance = 0;

    public VericDreamsnareBlockEntity(BlockPos pos, BlockState state) {
        super(MirthdewEncoreBlockEntityTypes.VERIC_DREAMSNARE, pos, state);
        this.updateCachedPositions(state);
    }

    @Override
    public Vibrations.ListenerData getVibrationListenerData() {
        return this.vibrationListenerData;
    }

    @Override
    public Vibrations.Callback getVibrationCallback() {
        return this.vibrationCallback;
    }

    public Vibrations.VibrationListener getEventListener() {
        return this.vibrationListener;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        RegistryOps<NbtElement> registryOps = registryLookup.getOps(NbtOps.INSTANCE);
        if (nbt.contains("listener", NbtElement.COMPOUND_TYPE)) {
            Vibrations.ListenerData.CODEC
                    .parse(registryOps, nbt.getCompound("listener"))
                    .resultOrPartial(string -> LOGGER.error("Failed to parse vibration listener for Sculk Shrieker: '{}'", string))
                    .ifPresent(vibrationListener -> this.vibrationListenerData = vibrationListener);
        }

        if(nbt.contains("tongue_target_offset", NbtElement.LIST_TYPE)) {
            NbtList nbtList = nbt.getList("tongue_target_offset", NbtElement.DOUBLE_TYPE);
            if (nbtList != null && nbtList.size() == 3) {
                double x = nbtList.getDouble(0);
                double y = nbtList.getDouble(1);
                double z = nbtList.getDouble(2);
                this.tongueTargetOffset = new Vec3d(x, y, z);
            }
        }
        if(nbt.contains("tongue_extended", NbtElement.BYTE_TYPE)) {
            this.tongueExtended = nbt.getBoolean("tongue_extended");
        }
        if(nbt.contains("entity_snared", NbtElement.BYTE_TYPE)) {
            this.entitySnared = nbt.getBoolean("entity_snared");
        }
        if(nbt.contains("tongue_distance", NbtElement.DOUBLE_TYPE)) {
            this.tongueDistance = nbt.getDouble("tongue_distance");
        }

        this.heldItem.clear();
        Inventories.readNbt(nbt, this.heldItem, registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        RegistryOps<NbtElement> registryOps = registryLookup.getOps(NbtOps.INSTANCE);
        Vibrations.ListenerData.CODEC
                .encodeStart(registryOps, this.vibrationListenerData)
                .resultOrPartial(string -> LOGGER.error("Failed to encode vibration listener for Sculk Shrieker: '{}'", string))
                .ifPresent(nbtElement -> nbt.put("listener", nbtElement));

        nbt.put("tongue_target_offset", this.toNbtList(this.tongueTargetOffset.x, this.tongueTargetOffset.y, this.tongueTargetOffset.z));
        nbt.putBoolean("tongue_extended", this.tongueExtended);
        nbt.putBoolean("entity_snared", this.entitySnared);
        nbt.putDouble("tongue_distance", this.tongueDistance);

        Inventories.writeNbt(nbt, this.heldItem, true, registryLookup);
    }

    protected NbtList toNbtList(double... values) {
        NbtList nbtList = new NbtList();

        for(double d : values) {
            nbtList.add(NbtDouble.of(d));
        }

        return nbtList;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbtCompound = this.createComponentlessNbt(registryLookup);
        nbtCompound.remove("listener");
        return nbtCompound;
    }

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
    }

    @Override
    public void setCachedState(BlockState state) {
        super.setCachedState(state);
        this.updateCachedPositions(state);
    }

    public void updateCachedPositions(BlockState state) {
        Direction facingDirection = state.contains(Properties.FACING) ? state.get(Properties.FACING) : Direction.UP;
        Vec3d directionVector = new Vec3d(facingDirection.getUnitVector());

        this.baseOffset = CENTER_POS.add(directionVector.multiply(-0.5));
        this.tongueBaseOffset = this.baseOffset.add(directionVector.multiply(3/16F));
    }

    public Vec3d getBaseOffset() {
        return this.baseOffset;
    }

    public Vec3d getTongueBaseOffset() {
        return this.tongueBaseOffset;
    }

    public Vec3d getTongueTargetOffset() {
        return this.tongueTargetOffset;
    }

    public double getTongueLength(float tickDelta) {
        return this.prevTongueDistance + tickDelta * (this.tongueDistance - this.prevTongueDistance);
    }

    public void markForUpdate(ServerWorld world) {
        world.getChunkManager().markForUpdate(this.getPos());
    }

    public void setHeldItem(ItemStack itemStack) {
        this.heldItem.set(0, itemStack);
    }

    public ItemStack getHeldItem() {
        return this.heldItem.getFirst();
    }

    public boolean isHoldingItem() {
        return !this.getHeldItem().isEmpty();
    }

    public DefaultedList<ItemStack> getItems() {
        return this.heldItem;
    }

    public Entity getSnaredEntity() {
        return this.snaredEntity;
    }

    public static void tick(World world, BlockPos pos, BlockState state, VericDreamsnareBlockEntity snare) {
        snare.tick(world, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(!world.isClient()) {
            Vibrations.Ticker.tick(world, this.getVibrationListenerData(), this.getVibrationCallback());
        }

        if(this.tongueDistance <= 0 && !this.tongueExtended) {
            this.tongueTargetOffset = this.tongueBaseOffset;
        }

        this.prevTongueDistance = this.tongueDistance;
        Vec3d offset = this.tongueTargetOffset.subtract(this.tongueBaseOffset);
        double offsetLength = offset.length();
        if (this.tongueExtended) {
            this.tongueDistance = Math.min(this.tongueDistance + 0.05 + 0.25 * (offsetLength - this.tongueDistance), offsetLength);
        } else {
            this.tongueDistance = Math.max(this.tongueDistance - (this.entitySnared ? 0.1 : 0.3), 0);
        }

        if(!world.isClient() && world instanceof ServerWorld serverWorld) {
            if(this.tongueDistance != this.prevTongueDistance) {
                this.tongueOffset = this.tongueBaseOffset.add(offset.multiply(this.tongueDistance / offsetLength));
            }

            if(this.tongueExtended) {
                Vec3d tonguePos = new Vec3d(pos.getX(), pos.getY(), pos.getZ()).add(this.tongueOffset);
                Vec3d tMin = tonguePos.subtract(0.2, 0.2, 0.2);
                Vec3d tMax = tonguePos.add(0.2, 0.2, 0.2);
                Box box = new Box(tMin, tMax);
                for(Entity entity : world.getOtherEntities(null, box)) {
                    if(entity instanceof DreamspeckEntity dreamspeckEntity && !dreamspeckEntity.isSnared()) {
                        dreamspeckEntity.setSnare(this.getPos());
                        this.snaredEntity = entity;
                        this.entitySnared = true;
                        this.tongueExtended = false;
                        this.markForUpdate(serverWorld);
                    } else {
                        if(!(entity instanceof ItemEntity)) {
                            entity.damage(MirthdewEncoreDamageTypes.of(world, DREAMSNARE_TONGUE), 2);
                        }
                    }
                }
            }

            if(this.tongueDistance >= offsetLength) {
                this.tongueExtended = false;
                this.markForUpdate(serverWorld);
            }

            if(this.entitySnared) {
                if(this.snaredEntity != null) {
                    this.snaredEntity.setPosition(new Vec3d(pos.getX(), pos.getY() - this.snaredEntity.getHeight() * 0.5, pos.getZ()).add(this.tongueOffset));
                }
                if(this.tongueDistance <= 0) {
                    if(this.snaredEntity instanceof DreamspeckEntity dreamspeckEntity) {
                        dreamspeckEntity.setSnare(null);
                    }

                    if(!this.isHoldingItem() && this.snaredEntity != null) {
                        this.eatEntity(serverWorld, this.snaredEntity);
                    }

                    this.snaredEntity = null;
                    this.entitySnared = false;
                    this.markForUpdate(serverWorld);
                }
            }
        }
    }

    public void eatEntity(ServerWorld world, Entity entity) {
        entity.discard();
        this.setHeldItem(MirthdewEncoreItems.DREAMSEED.getDefaultStack());
        world.spawnParticles(ParticleTypes.SCULK_CHARGE_POP, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 100, 0.25, 0.25, 0.25, 0.02);
    }

    public void attack(ServerWorld world, @Nullable Entity entity) {
        if (entity == null) return;
        if (world.getDifficulty() == Difficulty.PEACEFUL && !(entity instanceof DreamspeckEntity)) return;

        BlockPos blockPos = this.getPos();
        world.emitGameEvent(GameEvent.SHRIEK, blockPos, GameEvent.Emitter.of(entity));

        Vec3d target = entity.getPos().add(0, entity.getHeight() * 0.5, 0);
        this.attack(world, target);
    }

    public void attack(ServerWorld world, Vec3d target) {
        BlockPos pos = this.getPos();
        Vec3d tongueBasePos = new Vec3d(pos.getX(), pos.getY(), pos.getZ()).add(this.tongueBaseOffset);

        BlockHitResult blockHitResult = world.raycast(new RaycastContext(
                tongueBasePos,
                target,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                ShapeContext.absent()
        ));

        Vec3d offset = blockHitResult.getPos().subtract(tongueBasePos);
        double offsetLength = offset.length();
        if(offsetLength > 9.0) {
            return;
        }
        BlockState state = this.getCachedState();
        Direction direction = state.contains(Properties.FACING) ? state.get(Properties.FACING) : Direction.UP;

        double dot = new Vec3d(direction.getUnitVector()).dotProduct(offset.normalize());
        if(dot < 0.6) {
            return;
        }

        this.tongueTargetOffset = offset.add(this.tongueBaseOffset);
        this.tongueOffset = this.tongueTargetOffset;
        this.tongueExtended = true;
        this.markForUpdate(world);
    }

    public boolean canAttack(Entity entity) {
        if(this.tongueDistance > 0) return false;
        if(this.isHoldingItem()) return false;

        if(entity.isSpectator()) {
            return false;
        } else if(entity instanceof PlayerEntity player && player.getAbilities().creativeMode) {
            return false;
        } else {
            return true;
        }
    }

    class VibrationCallback implements Vibrations.Callback {
        private static final int RANGE = 8;
        private final PositionSource positionSource = new BlockPositionSource(VericDreamsnareBlockEntity.this.pos);

        public VibrationCallback() {
        }

        @Override
        public int getRange() {
            return RANGE;
        }

        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public TagKey<GameEvent> getTag() {
            return GameEventTags.SHRIEKER_CAN_LISTEN;
        }

        @Override
        public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter) {
            Entity entity = emitter.sourceEntity();
            if(entity == null) return false;
            return VericDreamsnareBlockEntity.this.canAttack(entity);
        }

        @Override
        public void accept(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
            VericDreamsnareBlockEntity.this.attack(world, sourceEntity);
        }

        @Override
        public void onListen() {
            VericDreamsnareBlockEntity.this.markDirty();
        }

        @Override
        public boolean requiresTickingChunksAround() {
            return true;
        }
    }
}
