package phanastrae.mirthdew_encore.client.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.*;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.entity.SlumbersocketBlockEntity;
import phanastrae.mirthdew_encore.client.render.entity.model.MirthdewEncoreEntityModelLayers;

public class SlumbersocketBlockEntityRenderer implements BlockEntityRenderer<SlumbersocketBlockEntity> {
    private static final ResourceLocation TEXTURE = MirthdewEncore.id("textures/entity/slumbersocket/eye.png");
    private static final ResourceLocation TEXTURE_DREAMING = MirthdewEncore.id("textures/entity/slumbersocket/eye_dreaming.png");

    private final ModelPart eye;

    public SlumbersocketBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.eye = context.bakeLayer(MirthdewEncoreEntityModelLayers.SLUMBERSOCKET_EYE);
    }

    @Override
    public void render(SlumbersocketBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if(entity.isHoldingItem()) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutout(this.getTexture(entity.getBlockState())));

            matrices.pushPose();
            matrices.translate(0.5, 0.5, 0.5);

            float yaw = -Mth.rotLerp(tickDelta, entity.prevYaw, entity.yaw);
            float pitch = Mth.lerp(tickDelta, entity.prevPitch, entity.pitch);
            matrices.mulPose(Axis.YP.rotationDegrees(yaw));
            matrices.mulPose(Axis.XN.rotationDegrees(pitch));

            matrices.mulPose(Axis.XP.rotationDegrees(180));
            this.eye.render(matrices, vertexConsumer, light, overlay);

            matrices.popPose();
        }
    }

    public ResourceLocation getTexture(BlockState state) {
        return (SlumbersocketBlockEntity.isDreaming(state)) ? TEXTURE_DREAMING : TEXTURE;
    }
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition eye = modelPartData.addOrReplaceChild(
                "eye",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(modelData, 32, 16);
    }
}
