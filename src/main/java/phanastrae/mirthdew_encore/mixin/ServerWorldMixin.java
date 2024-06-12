package phanastrae.mirthdew_encore.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlWorldAttachment;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mirthdew_encore$init(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List spawners, boolean shouldTickTime, RandomSequencesState randomSequencesState, CallbackInfo ci) {
        ServerWorld thisWorld = (ServerWorld) (Object)this;
        DreamtwirlWorldAttachment DTWA = DreamtwirlWorldAttachment.fromWorld(thisWorld);
        if(DTWA != null) {
            DTWA.setDreamtwirlStageManager(thisWorld);
        }
    }

    @Inject(method = "canPlayerModifyAt", at = @At("HEAD"), cancellable = true)
    private void mirthdew_encore$preventAdjacentDreamtwirlModification(PlayerEntity player, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(DreamtwirlWorldAttachment.positionsAreInSeperateDreamtwirls(player.getWorld(), player.getPos(), Vec3d.ofCenter(pos))) {
            cir.setReturnValue(false);
        }
    }
}
