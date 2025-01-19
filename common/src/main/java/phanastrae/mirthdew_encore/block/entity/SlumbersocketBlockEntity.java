package phanastrae.mirthdew_encore.block.entity;

import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import phanastrae.mirthdew_encore.block.DreamseedBlock;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.block.SlumbersocketBlock;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.component.type.LocationComponent;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.EndPlatformFeature;
import net.minecraft.world.phys.Vec3;

public class SlumbersocketBlockEntity extends BlockEntity {

    float targetYaw;
    float targetPitch;
    public float yaw;
    public float pitch;
    public float prevYaw;
    public float prevPitch;

    public long timer = 0;

    private final NonNullList<ItemStack> heldItem = NonNullList.withSize(1, ItemStack.EMPTY);

    public SlumbersocketBlockEntity(BlockPos pos, BlockState state) {
        super(MirthdewEncoreBlockEntityTypes.SLUMBERSOCKET, pos, state);
        this.setDefaultLookTarget(state);
        this.pitch = this.targetPitch;
        this.yaw = this.targetYaw;
        this.prevPitch = this.targetPitch;
        this.prevYaw = this.targetYaw;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        ContainerHelper.saveAllItems(nbt, this.heldItem, true, registryLookup);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        this.heldItem.clear();
        ContainerHelper.loadAllItems(nbt, this.heldItem, registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag nbtCompound = this.saveCustomOnly(registryLookup);

        return nbtCompound;
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

    public void setDefaultLookTarget(BlockState state) {
        Direction direction = state.hasProperty(HorizontalDirectionalBlock.FACING) ? state.getValue(HorizontalDirectionalBlock.FACING) : Direction.NORTH;
        this.targetYaw = direction.toYRot();
        this.targetPitch = 0;
    }

    public void markForUpdate(ServerLevel world) {
        world.getChunkSource().blockChanged(this.getBlockPos());
    }

    public static void tick(Level world, BlockPos pos, BlockState state, SlumbersocketBlockEntity blockEntity) {
        if(world.isClientSide()) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            Player playerEntity = world.getNearestPlayer(x, y, z, 16.0, false);
            if (playerEntity != null) {
                Vec3 offset = playerEntity.getEyePosition().subtract(x, y, z).normalize();
                blockEntity.targetYaw = (float)Math.toDegrees(Mth.atan2(offset.z, offset.x)) - 90;
                blockEntity.targetPitch = (float)Math.toDegrees(Mth.atan2(offset.y, offset.horizontalDistance()));
            } else {
                blockEntity.setDefaultLookTarget(state);
            }

            blockEntity.prevYaw = blockEntity.yaw;
            blockEntity.prevPitch = blockEntity.pitch;

            float turnSpeed = isDreaming(state) ? 0.015f : 0.07f;
            blockEntity.yaw = Mth.rotLerp(turnSpeed, blockEntity.yaw, blockEntity.targetYaw);
            blockEntity.pitch = Mth.lerp(turnSpeed, blockEntity.pitch, blockEntity.targetPitch);
        } else if(world instanceof ServerLevel serverWorld) {
            blockEntity.timer++;

            if (blockEntity.timer % 100 == 0) {
                if(state.hasProperty(SlumbersocketBlock.DREAMING)) {
                    boolean dreaming = state.getValue(SlumbersocketBlock.DREAMING);
                    ItemStack heldItem = blockEntity.getHeldItem();
                    if(!dreaming && (heldItem.is(Items.ENDER_EYE) || heldItem.is(MirthdewEncoreItems.SLUMBERING_EYE)) && !heldItem.has(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT)) {
                        if(world.getBlockState(pos.below()).isAir()) {
                            attemptEat(serverWorld, pos, state, blockEntity);
                        }
                    }
                }
            }
        }
    }

    public static void attemptEat(ServerLevel world, BlockPos pos, BlockState state, SlumbersocketBlockEntity blockEntity) {
        Optional<BlockPos> blockPosOptional = BlockPos.findClosestMatch(pos, 8, 8, (blockPos -> {
            BlockState checkState = world.getBlockState(blockPos);
            if(checkState.is(MirthdewEncoreBlocks.DREAMSEED)) {
                return checkState.getValue(DreamseedBlock.LIT);
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
        world.setBlockAndUpdate(seedBlockPos, Blocks.SOUL_FIRE.defaultBlockState());
        world.setBlockAndUpdate(pos, state.setValue(SlumbersocketBlock.DREAMING, true));

        ItemStack itemStack = blockEntity.getHeldItem();
        ItemStack newStack = MirthdewEncoreItems.SLUMBERING_EYE.getDefaultInstance();
        newStack.applyComponentsAndValidate(itemStack.getComponentsPatch());

        RandomSource random = world.getRandom();
        BlockPos targetPos = new BlockPos(
                regionPos.getCenterX() + random.nextInt(256) - 128,
                64,
                regionPos.getCenterZ() + random.nextInt(256) - 128
        );
        if(stage.getLevel() instanceof ServerLevel serverWorld) {
            EndPlatformFeature.createEndPlatform(serverWorld, targetPos, true);
        }

        newStack.set(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT, LocationComponent.fromPosAndWorld(targetPos.getBottomCenter(), stage.getLevel()));
        blockEntity.setHeldItem(newStack);

        Vec3 socketPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        world.playSound(null, socketPos.x, socketPos.y, socketPos.z, SoundEvents.ENDERMAN_SCREAM, SoundSource.BLOCKS, 0.3F, 0.3F);

        Vec3 seedOffset = new Vec3(seedBlockPos.getX() + 0.5, seedBlockPos.getY() + 0.5, seedBlockPos.getZ() + 0.5).subtract(socketPos);

        ParticleOptions particleEffect = new ItemParticleOption(ParticleTypes.ITEM, MirthdewEncoreItems.DREAMSEED.getDefaultInstance());

        if(world instanceof ServerLevel serverWorld) {
            for (int i = 0; i < 4 + 10 * seedOffset.length(); i++) {
                Vec3 particlePos = socketPos.add(seedOffset.scale(random.nextFloat()));

                serverWorld.sendParticles(ParticleTypes.ENCHANT,
                        particlePos.x,
                        particlePos.y,
                        particlePos.z,
                        6,
                        0.1, 0.1, 0.1, 0.05);

                serverWorld.sendParticles(ParticleTypes.WITCH,
                        particlePos.x,
                        particlePos.y,
                        particlePos.z,
                        3,
                        0.1, 0.1, 0.1, 0.05);

                serverWorld.sendParticles(particleEffect,
                        particlePos.x,
                        particlePos.y,
                        particlePos.z,
                        20,
                        0.1, 0.1, 0.1, 0.3);
            }
        }
    }

    public static boolean isDreaming(BlockState state) {
        return state.hasProperty(SlumbersocketBlock.DREAMING) ? state.getValue(SlumbersocketBlock.DREAMING) : false;
    }
}
