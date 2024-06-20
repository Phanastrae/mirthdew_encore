package phanastrae.mirthdew_encore.render.entity.feature;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import phanastrae.mirthdew_encore.render.entity.model.DreamspeckEntityModel;
import phanastrae.mirthdew_encore.render.entity.model.MirthdewEncoreEntityModelLayers;

public class DreamspeckOverlayFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, DreamspeckEntityModel<T>> {
    private final EntityModel<T> model;

    public DreamspeckOverlayFeatureRenderer(FeatureRendererContext<T, DreamspeckEntityModel<T>> context, EntityModelLoader loader) {
        super(context);
        this.model = new DreamspeckEntityModel<>(loader.getModelPart(MirthdewEncoreEntityModelLayers.DREAMSPECK_OUTER));
    }

    public void render(
            MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch
    ) {
        MinecraftClient client = MinecraftClient.getInstance();
        boolean renderOnlyOutline = client.hasOutline(entity) && entity.isInvisible();
        if (!entity.isInvisible() || renderOnlyOutline) {
            VertexConsumer vertexConsumer;
            if (renderOnlyOutline) {
                vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getOutline(this.getTexture(entity)));
            } else {
                vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(entity)));
            }

            this.getContextModel().copyStateTo(this.model);
            this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            this.model.render(matrixStack, vertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0F));
        }
    }
}
