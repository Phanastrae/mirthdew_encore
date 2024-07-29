package phanastrae.mirthdew_encore.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlWorldAttachment;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mirthdew_encore$init(MinecraftServer server, Executor workerExecutor, LevelStorageSource.LevelStorageAccess session, ServerLevelData properties, ResourceKey worldKey, LevelStem dimensionOptions, ChunkProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List spawners, boolean shouldTickTime, RandomSequences randomSequencesState, CallbackInfo ci) {
        ServerLevel thisWorld = (ServerLevel) (Object)this;
        DreamtwirlWorldAttachment DTWA = DreamtwirlWorldAttachment.fromWorld(thisWorld);
        if(DTWA != null) {
            DTWA.setDreamtwirlStageManager(thisWorld);
        }
    }

    @Inject(method = "mayInteract", at = @At("HEAD"), cancellable = true)
    private void mirthdew_encore$preventAdjacentDreamtwirlInteraction(Player player, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(DreamtwirlWorldAttachment.positionsAreInSeperateDreamtwirls(player.level(), player.position(), Vec3.atCenterOf(pos))) {
            cir.setReturnValue(false);
        }
    }
}
