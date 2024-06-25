package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import phanastrae.mirthdew_encore.block.DreamseedBlock;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.block.SlumbersocketBlock;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.component.type.LocationComponent;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.Optional;

public class SlumbersocketBlockEntity extends BlockEntity {

    float targetYaw;
    float targetPitch;
    public float yaw;
    public float pitch;
    public float prevYaw;
    public float prevPitch;

    public long timer = 0;

    private final DefaultedList<ItemStack> heldItem = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public SlumbersocketBlockEntity(BlockPos pos, BlockState state) {
        super(MirthdewEncoreBlockEntityTypes.SLUMBERSOCKET, pos, state);
        this.setDefaultLookTarget(state);
        this.pitch = this.targetPitch;
        this.yaw = this.targetYaw;
        this.prevPitch = this.targetPitch;
        this.prevYaw = this.targetYaw;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, this.heldItem, true, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        this.heldItem.clear();
        Inventories.readNbt(nbt, this.heldItem, registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbtCompound = this.createComponentlessNbt(registryLookup);

        return nbtCompound;
    }

    public void setHeldItem(ItemStack itemStack) {
        this.heldItem.set(0, itemStack);
        this.markDirty();
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

    public void setDefaultLookTarget(BlockState state) {
        Direction direction = state.contains(HorizontalFacingBlock.FACING) ? state.get(HorizontalFacingBlock.FACING) : Direction.NORTH;
        this.targetYaw = direction.asRotation();
        this.targetPitch = 0;
    }

    public void markForUpdate(ServerWorld world) {
        world.getChunkManager().markForUpdate(this.getPos());
    }

    public static void tick(World world, BlockPos pos, BlockState state, SlumbersocketBlockEntity blockEntity) {
        if(world.isClient()) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            PlayerEntity playerEntity = world.getClosestPlayer(x, y, z, 16.0, false);
            if (playerEntity != null) {
                Vec3d offset = playerEntity.getEyePos().subtract(x, y, z).normalize();
                blockEntity.targetYaw = (float)Math.toDegrees(MathHelper.atan2(offset.z, offset.x)) - 90;
                blockEntity.targetPitch = (float)Math.toDegrees(MathHelper.atan2(offset.y, offset.horizontalLength()));
            } else {
                blockEntity.setDefaultLookTarget(state);
            }

            blockEntity.prevYaw = blockEntity.yaw;
            blockEntity.prevPitch = blockEntity.pitch;

            float turnSpeed = isDreaming(state) ? 0.015f : 0.07f;
            blockEntity.yaw = MathHelper.lerpAngleDegrees(turnSpeed, blockEntity.yaw, blockEntity.targetYaw);
            blockEntity.pitch = MathHelper.lerp(turnSpeed, blockEntity.pitch, blockEntity.targetPitch);
        } else if(world instanceof ServerWorld serverWorld) {
            blockEntity.timer++;

            if (blockEntity.timer % 100 == 0) {
                if(state.contains(SlumbersocketBlock.DREAMING)) {
                    boolean dreaming = state.get(SlumbersocketBlock.DREAMING);
                    ItemStack heldItem = blockEntity.getHeldItem();
                    if(!dreaming && (heldItem.isOf(Items.ENDER_EYE) || heldItem.isOf(MirthdewEncoreItems.SLUMBERING_EYE)) && !heldItem.contains(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT)) {
                        if(world.getBlockState(pos.down()).isAir()) {
                            attemptEat(serverWorld, pos, state, blockEntity);
                        }
                    }
                }
            }
        }
    }

    public static void attemptEat(ServerWorld world, BlockPos pos, BlockState state, SlumbersocketBlockEntity blockEntity) {
        Optional<BlockPos> blockPosOptional = BlockPos.findClosest(pos, 8, 8, (blockPos -> {
            BlockState checkState = world.getBlockState(blockPos);
            if(checkState.isOf(MirthdewEncoreBlocks.DREAMSEED)) {
                return checkState.get(DreamseedBlock.LIT);
            } else {
                return false;
            }
        }));
        if(blockPosOptional.isEmpty()) return;

        DreamtwirlStageManager dreamtwirlStageManager = DreamtwirlStageManager.getMainDreamtwirlStageManager(world.getServer());
        if(dreamtwirlStageManager == null) return;

        Optional<DreamtwirlStage> stageOptional = dreamtwirlStageManager.createNewStage();
        if(stageOptional.isEmpty()) return;

        DreamtwirlStage stage = stageOptional.get();
        RegionPos regionPos = stage.getRegionPos();

        BlockPos seedBlockPos = blockPosOptional.get();
        world.setBlockState(seedBlockPos, Blocks.SOUL_FIRE.getDefaultState());
        world.setBlockState(pos, state.with(SlumbersocketBlock.DREAMING, true));

        ItemStack itemStack = blockEntity.getHeldItem();
        ItemStack newStack = MirthdewEncoreItems.SLUMBERING_EYE.getDefaultStack();
        newStack.applyChanges(itemStack.getComponentChanges());
        Random random = world.getRandom();
        Vec3d targetPos = new Vec3d(regionPos.getCenterX() + 256 * random.nextDouble() - 128, 64, regionPos.getCenterZ() + 256 * random.nextDouble() - 128);
        newStack.set(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT, LocationComponent.fromPosAndWorld(targetPos, stage.getWorld()));
        blockEntity.setHeldItem(newStack);

        Vec3d socketPos = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        world.playSound(null, socketPos.x, socketPos.y, socketPos.z, SoundEvents.ENTITY_ENDERMAN_SCREAM, SoundCategory.BLOCKS, 0.3F, 0.3F);

        Vec3d seedOffset = new Vec3d(seedBlockPos.getX() + 0.5, seedBlockPos.getY() + 0.5, seedBlockPos.getZ() + 0.5).subtract(socketPos);

        ParticleEffect particleEffect = new ItemStackParticleEffect(ParticleTypes.ITEM, MirthdewEncoreItems.DREAMSEED.getDefaultStack());

        if(world instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 4 + 10 * seedOffset.length(); i++) {
                Vec3d particlePos = socketPos.add(seedOffset.multiply(random.nextFloat()));

                serverWorld.spawnParticles(ParticleTypes.ENCHANT,
                        particlePos.x,
                        particlePos.y,
                        particlePos.z,
                        6,
                        0.1, 0.1, 0.1, 0.05);

                serverWorld.spawnParticles(ParticleTypes.WITCH,
                        particlePos.x,
                        particlePos.y,
                        particlePos.z,
                        3,
                        0.1, 0.1, 0.1, 0.05);

                serverWorld.spawnParticles(particleEffect,
                        particlePos.x,
                        particlePos.y,
                        particlePos.z,
                        20,
                        0.1, 0.1, 0.1, 0.3);
            }
        }
    }

    public static boolean isDreaming(BlockState state) {
        return state.contains(SlumbersocketBlock.DREAMING) ? state.get(SlumbersocketBlock.DREAMING) : false;
    }
}
