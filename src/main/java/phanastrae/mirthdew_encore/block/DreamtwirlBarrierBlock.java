package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DreamtwirlBarrierBlock extends Block {
    public static final MapCodec<DreamtwirlBarrierBlock> CODEC = createCodec(DreamtwirlBarrierBlock::new);

    @Override
    public MapCodec<DreamtwirlBarrierBlock> getCodec() {
        return CODEC;
    }

    public DreamtwirlBarrierBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        for(Direction direction : DIRECTIONS) {
            if(direction.getAxis().isVertical()) continue;

            BlockState adjacentState = world.getBlockState(pos.offset(direction));
            if(adjacentState.isOf(this)) continue;

            if(random.nextInt(2) == 0) {
                world.addParticle(ParticleTypes.SOUL,
                        x + direction.getOffsetX() * 0.5 - 0.5 + random.nextFloat(),
                        y - 0.5 + random.nextFloat(),
                        z + direction.getOffsetZ() * 0.5 - 0.5 + random.nextFloat(),
                        direction.getOffsetX() * 0.1,
                        0.04,
                        direction.getOffsetZ() * 0.1);
            }

            if (random.nextInt(1000) == 0) {
                world.playSound(
                        x,
                        y,
                        z,
                        SoundEvents.BLOCK_PORTAL_AMBIENT,
                        SoundCategory.BLOCKS,
                        0.2F + random.nextFloat() * 0.2F,
                        0.9F + random.nextFloat() * 0.15F,
                        false
                );
            }
        }
    }
}
