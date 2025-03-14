package phanastrae.mirthdew_encore.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.client.render.entity.WarpRendering;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityAttachment;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
    @Shadow protected M model;

    @Shadow @Nullable protected abstract RenderType getRenderType(T livingEntity, boolean bodyVisible, boolean translucent, boolean glowing);

    @Shadow protected abstract boolean isBodyVisible(T livingEntity);

    @Shadow public static int getOverlayCoords(LivingEntity livingEntity, float u) {
        return 0;
    }

    private LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Unique private boolean mirthdewEncore$cancelNextGetRenderType = false;
    @Unique private boolean mirthdewEncore$needToRenderAgain = false;

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getRenderType(Lnet/minecraft/world/entity/LivingEntity;ZZZ)Lnet/minecraft/client/renderer/RenderType;"))
    private void mirthdewEncore$markNormalRenderForCancel(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if(MirthdewEncoreEntityAttachment.fromEntity(entity).isWarping()) {
            this.mirthdewEncore$cancelNextGetRenderType = true;
        }
    }

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void mirthdewEncore$cancelNormalRender(T livingEntity, boolean bodyVisible, boolean translucent, boolean glowing, CallbackInfoReturnable<RenderType> cir) {
        if(this.mirthdewEncore$cancelNextGetRenderType) {
            this.mirthdewEncore$cancelNextGetRenderType = false;
            this.mirthdewEncore$needToRenderAgain = true;
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSpectator()Z"))
    private void mirthdewEncore$renderWithColor(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if(this.mirthdewEncore$needToRenderAgain) {
            this.mirthdewEncore$needToRenderAgain = false;

            Minecraft minecraft = Minecraft.getInstance();
            boolean bodyVisible = this.isBodyVisible(entity);
            boolean translucent = !bodyVisible && !entity.isInvisibleTo(minecraft.player);
            boolean glowing = minecraft.shouldEntityAppearGlowing(entity);

            MirthdewEncoreEntityAttachment meea = MirthdewEncoreEntityAttachment.fromEntity(entity);
            int color = WarpRendering.getWarpColor(meea, translucent, partialTicks);
            int light = WarpRendering.getLightColor(meea, packedLight, partialTicks);

            RenderType renderType = this.getRenderType(entity, bodyVisible, true, glowing);
            if (renderType != null) {
                VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
                this.model.renderToBuffer(poseStack, vertexConsumer, light, getOverlayCoords(entity, 0), color);
            }
        }
    }
}
