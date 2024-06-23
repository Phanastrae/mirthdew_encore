package phanastrae.mirthdew_encore.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.entity.DreamspeckEntity;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;

public class DreamseedBlock extends Block {
    protected static final VoxelShape SHAPE = VoxelShapes.combine(
            Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 9.0, 12.0),
            Block.createCuboidShape(7.0, 9.0, 7.0, 9.0, 12.0, 9.0),
            BooleanBiFunction.OR);

    public DreamseedBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSideSolidFullSquare(world, pos, Direction.UP);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        if (!world.isClient) {
            if (!EnchantmentHelper.hasAnyEnchantmentsIn(tool, EnchantmentTags.PREVENTS_DECORATED_POT_SHATTERING)) {
                DreamspeckEntity dreamspeckEntity = MirthdewEncoreEntityTypes.DREAM_SPECK.create(world);
                if(dreamspeckEntity != null) {
                    dreamspeckEntity.setPosition(pos.toBottomCenterPos());
                    world.spawnEntity(dreamspeckEntity);
                }
            }
        }
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
