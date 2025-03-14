package phanastrae.mirthdew_encore.client.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.entity.VericDreamsnareBlockEntity;
import phanastrae.mirthdew_encore.client.render.entity.model.MirthdewEncoreEntityModelLayers;

public class VericDreamsnareBlockEntityRenderer implements BlockEntityRenderer<VericDreamsnareBlockEntity> {
    private static final ResourceLocation TEXTURE = MirthdewEncore.id("textures/entity/veric_dreamsnare/tongue.png");

    private final ModelPart tongueBody;
    private final ModelPart tongueTip;
    private final ItemRenderer itemRenderer;

    public VericDreamsnareBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(MirthdewEncoreEntityModelLayers.DREAMSNARE_TONGUE);
        this.tongueBody = root.getChild("tongue_body");
        this.tongueTip = root.getChild("tongue_tip");
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(VericDreamsnareBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutout(TEXTURE));
        Level level = entity.getLevel();
        long time = level == null ? 0 : level.getGameTime();

        boolean holdingItem = entity.isHoldingItem();

        matrices.pushPose();
        Vec3 tongueBaseOffset = entity.getTongueBaseOffset();
        matrices.translate(tongueBaseOffset.x, tongueBaseOffset.y, tongueBaseOffset.z);

        Vec3 baseOffset = entity.getBaseOffset();
        Vec3 tongueTargetOffset = holdingItem ? tongueBaseOffset : entity.getTongueTargetOffset();
        Vec3 targetOffset = tongueTargetOffset.subtract(baseOffset);
        Vec3 offsetNormalised = targetOffset.normalize();

        double offset = 0.5 + 0.25 * Math.sin(Math.toRadians(((time % 90) + tickDelta) * 4));
        double tongueLength = holdingItem ? offset : entity.getTongueLength(tickDelta);

        float yaw = (float)Mth.atan2(offsetNormalised.x, offsetNormalised.z);
        float pitch = (float)Mth.atan2(offsetNormalised.y, offsetNormalised.horizontalDistance());
        matrices.mulPose(Axis.YP.rotation(yaw));
        matrices.mulPose(Axis.XN.rotation(pitch - Mth.HALF_PI));

        float l = (float)tongueLength;

        float spinRotation = l * Mth.TWO_PI / 5F;
        matrices.mulPose(Axis.YP.rotation(spinRotation));

        matrices.pushPose();
        matrices.translate(0, l - 1, 0);
        this.tongueTip.render(matrices, vertexConsumer, light, overlay);
        matrices.popPose();

        if(l > 0) {
            matrices.pushPose();
            matrices.scale(1, l, 1);
            this.tongueBody.render(matrices, vertexConsumer, light, overlay);
            matrices.popPose();
        }

        if(holdingItem) {
            ItemStack itemStack = entity.getHeldItem();

            if(level != null) {
                matrices.pushPose();

                matrices.mulPose(Axis.YP.rotationDegrees(((time % 120) + tickDelta) * 3));

                matrices.translate(0, offset + 0.4 , 0);

                matrices.scale(2, 2, 2);
                matrices.mulPose(Axis.XP.rotationDegrees(180));

                this.itemRenderer.renderStatic(
                        null,
                        itemStack,
                        ItemDisplayContext.FIXED,
                        false,
                        matrices,
                        vertexConsumers,
                        level,
                        light,
                        overlay,
                        0
                );

                matrices.popPose();
            }
        }

        matrices.popPose();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        PartDefinition tongue_body = modelPartData.addOrReplaceChild(
                "tongue_body",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-1.0F, -18.0F, -0.5F, 2.0F, 16.0F, 2.0F),
                PartPose.offset(0.0F, 18.0F, -0.5F));

        PartDefinition tongue_tip = modelPartData.addOrReplaceChild(
                "tongue_tip",
                CubeListBuilder.create()
                        .texOffs(18, 8)
                        .addBox(-1.5F, -5.5F, -1.0F, 3.0F, 5.0F, 3.0F)
                        .texOffs(8, 0)
                        .addBox(0.0F, -1.0F, -2.0F, 0.0F, 4.0F, 5.0F),
                PartPose.offset(0.0F, 18.0F, -0.5F));

        PartDefinition rotated_block = tongue_tip.addOrReplaceChild(
                "rotated_block",
                CubeListBuilder.create()
                        .texOffs(18, 0)
                        .addBox(-1.5F, -2.5F, -1.5F, 3.0F, 5.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, 0.5F, 0.0F, 0.7854F, 0.0F));

        PartDefinition rotated_flat = tongue_tip.addOrReplaceChild(
                "rotated_flat",
                CubeListBuilder.create()
                        .texOffs(8, 0)
                        .addBox(0.0F, -1.0F, -2.0F, 0.0F, 4.0F, 5.0F),
                PartPose.offsetAndRotation(0.5F, 0.0F, 0.5F, 0.0F, -1.5708F, 0.0F));

        return LayerDefinition.create(modelData, 32, 32);
    }


}
