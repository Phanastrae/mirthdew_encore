package phanastrae.mirthdew_encore.client.render.entity.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class DreamspeckEntityModel<T extends Entity> extends EntityModel<T> {

    private final ModelPart root;

    public DreamspeckEntityModel(ModelPart root) {
        this.root = root.getChild("root");
    }
    public static TexturedModelData getInnerTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root",
                ModelPartBuilder.create()
                        .uv(0, 8)
                        .cuboid(-1.5F, 18.5F, -1.5F, 3.0F, 3.0F, 3.0F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 16, 16);
    }
    public static TexturedModelData getOuterTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-2.0F, 18.0F, -2.0F, 4.0F, 4.0F, 4.0F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }
}
