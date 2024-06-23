package phanastrae.mirthdew_encore.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.BlockState;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.SculkBlock;
import net.minecraft.block.SculkSpreadable;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.block.entity.MirthdewEncoreBlockEntityTypes;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

@Mixin(SculkBlock.class)
public abstract class SculkBlockMixin extends ExperienceDroppingBlock implements SculkSpreadable {

    public SculkBlockMixin(IntProvider experienceDropped, Settings settings) {
        super(experienceDropped, settings);
    }

    @Inject(method = "spread", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", shift = At.Shift.AFTER))
    private void mirthdew_encore$addDreamseedsToSnares(SculkSpreadManager.Cursor cursor, WorldAccess world, BlockPos catalystPos, Random random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock, CallbackInfoReturnable<Integer> cir) {
        if(spreadManager.isWorldGen()) {
            BlockPos pos = cursor.getPos().up();
            BlockState state = world.getBlockState(pos);
            if (state.isOf(MirthdewEncoreBlocks.VERIC_DREAMSNARE)) {
                world.getBlockEntity(pos, MirthdewEncoreBlockEntityTypes.VERIC_DREAMSNARE).ifPresent(dreamsnareBlockEntity -> {
                    if (random.nextFloat() > 0.4) {
                        dreamsnareBlockEntity.setHeldItem(MirthdewEncoreItems.DREAMSEED.getDefaultStack());
                    }
                });
            }
        }
    }

    @Inject(method = "getExtraBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;contains(Lnet/minecraft/state/property/Property;)Z", shift = At.Shift.BEFORE))
    private void mirthdew_encore$addDreamsnareToSpawning(WorldAccess world, BlockPos pos, Random random, boolean allowShrieker, CallbackInfoReturnable<BlockState> cir,
                                                         @Local(ordinal = 0) LocalRef<BlockState> localBlockStateRef) {
        if(random.nextInt(20) == 0) {
            localBlockStateRef.set(MirthdewEncoreBlocks.VERIC_DREAMSNARE.getDefaultState());
        }
    }

    @Inject(method = "shouldNotDecay", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 1, shift = At.Shift.BEFORE))
    private static void mirthdew_encore$limitDreamsnareSpawning(WorldAccess world, BlockPos pos, CallbackInfoReturnable<Boolean> cir,
                                                                @Local(ordinal = 0) LocalIntRef localIntRef, @Local(ordinal = 1) BlockState localBlockStateRef) {
        if(localBlockStateRef.isOf(MirthdewEncoreBlocks.VERIC_DREAMSNARE)) {
            localIntRef.set(localIntRef.get() + 1);
        }
    }
}
