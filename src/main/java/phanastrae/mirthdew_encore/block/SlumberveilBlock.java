package phanastrae.mirthdew_encore.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Portal;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.block.entity.MirthdewEncoreBlockEntityTypes;
import phanastrae.mirthdew_encore.block.entity.SlumbersocketBlockEntity;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.component.type.LocationComponent;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.Optional;

public class SlumberveilBlock extends Block implements Portal {
    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
    public static final BooleanProperty SUPPORTING = BooleanProperty.of("supporting");
    public static final IntProperty DISTANCE = IntProperty.of("distance", 0, 15);
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 7.0, 16.0, 16.0, 9.0);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(7.0, 0.0, 0.0, 9.0, 16.0, 16.0);

    public SlumberveilBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(AXIS, Direction.Axis.X)
                .with(SUPPORTING, false)
                .with(DISTANCE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS, SUPPORTING, DISTANCE);
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return switch (rotation) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.get(AXIS)) {
                case Z -> state.with(AXIS, Direction.Axis.X);
                case X -> state.with(AXIS, Direction.Axis.Z);
                default -> state;
            };
            default -> state;
        };
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(AXIS)) {
            case Z -> Z_SHAPE;
            default -> X_SHAPE;
        };
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        Direction.Axis axis = state.get(AXIS);
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
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, 2 + world.getRandom().nextInt(3));
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        world.scheduleBlockTick(pos, this, 2 + world.getRandom().nextInt(3));
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean blockBroken = updateBlock(state, world, pos);
        if(!blockBroken) {
            spreadVeil(state, world, pos);
        }
    }

    public boolean updateBlock(BlockState state, ServerWorld world, BlockPos pos) {
        Direction.Axis axis = state.get(AXIS);
        boolean supporting = state.get(SUPPORTING);
        int distance = state.get(DISTANCE);

        BlockPos upPos = pos.up();
        if(!supporting) {
            // check for support above
            BlockState supportState = world.getBlockState(upPos);
            if(supportState.isOf(this)) {
                if(axis == supportState.get(AXIS)) {
                    boolean supporting2 = supportState.get(SUPPORTING);
                    int targetDistance = supporting2 ? 0 : supportState.get(DISTANCE) + 1;
                    if(distance == targetDistance) {
                        return false;
                    } else {
                        distance = targetDistance;
                        if(distance <= 15) {
                            world.setBlockState(pos, state.with(DISTANCE, distance));
                            return false;
                        }
                    }
                }
            }

            world.breakBlock(pos, false);
            return true;
        } else {
            BlockState supportState = world.getBlockState(upPos);
            // check for support above and distance values of neighbors
            if(supportState.isOf(MirthdewEncoreBlocks.SLUMBERSOCKET) && supportState.get(SlumbersocketBlock.DREAMING)) {
                if(distance != 0) {
                    world.setBlockState(pos, state.with(DISTANCE, 0));
                }
                return false;
            } else {
                if (Block.sideCoversSmallSquare(world, upPos, Direction.DOWN)) {
                    int minNeighborDistance = 15;
                    for (Direction direction : Direction.values()) {
                        Direction.Axis axis2 = direction.getAxis();
                        if (axis2.isVertical() || axis != axis2) continue;

                        BlockState neighborState = world.getBlockState(pos.offset(direction));
                        if (neighborState.isOf(this)) {
                            if(neighborState.get(SUPPORTING)) {
                                int neighborDistance = neighborState.get(DISTANCE);
                                if (neighborDistance < minNeighborDistance) {
                                    minNeighborDistance = neighborDistance;
                                }
                            }
                        }
                    }

                    if(distance == minNeighborDistance + 1) {
                        return false;
                    } else {
                        distance = minNeighborDistance + 1;
                        if(distance <= 15) {
                            world.setBlockState(pos, state.with(DISTANCE, distance));
                            return false;
                        }
                    }
                }

                world.breakBlock(pos, false);
                return true;
            }
        }
    }

    public void spreadVeil(BlockState state, ServerWorld world, BlockPos pos) {
        int distance = state.get(DISTANCE);
        Direction.Axis axis = state.get(AXIS);
        boolean supporting = state.get(SUPPORTING);

        // spread sideways
        if(supporting && distance <= 14) {
            for(Direction direction : Direction.values()) {
                Direction.Axis axis2 = direction.getAxis();
                if(axis2.isVertical() || axis != axis2) continue;

                BlockPos pos2 = pos.offset(direction);
                BlockState neighborState = world.getBlockState(pos2);
                if(!neighborState.isOf(this) && neighborState.isAir() && Block.sideCoversSmallSquare(world, pos2.offset(Direction.UP), Direction.DOWN)) {
                    world.setBlockState(pos2, state.with(DISTANCE, distance + 1));
                }
            }
        }

        // spread downwards
        if(supporting || distance <= 14) {
            BlockPos downPos = pos.down();
            BlockState downState = world.getBlockState(downPos);
            if (!downState.isOf(this) && downState.isAir()) {
                int targetDistance = supporting ? 0 : distance + 1;
                world.setBlockState(downPos, state.with(SUPPORTING, false).with(DISTANCE, targetDistance));
            }
        }
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity.canUsePortals(false)) {
            entity.tryUsePortal(this, pos);
        }
        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public int getPortalDelay(ServerWorld world, Entity entity) {
        return entity instanceof PlayerEntity playerEntity
                ? Math.max(
                1,
                world.getGameRules()
                        .getInt(playerEntity.getAbilities().invulnerable ? GameRules.PLAYERS_NETHER_PORTAL_CREATIVE_DELAY : GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY)
        )
                : 0;
    }

    @Nullable
    @Override
    public TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
        Optional<SlumbersocketBlockEntity> slumbersocketBlockEntityOptional = findVeilSocket(world, pos);
        if(slumbersocketBlockEntityOptional.isPresent()) {
            SlumbersocketBlockEntity slumbersocketBlockEntity = slumbersocketBlockEntityOptional.get();
            ItemStack itemStack = slumbersocketBlockEntity.getHeldItem();
            if(!itemStack.isEmpty() && itemStack.contains(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT)) {
                LocationComponent locationComponent = itemStack.get(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT);
                Vec3d targetPos = locationComponent.getPos();
                World targetWorld = locationComponent.getWorld(world.getServer());
                if(targetWorld != null) {
                    boolean validTarget = true;
                    DreamtwirlStageManager dreamtwirlStageManager = DreamtwirlStageManager.getDreamtwirlStageManager(targetWorld);
                    if(dreamtwirlStageManager != null) {
                        if(dreamtwirlStageManager.getDreamtwirlIfPresent(RegionPos.fromVec3d(targetPos)) == null) {
                            validTarget = false;
                        }
                    }

                    if(validTarget) {
                        if (targetWorld instanceof ServerWorld serverWorld) {
                            return new TeleportTarget(
                                    serverWorld,
                                    targetPos,
                                    entity.getVelocity(),
                                    entity.getYaw(),
                                    entity.getPitch(),
                                    TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)
                            );
                        }
                    }
                }
            }
        }

        return null;
    }

    public Optional<SlumbersocketBlockEntity> findVeilSocket(World world, BlockPos pos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        mutable.set(pos);

        BlockState state = world.getBlockState(pos);

        if(!state.contains(SUPPORTING) || !state.contains(DISTANCE)) return Optional.empty();
        int distance = state.get(DISTANCE);
        boolean supporting = state.get(SUPPORTING);

        if(!supporting) {
            // go to top
            for (int i = 0; i < distance + 1; i++) {
                mutable.set(mutable, 0, 1, 0);
                if (!world.getBlockState(mutable).isOf(this)) {
                    mutable.set(mutable, 0, -1, 0);
                    break;
                }
            }
        }

        // go to lowest distance veil block
        state = world.getBlockState(mutable);
        if(!state.contains(DISTANCE)) return Optional.empty();
        distance = state.get(DISTANCE);
        for(int j = 0; j < 15; j++) {
            for(Direction direction : Direction.values()) {
                mutable.set(mutable, direction);
                BlockState adjState = world.getBlockState(mutable);
                if(adjState.isOf(this) && adjState.get(SUPPORTING)) {
                    int adjDistance = adjState.get(DISTANCE);
                    if(adjDistance < distance) {
                        distance = adjDistance;
                        break;
                    }
                }
                mutable.set(mutable, direction.getOpposite());
            }
        }

        // check block above
        mutable.set(mutable, 0, 1, 0);
        BlockState socketState = world.getBlockState(mutable);
        if(socketState.isOf(MirthdewEncoreBlocks.SLUMBERSOCKET)) {
            if(socketState.get(SlumbersocketBlock.DREAMING)) {
                return world.getBlockEntity(mutable, MirthdewEncoreBlockEntityTypes.SLUMBERSOCKET);
            }
        }

        return Optional.empty();
    }
}
