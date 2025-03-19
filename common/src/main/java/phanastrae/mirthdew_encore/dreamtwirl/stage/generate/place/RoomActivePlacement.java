package phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import phanastrae.mirthdew_encore.util.FloatNoise2D;

public class RoomActivePlacement {
    public static final FloatNoise2D NOISE = generateNoise();
    // max noise delay, in seconds
    public static final float MAX_NOISE_DELAY = 0.2F;
    // max noise delay, in ticks
    public static final float MAX_NOISE_DELAY_TICKS = MAX_NOISE_DELAY * 20;
    // foam movement speed, in blocks per second
    public static final float FOAM_SPEED = 8.5F;
    // time it takes for foam to move, in ticks per block
    public static final float TICKS_PER_BLOCK = 20 / FOAM_SPEED;
    // time it takes for foam to convert into real blocks, in seconds
    public static final float FOAM_DELAY = 0.9F;
    // time it takes for foam to convert into real blocks, in ticks
    public static final float FOAM_DELAY_TICKS = FOAM_DELAY * 20;

    public static float getNoise(int x, int z) {
        return NOISE.get(x, z);
    }

    public static boolean canPlaceInBoxAtTime(BlockPos placementOrigin, int time, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        // we always reach one of the corners last, so if they have all been reached we can skip generation
        boolean beyondAllMaxTimes = true;
        for(int i = 0; i < 8; i++) {
            int x = (i & 0x1) == 0 ? minX : maxX;
            int y = (i & 0x2) == 0 ? minY : maxY;
            int z = (i & 0x4) == 0 ? minZ : maxZ;

            int maxTimeToReachPos = getTimeToReachPos(placementOrigin, x, y, z, true, getNoise(x, z)) + 1;
            if(time <= maxTimeToReachPos) {
                beyondAllMaxTimes = false;
                break;
            }
        }
        if(beyondAllMaxTimes) {
            return false;
        }

        // first x,y,z reached should be the ones with the same x,y,z as placementOrigin, or the closest to that
        int earliestX = Mth.clamp(placementOrigin.getX(), minX, maxX);
        int earliestY = Mth.clamp(placementOrigin.getY(), minY, maxY);
        int earliestZ = Mth.clamp(placementOrigin.getZ(), minZ, maxZ);
        // if we are too early to reach any part of the box, then skip
        int minTimeToReachBox = getTimeToReachPos(placementOrigin, earliestX, earliestY, earliestZ, false, 0) - 1;
        if(time < minTimeToReachBox) {
            return false;
        }

        return true;
    }

    public static boolean canPlaceInColumnAtTime(BlockPos placementOrigin, int time, int x, int z, int minY, int maxY) {
        float noise = getNoise(x, z);

        // we always reach either the top or bottom last, so if both have already been reached then skip
        int maxTimeToReachBottom = getTimeToReachPos(placementOrigin, x, minY, z, true, noise) + 1;
        int maxTimeToReachTop = getTimeToReachPos(placementOrigin, x, maxY, z, true, noise) + 1;
        if(time > maxTimeToReachBottom && time > maxTimeToReachTop) {
            return false;
        }

        // first y reached should be the one on same height as placementOrigin, or the closest to that
        int earliestY = Mth.clamp(placementOrigin.getY(), minY, maxY);
        // if we are too early to reach any part of the column, then skip
        int minTimeToReachColumn = getTimeToReachPos(placementOrigin, x, earliestY, z, false, noise) - 1;
        if(time < minTimeToReachColumn) {
            return false;
        }

        return true;
    }

    public static int getTimeToReachPos(BlockPos placementOrigin, BlockPos pos, boolean addDelay, int x, int z) {
        return getTimeToReachPos(placementOrigin, pos, addDelay, getNoise(x, z));
    }

    public static int getTimeToReachPos(BlockPos placementOrigin, BlockPos pos, boolean addDelay, float noise) {
        return getTimeToReachPos(placementOrigin, pos.getX(), pos.getY(), pos.getZ(), addDelay, noise);
    }

    public static int getTimeToReachPos(BlockPos placementOrigin, int x, int y, int z, boolean addDelay, float noise) {
        if(placementOrigin == null) return 0;

        int dx = placementOrigin.getX() - x;
        int dy = placementOrigin.getY() - y;
        int dz = placementOrigin.getZ() - z;
        int distSqr = dx*dx + dy*dy + dz*dz;
        return Mth.ceil(Math.sqrt(distSqr) * TICKS_PER_BLOCK + (addDelay ? FOAM_DELAY_TICKS : 0) + noise); // TODO change this
    }

    public static void setBlock(ServerLevel level, BlockPos pos, BlockState state, boolean updateNeighbors) {
        level.setBlock(pos, state, updateNeighbors ? 3 : 2, 512);
    }

    public static void playBlockPlaceEffects(ServerLevel level, RandomSource random, BlockPos pos) {
        if(blockExposed(level, pos) || random.nextInt(8) == 0) {
            if (random.nextInt(15) == 0) {
                level.playSound(null,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 0.9F, 0.4F + 0.2F * random.nextFloat());
            }
            if (random.nextInt(4) == 0) {
                level.sendParticles(ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        14, 0.5, 0.5, 0.5, 0.1);
            }
        }
    }

    public static void playFoamPlaceEffects(ServerLevel level, RandomSource random, BlockPos pos) {
        if(blockExposed(level, pos) || random.nextInt(8) == 0) {
            if (random.nextInt(15) == 0) {
                level.playSound(null,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS, 0.5F, 0.4F + 0.2F * random.nextFloat());
            }
            if (random.nextInt(4) == 0) {
                level.sendParticles(ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        14, 0.5, 0.5, 0.5, 0.1);
            }
        }
    }

    public static boolean blockExposed(LevelReader level, BlockPos pos) {
        BlockPos.MutableBlockPos adjPos = pos.mutable();
        for(Direction direction : Direction.values()) {
            adjPos.setWithOffset(pos, direction);
            BlockState adjState = level.getBlockState(adjPos);
            if(!adjState.isFaceSturdy(level, adjPos, direction.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    public static void tryUpdateSelf(ServerLevel level, BlockPos pos, BlockState state) {
        FluidState fluidState = state.getFluidState();
        if (!fluidState.isEmpty()) {
            fluidState.tick(level, pos);
        }

        Block block = state.getBlock();
        if (!(block instanceof LiquidBlock)) {
            BlockState newState = Block.updateFromNeighbourShapes(state, level, pos);
            if (!newState.equals(state)) {
                level.setBlock(pos, newState, 20);
            }
        }
    }

    public static boolean isStateFragile(BlockState state, LevelReader levelReader, BlockPos blockPos) {
        Block block = state.getBlock();
        if(block instanceof EntityBlock) {
            return true;
        }

        // TODO tweak which blocks are/aren't fragile
        /*
        if(state.is(HyphaPiraceaBlockTags.PLACEMENT_FRAGILE)) {
            return true;
        }
        */

        if(block instanceof DoorBlock) {
            // bottoms of doors are not always properly detected by the canSurvive check
            return true;
        }

        if(!state.canSurvive(levelReader, blockPos)) {
            // this prevents some, but not all, problems
            return true;
        }

        return false;
    }

    public static FloatNoise2D generateNoise() {
        RandomSource randomSource = RandomSource.create(12345);
        return FloatNoise2D.generateNoise(128, 128, () -> randomSource.nextFloat() * MAX_NOISE_DELAY_TICKS);
    }
}
