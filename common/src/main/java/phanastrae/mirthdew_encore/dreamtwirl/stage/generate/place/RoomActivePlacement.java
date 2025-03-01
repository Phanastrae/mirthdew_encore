package phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
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

    public static int getTimeToReachPos(BlockPos placementOrigin, BlockPos pos, boolean addDelay, int x, int z) {
        return getTimeToReachPos(placementOrigin, pos, addDelay, getNoise(x, z));
    }

    public static int getTimeToReachPos(BlockPos placementOrigin, BlockPos pos, boolean addDelay, float noise) {
        if(placementOrigin == null) return 0;

        Vec3i dif = placementOrigin.subtract(pos);
        int maxDist = dif.getX()*dif.getX() + dif.getY()*dif.getY() + dif.getZ()*dif.getZ();
        return Mth.ceil(Math.sqrt(maxDist) * TICKS_PER_BLOCK + (addDelay ? FOAM_DELAY_TICKS : 0) + noise); // TODO change this
    }

    public static void setBlock(ServerLevel level, BlockPos pos, BlockState state, boolean updateNeighbors) {
        level.setBlock(pos, state, updateNeighbors ? 3 : 2, 512);
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
