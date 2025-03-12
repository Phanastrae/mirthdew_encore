package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
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
import phanastrae.mirthdew_encore.component.type.LinkedAcheruneComponent;
import phanastrae.mirthdew_encore.component.type.LinkedDreamtwirlComponent;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.Acherune;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;
import phanastrae.mirthdew_encore.item.SlumberingEyeItem;
import phanastrae.mirthdew_encore.util.BlockPosDimensional;

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

    private boolean checkedAcherune = false;
    private @Nullable Acherune linkedAcherune;
    private boolean checkedDreaming = false;
    private boolean wasDreaming;

    public SlumbersocketBlockEntity(BlockPos pos, BlockState state) {
        super(MirthdewEncoreBlockEntityTypes.SLUMBERSOCKET, pos, state);
        this.setDefaultLookTarget(state);
        this.pitch = this.targetPitch;
        this.yaw = this.targetYaw;
        this.prevPitch = this.targetPitch;
        this.prevYaw = this.targetYaw;
        this.wasDreaming = state.hasProperty(SlumbersocketBlock.DREAMING) ? state.getValue(SlumbersocketBlock.DREAMING) : false;
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

        this.checkedAcherune = false;
        this.checkedDreaming = false;
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

    public void markForUpdate(ServerLevel level) {
        level.getChunkSource().blockChanged(this.getBlockPos());
    }

    public static void tickClient(Level level, BlockPos pos, BlockState state, SlumbersocketBlockEntity blockEntity) {
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

        ItemStack heldItem = blockEntity.getHeldItem();
        float turnSpeed = 0.0f;
        if(heldItem.is(Items.ENDER_EYE)) {
            turnSpeed = 0.07f;
        } else if(heldItem.is(MirthdewEncoreItems.SLEEPY_EYE)) {
            turnSpeed = 0.03f;
        } else if(heldItem.is(MirthdewEncoreItems.SLUMBERING_EYE)) {
            turnSpeed = 0.015f;
        }

        blockEntity.yaw = Mth.rotLerp(turnSpeed, blockEntity.yaw, blockEntity.targetYaw);
        blockEntity.pitch = Mth.lerp(turnSpeed, blockEntity.pitch, blockEntity.targetPitch);
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, SlumbersocketBlockEntity blockEntity) {
        if(!(level instanceof ServerLevel serverLevel)) return;

        blockEntity.timer++;

        if(!blockEntity.checkedAcherune) {
            blockEntity.checkAcherune(level, pos);
        }
        if(!blockEntity.checkedDreaming || (!blockEntity.wasDreaming && blockEntity.timer % 100 == 0)) {
            blockEntity.checkDreaming(level, pos, state);
        }

        if(blockEntity.timer % 100 == 0) {
            if(state.hasProperty(SlumbersocketBlock.DREAMING)) {
                ItemStack heldItem = blockEntity.getHeldItem();
                if(!blockEntity.wasDreaming) {
                    if((heldItem.is(Items.ENDER_EYE))) {
                        if(level.getBlockState(pos.below()).isAir()) {
                            attemptEat(serverLevel, pos, state, blockEntity);
                        }
                    }
                }

                if(heldItem.has(MirthdewEncoreDataComponentTypes.LINKED_DREAMTWIRL) && !heldItem.has(MirthdewEncoreDataComponentTypes.LINKED_ACHERUNE)) {
                    attemptLinkToDreamtwirlSpawn(serverLevel, pos, state, blockEntity, heldItem);
                }
            }
        }
    }

    public void checkAcherune(Level level, BlockPos pos) {
        ItemStack eye = this.getHeldItem();
        if(eye.has(MirthdewEncoreDataComponentTypes.LINKED_ACHERUNE)) {
            LinkedAcheruneComponent lac = eye.get(MirthdewEncoreDataComponentTypes.LINKED_ACHERUNE);
            this.linkedAcherune = lac.getAcherune(level.getServer());
        } else {
            this.linkedAcherune = null;
        }
        this.checkedAcherune = true;
    }

    public void checkDreaming(Level level, BlockPos pos, BlockState state) {
        boolean dreaming = this.isDreaming(level, pos);

        if(!dreaming) {
            this.tryBindToAcherune(level, pos);
            dreaming = this.isDreaming(level, pos);
        }

        if (dreaming != this.wasDreaming && state.hasProperty(SlumbersocketBlock.DREAMING)) {
            this.wasDreaming = dreaming;
            level.setBlock(pos, state.setValue(SlumbersocketBlock.DREAMING, dreaming), 3);
        }

        this.checkedDreaming = true;
    }

    public void tryBindToAcherune(Level level, BlockPos pos) {
        if(this.linkedAcherune != null && level instanceof ServerLevel serverLevel) {
            ItemStack eye = this.getHeldItem();
            if(eye.is(MirthdewEncoreItems.SLUMBERING_EYE) && eye.has(MirthdewEncoreDataComponentTypes.LINKED_ACHERUNE)) {
                LinkedAcheruneComponent lac = eye.get(MirthdewEncoreDataComponentTypes.LINKED_ACHERUNE);

                Level acLevel = lac.getLevel(serverLevel.getServer());
                if(acLevel != null) {
                    DreamtwirlStage stage = DreamtwirlStageManager.getStage(acLevel, lac.regionId());
                    if (stage != null) {
                        if (acLevel instanceof ServerLevel acServerLevel) {
                            this.linkedAcherune.validateLinkedPos(acServerLevel.getServer(), stage.getStageAcherunes());
                        }

                        if (this.linkedAcherune.getLinkedPos() == null) {
                            this.linkedAcherune.setLinkedPos(BlockPosDimensional.fromPosAndLevel(pos, level));
                            stage.getStageAcherunes().setDirty();
                        }
                    }
                }
            }
        }
    }

    public BlockPos getTargetPos() {
        BlockPos socketPos = this.getBlockPos();
        Level level = this.getLevel();
        if(level == null) return socketPos;

        // search for runes near socket
        Optional<BlockPos> blockPosOptional = BlockPos.findClosestMatch(socketPos, 24, 24, (blockPos -> {
            BlockState checkState = level.getBlockState(blockPos);
            return checkState.is(MirthdewEncoreBlocks.WAKESIDE_RUNE);
        }));
        if(blockPosOptional.isPresent()) {
            return blockPosOptional.get().above();
        }

        BlockState state = this.getBlockState();
        if(state.hasProperty(SlumbersocketBlock.FACING)) {
            Direction direction = state.getValue(SlumbersocketBlock.FACING);

            // move forwards out of the eye
            BlockPos targetPos = socketPos;
            for(int i = 0; i < 5; i++) {
                BlockPos adjPos = targetPos.relative(direction);
                BlockState adjState = level.getBlockState(adjPos);

                if(adjState.getCollisionShape(level, adjPos).isEmpty()) {
                    targetPos = adjPos;
                }
            }

            // move downwards to ground
            for(int i = 0; i < 48; i++) {
                BlockPos downPos = targetPos.below();
                BlockState downState = level.getBlockState(downPos);

                if(downState.getCollisionShape(level, downPos).isEmpty()) {
                    targetPos = downPos;
                }
            }

            // search for runes near target pos
            Optional<BlockPos> blockPosOptional2 = BlockPos.findClosestMatch(targetPos, 24, 24, (blockPos -> {
                BlockState checkState = level.getBlockState(blockPos);
                return checkState.is(MirthdewEncoreBlocks.WAKESIDE_RUNE);
            }));
            if(blockPosOptional2.isPresent()) {
                return blockPosOptional2.get().above();
            }

            return targetPos;
        } else {
            return socketPos;
        }
    }

    public boolean isDreaming(Level level, BlockPos pos) {
        if(this.canDream()) {
            BlockState below = level.getBlockState(pos.below());
            return below.is(MirthdewEncoreBlocks.SLUMBERVEIL) || below.isAir();
        } else {
            return false;
        }
    }

    public boolean canDream() {
        ItemStack eye = this.getHeldItem();
        if(!eye.is(MirthdewEncoreItems.SLUMBERING_EYE)) {
            return false;
        }

        if(eye.has(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT)) {
            return true;
        }

        if(eye.has(MirthdewEncoreDataComponentTypes.LINKED_ACHERUNE)) {
            if(this.linkedAcherune != null && this.linkedAcherune.isLinkedTo(this.getBlockPos(), this.getLevel())) {
                return true;
            }
        }

        return false;
    }

    public void damageEye() {
        this.setHeldItem(SlumberingEyeItem.damageEye(this.getHeldItem()));
        if(this.getLevel() instanceof ServerLevel serverLevel) {
            this.markForUpdate(serverLevel);
        }
    }

    public boolean canRepairEye() {
        ItemStack heldItem = this.getHeldItem();
        return heldItem.is(MirthdewEncoreItems.SLEEPY_EYE) || (heldItem.is(MirthdewEncoreItems.SLUMBERING_EYE) && heldItem.getDamageValue() != 0);
    }

    public boolean repairEye() {
        if(canRepairEye()) {
            this.setHeldItem(SlumberingEyeItem.repairEye(this.getHeldItem()));

            if(this.getLevel() instanceof ServerLevel serverLevel) {
                this.markForUpdate(serverLevel);
            }
            return true;
        } else {
            return false;
        }
    }

    public static void attemptLinkToDreamtwirlSpawn(ServerLevel level, BlockPos pos, BlockState state, SlumbersocketBlockEntity blockEntity, ItemStack heldItem) {
        if(!heldItem.has(MirthdewEncoreDataComponentTypes.LINKED_DREAMTWIRL)) return;
        LinkedDreamtwirlComponent linkedDreamtwirl = heldItem.get(MirthdewEncoreDataComponentTypes.LINKED_DREAMTWIRL);

        DreamtwirlStage stage = linkedDreamtwirl.getStage(level.getServer());
        if(stage == null) return;

        if(stage.isReady()) {
            Acherune acherune = stage.getEntranceAcherune(level.getRandom());
            if(acherune != null) {
                ItemStack newStack = heldItem.copy();
                newStack.remove(MirthdewEncoreDataComponentTypes.LINKED_DREAMTWIRL);
                newStack.set(MirthdewEncoreDataComponentTypes.LINKED_ACHERUNE, LinkedAcheruneComponent.fromAcheruneAndStage(stage, acherune));

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

        // eat seed
        level.setBlockAndUpdate(seedBlockPos, Blocks.SOUL_FIRE.defaultBlockState());

        // convert eye to slumbering eye
        ItemStack itemStack = blockEntity.getHeldItem();
        ItemStack newStack = MirthdewEncoreItems.SLUMBERING_EYE.getDefaultInstance();
        newStack.applyComponentsAndValidate(itemStack.getComponentsPatch());
        newStack.set(MirthdewEncoreDataComponentTypes.LINKED_DREAMTWIRL, LinkedDreamtwirlComponent.fromStage(stage));
        newStack.set(DataComponents.DAMAGE, 2);
        blockEntity.setHeldItem(newStack);
        blockEntity.markForUpdate(level);

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
}
