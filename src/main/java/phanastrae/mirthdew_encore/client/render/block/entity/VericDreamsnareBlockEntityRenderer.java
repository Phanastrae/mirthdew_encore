package phanastrae.mirthdew_encore.client.render.block.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Math;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.entity.VericDreamsnareBlockEntity;
import phanastrae.mirthdew_encore.client.render.entity.model.MirthdewEncoreEntityModelLayers;

public class VericDreamsnareBlockEntityRenderer implements BlockEntityRenderer<VericDreamsnareBlockEntity> {
    private static final Identifier TEXTURE = MirthdewEncore.id("textures/entity/veric_dreamsnare/tongue.png");

    private final ModelPart tongueBody;
    private final ModelPart tongueTip;
    private final ItemRenderer itemRenderer;

    public VericDreamsnareBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        ModelPart root = context.getLayerModelPart(MirthdewEncoreEntityModelLayers.DREAMSNARE_TONGUE);
        this.tongueBody = root.getChild("tongue_body");
        this.tongueTip = root.getChild("tongue_tip");
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(VericDreamsnareBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
        World world = entity.getWorld();
        long time = world == null ? 0 : world.getTime();

        boolean holdingItem = entity.isHoldingItem();

        matrices.push();
        Vec3d tongueBaseOffset = entity.getTongueBaseOffset();
        matrices.translate(tongueBaseOffset.x, tongueBaseOffset.y, tongueBaseOffset.z);

        Vec3d baseOffset = entity.getBaseOffset();
        Vec3d tongueTargetOffset = holdingItem ? tongueBaseOffset : entity.getTongueTargetOffset();
        Vec3d targetOffset = tongueTargetOffset.subtract(baseOffset);
        Vec3d offsetNormalised = targetOffset.normalize();

        double offset = 0.5 + 0.25 * Math.sin(Math.toRadians(((time % 90) + tickDelta) * 4));
        double tongueLength = holdingItem ? offset : entity.getTongueLength(tickDelta);

        float yaw = (float) MathHelper.atan2(offsetNormalised.x, offsetNormalised.z);
        float pitch = (float)MathHelper.atan2(offsetNormalised.y, offsetNormalised.horizontalLength());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(yaw));
        matrices.multiply(RotationAxis.NEGATIVE_X.rotation(pitch - MathHelper.HALF_PI));

        float l = (float)tongueLength;

        matrices.push();
        matrices.translate(0, l - 1, 0);
        this.tongueTip.render(matrices, vertexConsumer, light, overlay);
        matrices.pop();

        if(l > 0) {
            matrices.push();
            matrices.scale(1, l, 1);
            this.tongueBody.render(matrices, vertexConsumer, light, overlay);
            matrices.pop();
        }

        if(holdingItem) {
            ItemStack itemStack = entity.getHeldItem();

            if(world != null) {
                matrices.push();

                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((time % 120) + tickDelta) * 3));

                matrices.translate(0, offset + 0.4 , 0);

                matrices.scale(2, 2, 2);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

                this.itemRenderer.renderItem(
                        null,
                        itemStack,
                        ModelTransformationMode.FIXED,
                        false,
                        matrices,
                        vertexConsumers,
                        world,
                        light,
                        overlay,
                        0
                );

                matrices.pop();
            }
        }

        matrices.pop();
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData tongue_body = modelPartData.addChild(
                "tongue_body",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-1.0F, -18.0F, -0.5F, 2.0F, 16.0F, 2.0F),
                ModelTransform.pivot(0.0F, 18.0F, -0.5F));

        ModelPartData tongue_tip = modelPartData.addChild(
                "tongue_tip",
                ModelPartBuilder.create()
                        .uv(18, 8)
                        .cuboid(-1.5F, -5.5F, -1.0F, 3.0F, 5.0F, 3.0F)
                        .uv(8, 0)
                        .cuboid(0.0F, -1.0F, -2.0F, 0.0F, 4.0F, 5.0F),
                ModelTransform.pivot(0.0F, 18.0F, -0.5F));

        ModelPartData rotated_block = tongue_tip.addChild(
                "rotated_block",
                ModelPartBuilder.create()
                        .uv(18, 0)
                        .cuboid(-1.5F, -2.5F, -1.5F, 3.0F, 5.0F, 3.0F),
                ModelTransform.of(0.0F, -1.0F, 0.5F, 0.0F, 0.7854F, 0.0F));

        ModelPartData rotated_flat = tongue_tip.addChild(
                "rotated_flat",
                ModelPartBuilder.create()
                        .uv(8, 0)
                        .cuboid(0.0F, -1.0F, -2.0F, 0.0F, 4.0F, 5.0F),
                ModelTransform.of(0.5F, 0.0F, 0.5F, 0.0F, -1.5708F, 0.0F));

        return TexturedModelData.of(modelData, 32, 32);
    }


}
