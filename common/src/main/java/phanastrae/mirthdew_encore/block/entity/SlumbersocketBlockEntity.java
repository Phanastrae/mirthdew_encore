package phanastrae.mirthdew_encore.block.entity;

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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import phanastrae.mirthdew_encore.block.DreamseedBlock;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.block.SlumbersocketBlock;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.component.type.LinkedDreamtwirlComponent;
import phanastrae.mirthdew_encore.component.type.LocationComponent;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
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

    public static void tick(Level level, BlockPos pos, BlockState state, SlumbersocketBlockEntity blockEntity) {
        if(level.isClientSide()) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            Player playerEntity = level.getNearestPlayer(x, y, z, 16.0, false);
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
        } else if(level instanceof ServerLevel serverLevel) {
            blockEntity.timer++;

            if (blockEntity.timer % 100 == 0) {
                if(state.hasProperty(SlumbersocketBlock.DREAMING)) {
                    boolean dreaming = state.getValue(SlumbersocketBlock.DREAMING);
                    ItemStack heldItem = blockEntity.getHeldItem();
                    if(!dreaming) {
                        if((heldItem.is(Items.ENDER_EYE) || heldItem.is(MirthdewEncoreItems.SLUMBERING_EYE)) && !heldItem.has(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT) && !heldItem.has(MirthdewEncoreDataComponentTypes.LINKED_DREAMTWIRL)) {
                            if(level.getBlockState(pos.below()).isAir()) {
                                attemptEat(serverLevel, pos, state, blockEntity);
                            }
                        }
                    } else {
                        if(heldItem.is(MirthdewEncoreItems.SLUMBERING_EYE) && heldItem.has(MirthdewEncoreDataComponentTypes.LINKED_DREAMTWIRL)) {
                            attemptLinkToDreamtwirlSpawn(serverLevel, pos, state, blockEntity, heldItem);
                        }
                    }
                }
            }
        }
    }

    public static void attemptLinkToDreamtwirlSpawn(ServerLevel level, BlockPos pos, BlockState state, SlumbersocketBlockEntity blockEntity, ItemStack heldItem) {
        if(!heldItem.has(MirthdewEncoreDataComponentTypes.LINKED_DREAMTWIRL)) return;
        LinkedDreamtwirlComponent linkedDreamtwirl = heldItem.get(MirthdewEncoreDataComponentTypes.LINKED_DREAMTWIRL);

        DreamtwirlStage stage = linkedDreamtwirl.getStage(level.getServer());
        if(stage == null) return;

        if(stage.isReady()) {
            Vec3 entrance = stage.getEntrancePos(level.getRandom());
            if(entrance != null) {
                ItemStack newStack = heldItem.copy();
                newStack.remove(MirthdewEncoreDataComponentTypes.LINKED_DREAMTWIRL);
                newStack.set(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT, LocationComponent.fromPosAndLevel(entrance, stage.getLevel()));
                blockEntity.setHeldItem(newStack);
            }
        }
    }

    public static void attemptEat(ServerLevel level, BlockPos socketPos, BlockState socketState, SlumbersocketBlockEntity blockEntity) {
        Optional<BlockPos> blockPosOptional = BlockPos.findClosestMatch(socketPos, 8, 8, (blockPos -> {
            BlockState checkState = level.getBlockState(blockPos);
            if(checkState.is(MirthdewEncoreBlocks.DREAMSEED)) {
                return checkState.getValue(DreamseedBlock.LIT);
            } else {
                return false;
            }
        }));
        if(blockPosOptional.isEmpty()) return;
        BlockPos seedBlockPos = blockPosOptional.get();

        eatSeed(level, socketPos, socketState, blockEntity, seedBlockPos);
    }

    public static void eatSeed(ServerLevel level, BlockPos socketPos, BlockState socketState, SlumbersocketBlockEntity blockEntity, BlockPos seedBlockPos) {
        Optional<DreamtwirlStage> stageOptional = createNewStage(level);
        if(stageOptional.isEmpty()) return;
        DreamtwirlStage stage = stageOptional.get();
        stage.generate(level.random.nextLong(), level);

        RandomSource random = level.getRandom();
        RegionPos regionPos = stage.getRegionPos();

        // spawn platform
        /*
        BlockPos targetPos = new BlockPos(
                regionPos.getCenterX() + random.nextInt(256) - 128,
                64,
                regionPos.getCenterZ() + random.nextInt(256) - 128
        );
        if(stage.getLevel() instanceof ServerLevel serverLevel) {
            EndPlatformFeature.createEndPlatform(serverLevel, targetPos, true);
        }
        */

        // eat seed
        level.setBlockAndUpdate(seedBlockPos, Blocks.SOUL_FIRE.defaultBlockState());

        // convert eye to slumbering eye
        ItemStack itemStack = blockEntity.getHeldItem();
        ItemStack newStack = MirthdewEncoreItems.SLUMBERING_EYE.getDefaultInstance();
        newStack.applyComponentsAndValidate(itemStack.getComponentsPatch());
        //newStack.set(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT, LocationComponent.fromPosAndLevel(targetPos.getBottomCenter(), stage.getLevel()));
        newStack.set(MirthdewEncoreDataComponentTypes.LINKED_DREAMTWIRL, LinkedDreamtwirlComponent.fromStage(stage));
        blockEntity.setHeldItem(newStack);

        level.setBlockAndUpdate(socketPos, socketState.setValue(SlumbersocketBlock.DREAMING, true));

        // particle and sounds
        Vec3 socketPosV3 = socketPos.getCenter();
        level.playSound(null, socketPosV3.x, socketPosV3.y, socketPosV3.z, SoundEvents.ENDERMAN_SCREAM, SoundSource.BLOCKS, 0.3F, 0.3F);

        Vec3 seedOffset = seedBlockPos.getCenter().subtract(socketPosV3);
        ParticleOptions seedParticle = new ItemParticleOption(ParticleTypes.ITEM, MirthdewEncoreItems.DREAMSEED.getDefaultInstance());

        for (int i = 0; i < 4 + 10 * seedOffset.length(); i++) {
            Vec3 particlePos = socketPosV3.add(seedOffset.scale(random.nextFloat()));

            level.sendParticles(ParticleTypes.ENCHANT,
                    particlePos.x,
                    particlePos.y,
                    particlePos.z,
                    6,
                    0.1, 0.1, 0.1, 0.05);

            level.sendParticles(ParticleTypes.WITCH,
                    particlePos.x,
                    particlePos.y,
                    particlePos.z,
                    3,
                    0.1, 0.1, 0.1, 0.05);

            level.sendParticles(seedParticle,
                    particlePos.x,
                    particlePos.y,
                    particlePos.z,
                    20,
                    0.1, 0.1, 0.1, 0.3);
        }
    }

    public static Optional<DreamtwirlStage> createNewStage(ServerLevel level) {
        DreamtwirlStageManager dreamtwirlStageManager = DreamtwirlStageManager.getMainDreamtwirlStageManager(level.getServer());
        if(dreamtwirlStageManager == null) {
            return Optional.empty();
        }

        return dreamtwirlStageManager.createNewStage();
    }

    public static boolean isDreaming(BlockState state) {
        return state.hasProperty(SlumbersocketBlock.DREAMING) ? state.getValue(SlumbersocketBlock.DREAMING) : false;
    }
}
