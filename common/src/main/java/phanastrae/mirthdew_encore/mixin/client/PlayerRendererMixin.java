package phanastrae.mirthdew_encore.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.client.render.entity.WarpRendering;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityAttachment;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/PlayerModel;setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", shift = At.Shift.AFTER), cancellable = true)
    private void mirthdewEncore$renderHandWhenWarping(PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, ModelPart rendererArm, ModelPart rendererArmwear, CallbackInfo ci) {
        MirthdewEncoreEntityAttachment meea = MirthdewEncoreEntityAttachment.fromEntity(player);
        if(meea.isWarping()) {
            int color = WarpRendering.getWarpColor(meea, false, 0);
            int light = WarpRendering.getLightColor(meea, combinedLight, 0);

            ResourceLocation resourcelocation = player.getSkin().texture();
            RenderType renderType = RenderType.entityTranslucent(resourcelocation);
            VertexConsumer vc = buffer.getBuffer(renderType);

            rendererArm.xRot = 0.0F;
            rendererArm.render(poseStack, vc, light, OverlayTexture.NO_OVERLAY, color);
            rendererArmwear.xRot = 0.0F;
            rendererArmwear.render(poseStack, vc, light, OverlayTexture.NO_OVERLAY, color);

            ci.cancel();
        }
    }
}
