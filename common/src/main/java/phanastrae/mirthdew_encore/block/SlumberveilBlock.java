package phanastrae.mirthdew_encore.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.block.entity.MirthdewEncoreBlockEntityTypes;
import phanastrae.mirthdew_encore.block.entity.SlumbersocketBlockEntity;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.component.type.LinkedAcheruneComponent;
import phanastrae.mirthdew_encore.component.type.LocationComponent;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.Acherune;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.Optional;

public class SlumberveilBlock extends Block implements Portal {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty SUPPORTING = BooleanProperty.create("supporting");
    public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 0, 15);
    protected static final VoxelShape X_SHAPE = Block.box(0.0, 0.0, 7.0, 16.0, 16.0, 9.0);
    protected static final VoxelShape Z_SHAPE = Block.box(7.0, 0.0, 0.0, 9.0, 16.0, 16.0);

    public SlumberveilBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(AXIS, Direction.Axis.X)
                .setValue(SUPPORTING, false)
                .setValue(DISTANCE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, SUPPORTING, DISTANCE);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return switch (rotation) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
                case Z -> state.setValue(AXIS, Direction.Axis.X);
                case X -> state.setValue(AXIS, Direction.Axis.Z);
                default -> state;
            };
            default -> state;
        };
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(AXIS)) {
            case Z -> Z_SHAPE;
            default -> X_SHAPE;
        };
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        Direction.Axis axis = state.getValue(AXIS);
        for (int i = 0; i < 5; i++) {
            double x = (double)pos.getX() + random.nextDouble();
            double y = (double)pos.getY() + random.nextDouble();
            double z = (double)pos.getZ() + random.nextDouble();
            double ox = ((double)random.nextFloat() - 0.5) * 0.5;
            double oy = ((double)random.nextFloat() - 0.5) * 0.5;
            double oz = ((double)random.nextFloat() - 0.5) * 0.5;
            int r = random.nextInt(2) * 2 - 1;
            if (axis == Direction.Axis.X) {
                x = (double)pos.getX() + 0.5 + 0.25 * (double)r;
                ox = random.nextFloat() * 2.0F * (float)r;
            } else {
                z = (double)pos.getZ() + 0.5 + 0.25 * (double)r;
                oz = random.nextFloat() * 2.0F * (float)r;
            }

            world.addParticle(i == 0 ? ParticleTypes.WITCH : ParticleTypes.ENCHANT,
                    x, y, z,
                    ox, oy, oz);
        }
    }

    @Override
    protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleTick(pos, this, 2 + world.getRandom().nextInt(3));
    }

    @Override
    protected BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos
    ) {
        world.scheduleTick(pos, this, 2 + world.getRandom().nextInt(3));
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        boolean blockBroken = updateBlock(state, world, pos);
        if(!blockBroken) {
            spreadVeil(state, world, pos);
        }
    }

    public boolean updateBlock(BlockState state, ServerLevel world, BlockPos pos) {
        Direction.Axis axis = state.getValue(AXIS);
        boolean supporting = state.getValue(SUPPORTING);
        int distance = state.getValue(DISTANCE);

        BlockPos upPos = pos.above();
        if(!supporting) {
            // check for support above
            BlockState supportState = world.getBlockState(upPos);
            if(supportState.is(this)) {
                if(axis == supportState.getValue(AXIS)) {
                    boolean supporting2 = supportState.getValue(SUPPORTING);
                    int targetDistance = supporting2 ? 0 : supportState.getValue(DISTANCE) + 1;
                    if(distance == targetDistance) {
                        return false;
                    } else {
                        distance = targetDistance;
                        if(distance <= 15) {
                            world.setBlockAndUpdate(pos, state.setValue(DISTANCE, distance));
                            return false;
                        }
                    }
                }
            }

            world.destroyBlock(pos, false);
            return true;
        } else {
            BlockState supportState = world.getBlockState(upPos);
            // check for support above and distance values of neighbors
            if(supportState.is(MirthdewEncoreBlocks.SLUMBERSOCKET) && supportState.getValue(SlumbersocketBlock.DREAMING)) {
                if(distance != 0) {
                    world.setBlockAndUpdate(pos, state.setValue(DISTANCE, 0));
                }
                return false;
            } else {
                if (Block.canSupportCenter(world, upPos, Direction.DOWN)) {
                    int minNeighborDistance = 15;
                    for (Direction direction : Direction.values()) {
                        Direction.Axis axis2 = direction.getAxis();
                        if (axis2.isVertical() || axis != axis2) continue;

                        BlockState neighborState = world.getBlockState(pos.relative(direction));
                        if (neighborState.is(this)) {
                            if(neighborState.getValue(SUPPORTING)) {
                                int neighborDistance = neighborState.getValue(DISTANCE);
                                if (neighborDistance < minNeighborDistance) {
                                    minNeighborDistance = neighborDistance;
                                }
                            }
                        }
                    }

                    if(distance == minNeighborDistance + 1) {
                        return false;
                    }
                }

                world.destroyBlock(pos, false);
                return true;
            }
        }
    }

    public void spreadVeil(BlockState state, ServerLevel world, BlockPos pos) {
        int distance = state.getValue(DISTANCE);
        Direction.Axis axis = state.getValue(AXIS);
        boolean supporting = state.getValue(SUPPORTING);

        // spread sideways
        if(supporting && distance <= 14) {
            for(Direction direction : Direction.values()) {
                Direction.Axis axis2 = direction.getAxis();
                if(axis2.isVertical() || axis != axis2) continue;

                BlockPos pos2 = pos.relative(direction);
                BlockState neighborState = world.getBlockState(pos2);
                if(!neighborState.is(this) && neighborState.isAir() && Block.canSupportCenter(world, pos2.relative(Direction.UP), Direction.DOWN)) {
                    world.setBlockAndUpdate(pos2, state.setValue(DISTANCE, distance + 1));
                }
            }
        }

        // spread downwards
        if(supporting || distance <= 14) {
            BlockPos downPos = pos.below();
            BlockState downState = world.getBlockState(downPos);
            if (!downState.is(this) && downState.isAir()) {
                int targetDistance = supporting ? 0 : distance + 1;
                world.setBlockAndUpdate(downPos, state.setValue(SUPPORTING, false).setValue(DISTANCE, targetDistance));
            }
        }
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (entity.canUsePortal(false)) {
            entity.setAsInsidePortal(this, pos);
        }
        super.entityInside(state, world, pos, entity);
    }

    @Override
    public Transition getLocalTransition() {
        return Transition.CONFUSION;
    }

    @Override
    public int getPortalTransitionTime(ServerLevel world, Entity entity) {
        return entity instanceof Player playerEntity
                ? Math.max(
                1,
                world.getGameRules()
                        .getInt(playerEntity.getAbilities().invulnerable ? GameRules.RULE_PLAYERS_NETHER_PORTAL_CREATIVE_DELAY : GameRules.RULE_PLAYERS_NETHER_PORTAL_DEFAULT_DELAY)
        )
                : 0;
    }

    @Nullable
    @Override
    public DimensionTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
        Optional<SlumbersocketBlockEntity> slumbersocketBlockEntityOptional = findVeilSocket(level, pos);
        if(slumbersocketBlockEntityOptional.isPresent()) {
            SlumbersocketBlockEntity slumbersocketBlockEntity = slumbersocketBlockEntityOptional.get();
            ItemStack itemStack = slumbersocketBlockEntity.getHeldItem();
            if(!itemStack.isEmpty()) {
                Vec3 targetPos = null;
                Level targetWorld = null;
                if(itemStack.has(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT)) {
                    LocationComponent locationComponent = itemStack.get(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT);

                    targetPos = locationComponent.getPos();
                    targetWorld = locationComponent.getLevel(level.getServer());
                } else if(itemStack.has(MirthdewEncoreDataComponentTypes.LINKED_ACHERUNE)) {
                    LinkedAcheruneComponent linkedAcherune = itemStack.get(MirthdewEncoreDataComponentTypes.LINKED_ACHERUNE);

                    Acherune acherune = linkedAcherune.getAcherune(level.getServer());
                    if(acherune != null) {
                        targetPos = acherune.getPos().above().getBottomCenter();
                        targetWorld = linkedAcherune.getLevel(level.getServer());
                    }
                }

                if(targetPos != null && targetWorld != null) {
                    boolean validTarget = true;
                    DreamtwirlStageManager dreamtwirlStageManager = DreamtwirlStageManager.getDreamtwirlStageManager(targetWorld);
                    if (dreamtwirlStageManager != null) {
                        if (dreamtwirlStageManager.getDreamtwirlIfPresent(RegionPos.fromVec3d(targetPos)) == null) {
                            validTarget = false;
                        }
                    }

                    if (validTarget) {
                        if (targetWorld instanceof ServerLevel serverWorld) {
                            return new DimensionTransition(
                                    serverWorld,
                                    targetPos,
                                    entity.getDeltaMovement(),
                                    entity.getYRot(),
                                    entity.getXRot(),
                                    DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET)
                            );
                        }
                    }
                }
            }
        }

        return null;
    }

    public Optional<SlumbersocketBlockEntity> findVeilSocket(Level world, BlockPos pos) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        mutable.set(pos);

        BlockState state = world.getBlockState(pos);

        if(!state.hasProperty(SUPPORTING) || !state.hasProperty(DISTANCE)) return Optional.empty();
        int distance = state.getValue(DISTANCE);
        boolean supporting = state.getValue(SUPPORTING);

        if(!supporting) {
            // go to top
            for (int i = 0; i < distance + 1; i++) {
                mutable.setWithOffset(mutable, 0, 1, 0);
                if (!world.getBlockState(mutable).is(this)) {
                    mutable.setWithOffset(mutable, 0, -1, 0);
                    break;
                }
            }
        }

        // go to lowest distance veil block
        state = world.getBlockState(mutable);
        if(!state.hasProperty(DISTANCE)) return Optional.empty();
        distance = state.getValue(DISTANCE);
        for(int j = 0; j < 15; j++) {
            for(Direction direction : Direction.values()) {
                mutable.setWithOffset(mutable, direction);
                BlockState adjState = world.getBlockState(mutable);
                if(adjState.is(this) && adjState.getValue(SUPPORTING)) {
                    int adjDistance = adjState.getValue(DISTANCE);
                    if(adjDistance < distance) {
                        distance = adjDistance;
                        break;
                    }
                }
                mutable.setWithOffset(mutable, direction.getOpposite());
            }
        }

        // check block above
        mutable.setWithOffset(mutable, 0, 1, 0);
        BlockState socketState = world.getBlockState(mutable);
        if(socketState.is(MirthdewEncoreBlocks.SLUMBERSOCKET)) {
            if(socketState.getValue(SlumbersocketBlock.DREAMING)) {
                return world.getBlockEntity(mutable, MirthdewEncoreBlockEntityTypes.SLUMBERSOCKET);
            }
        }

        return Optional.empty();
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return adjacentState.is(this) || super.skipRendering(state, adjacentState, direction);
    }
}
