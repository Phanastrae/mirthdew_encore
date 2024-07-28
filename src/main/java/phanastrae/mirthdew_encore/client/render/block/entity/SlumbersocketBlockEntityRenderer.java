package phanastrae.mirthdew_encore.client.render.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.entity.SlumbersocketBlockEntity;
import phanastrae.mirthdew_encore.client.render.entity.model.MirthdewEncoreEntityModelLayers;

public class SlumbersocketBlockEntityRenderer implements BlockEntityRenderer<SlumbersocketBlockEntity> {
    private static final Identifier TEXTURE = MirthdewEncore.id("textures/entity/slumbersocket/eye.png");
    private static final Identifier TEXTURE_DREAMING = MirthdewEncore.id("textures/entity/slumbersocket/eye_dreaming.png");

    private final ModelPart eye;

    public SlumbersocketBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.eye = context.getLayerModelPart(MirthdewEncoreEntityModelLayers.SLUMBERSOCKET_EYE);
    }

    @Override
    public void render(SlumbersocketBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(entity.isHoldingItem()) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(this.getTexture(entity.getCachedState())));

            matrices.push();
            matrices.translate(0.5, 0.5, 0.5);

            float yaw = -MathHelper.lerpAngleDegrees(tickDelta, entity.prevYaw, entity.yaw);
            float pitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.pitch);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
            matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(pitch));

            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
            this.eye.render(matrices, vertexConsumer, light, overlay);

            matrices.pop();
        }
    }

    public Identifier getTexture(BlockState state) {
        return (SlumbersocketBlockEntity.isDreaming(state)) ? TEXTURE_DREAMING : TEXTURE;
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData eye = modelPartData.addChild(
                "eye",
                ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 32, 16);
    }
}
