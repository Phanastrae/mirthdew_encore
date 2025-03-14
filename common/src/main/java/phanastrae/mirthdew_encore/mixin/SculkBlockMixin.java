package phanastrae.mirthdew_encore.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.block.entity.MirthdewEncoreBlockEntityTypes;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

@Mixin(SculkBlock.class)
public abstract class SculkBlockMixin extends DropExperienceBlock implements SculkBehaviour {

    public SculkBlockMixin(IntProvider experienceDropped, Properties settings) {
        super(experienceDropped, settings);
    }

    @Inject(method = "attemptUseCharge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", shift = At.Shift.AFTER))
    private void mirthdew_encore$addDreamseedsToSnares(SculkSpreader.ChargeCursor cursor, LevelAccessor world, BlockPos catalystPos, RandomSource random, SculkSpreader spreadManager, boolean shouldConvertToBlock, CallbackInfoReturnable<Integer> cir) {
        if(spreadManager.isWorldGeneration()) {
            BlockPos pos = cursor.getPos().above();
            BlockState state = world.getBlockState(pos);
            if (state.is(MirthdewEncoreBlocks.VERIC_DREAMSNARE)) {
                world.getBlockEntity(pos, MirthdewEncoreBlockEntityTypes.VERIC_DREAMSNARE).ifPresent(dreamsnareBlockEntity -> {
                    if (random.nextFloat() > 0.4) {
                        dreamsnareBlockEntity.setHeldItem(MirthdewEncoreItems.DREAMSEED.getDefaultInstance());
                    }
                });
            }
        }
    }

    @Inject(method = "getRandomGrowthState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;hasProperty(Lnet/minecraft/world/level/block/state/properties/Property;)Z"))
    private void mirthdew_encore$addDreamsnareToSpawning(LevelAccessor world, BlockPos pos, RandomSource random, boolean allowShrieker, CallbackInfoReturnable<BlockState> cir,
                                                         @Local(ordinal = 0) LocalRef<BlockState> localBlockStateRef) {
        if(random.nextInt(20) == 0) {
            localBlockStateRef.set(MirthdewEncoreBlocks.VERIC_DREAMSNARE.defaultBlockState());
        }
    }

    @Inject(method = "canPlaceGrowth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z", ordinal = 1))
    private static void mirthdew_encore$limitDreamsnareSpawning(LevelAccessor world, BlockPos pos, CallbackInfoReturnable<Boolean> cir,
                                                                @Local(ordinal = 0) LocalIntRef localIntRef, @Local(ordinal = 1) BlockState localBlockStateRef) {
        if(localBlockStateRef.is(MirthdewEncoreBlocks.VERIC_DREAMSNARE)) {
            localIntRef.set(localIntRef.get() + 1);
        }
    }
}
