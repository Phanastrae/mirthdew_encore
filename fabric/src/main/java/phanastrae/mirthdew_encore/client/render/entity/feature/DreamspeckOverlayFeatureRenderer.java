package phanastrae.mirthdew_encore.client.render.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import phanastrae.mirthdew_encore.client.render.entity.model.DreamspeckEntityModel;
import phanastrae.mirthdew_encore.client.render.entity.model.MirthdewEncoreEntityModelLayers;

public class DreamspeckOverlayFeatureRenderer<T extends LivingEntity> extends RenderLayer<T, DreamspeckEntityModel<T>> {
    private final EntityModel<T> model;

    public DreamspeckOverlayFeatureRenderer(RenderLayerParent<T, DreamspeckEntityModel<T>> context, EntityModelSet loader) {
        super(context);
        this.model = new DreamspeckEntityModel<>(loader.bakeLayer(MirthdewEncoreEntityModelLayers.DREAMSPECK_OUTER));
    }

    public void render(
            PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch
    ) {
        Minecraft client = Minecraft.getInstance();
        boolean renderOnlyOutline = client.shouldEntityAppearGlowing(entity) && entity.isInvisible();
        if (!entity.isInvisible() || renderOnlyOutline) {
            VertexConsumer vertexConsumer;
            if (renderOnlyOutline) {
                vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.outline(this.getTextureLocation(entity)));
            } else {
                vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
            }

            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(entity, limbAngle, limbDistance, tickDelta);
            this.model.setupAnim(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            this.model.renderToBuffer(matrixStack, vertexConsumer, light, LivingEntityRenderer.getOverlayCoords(entity, 0.0F));
        }
    }
}
