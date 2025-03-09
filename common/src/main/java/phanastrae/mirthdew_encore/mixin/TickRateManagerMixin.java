package phanastrae.mirthdew_encore.mixin;

import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlLevelAttachment;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.util.RegionPos;

@Mixin(TickRateManager.class)
public class TickRateManagerMixin {

    @Inject(method = "isEntityFrozen", at = @At("HEAD"), cancellable = true)
    private void mirthdew_encore$freezeEntitiesInDeletingDreamtwirls(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        DreamtwirlLevelAttachment DLA = DreamtwirlLevelAttachment.fromLevel(entity.level());
        if(DLA != null) {
            DreamtwirlStageManager DSM = DLA.getDreamtwirlStageManager();
            if(DSM != null) {
                RegionPos regionPos = RegionPos.fromEntity(entity);
                DreamtwirlStage stage = DSM.getDreamtwirlIfPresent(regionPos);
                if(stage != null && stage.isDeletingSelf()) {
                    if(!(entity instanceof Player) && entity.countPlayerPassengers() <= 0) {
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }
}
