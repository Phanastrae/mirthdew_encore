package phanastrae.mirthdew_encore.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlLevelAttachment;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mirthdew_encore$init(MinecraftServer server, Executor workerExecutor, LevelStorageSource.LevelStorageAccess session, ServerLevelData properties, ResourceKey worldKey, LevelStem dimensionOptions, ChunkProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List spawners, boolean shouldTickTime, RandomSequences randomSequencesState, CallbackInfo ci) {
        ServerLevel thisWorld = (ServerLevel) (Object)this;
        DreamtwirlLevelAttachment DTLA = DreamtwirlLevelAttachment.fromLevel(thisWorld);
        if(DTLA != null) {
            DTLA.setDreamtwirlStageManager(thisWorld);
        }
    }

    @Inject(method = "mayInteract", at = @At("HEAD"), cancellable = true)
    private void mirthdew_encore$preventAdjacentDreamtwirlInteraction(Player player, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(DreamtwirlLevelAttachment.positionsAreInSeparateOrUnstableDreamtwirls(player.level(), player.position(), Vec3.atCenterOf(pos))) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "shouldTickBlocksAt", at = @At("HEAD"), cancellable = true)
    private void mirthdew_encore$preventBlockTicksInDeletingDreamtwirls(long chunkPos, CallbackInfoReturnable<Boolean> cir) {
        DreamtwirlLevelAttachment DTLA = DreamtwirlLevelAttachment.fromLevel((ServerLevel)(Object)this);
        if(DTLA != null) {
            DreamtwirlStageManager DSM = DTLA.getDreamtwirlStageManager();
            if(DSM != null) {
                RegionPos regionPos = RegionPos.fromChunkPos(new ChunkPos(chunkPos));
                DreamtwirlStage stage = DSM.getDreamtwirlIfPresent(regionPos);
                if(stage != null && stage.isDeletingSelf()) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
