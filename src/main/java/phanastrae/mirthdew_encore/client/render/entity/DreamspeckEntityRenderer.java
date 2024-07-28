package phanastrae.mirthdew_encore.client.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.entity.DreamspeckEntity;
import phanastrae.mirthdew_encore.client.render.entity.feature.DreamspeckOverlayFeatureRenderer;
import phanastrae.mirthdew_encore.client.render.entity.model.DreamspeckEntityModel;
import phanastrae.mirthdew_encore.client.render.entity.model.MirthdewEncoreEntityModelLayers;

public class DreamspeckEntityRenderer extends MobEntityRenderer<DreamspeckEntity, DreamspeckEntityModel<DreamspeckEntity>> {
    private static final Identifier TEXTURE = MirthdewEncore.id("textures/entity/dreamspeck/dreamspeck.png");

    protected DreamspeckEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new DreamspeckEntityModel<>(context.getPart(MirthdewEncoreEntityModelLayers.DREAMSPECK)), 0.2F);
        this.addFeature(new DreamspeckOverlayFeatureRenderer<>(this, context.getModelLoader()));
    }

    @Override
    protected int getBlockLight(DreamspeckEntity entity, BlockPos pos) {
        return Math.max(12, super.getBlockLight(entity, pos));
    }

    @Override
    public Identifier getTexture(DreamspeckEntity entity) {
        return TEXTURE;
    }
}
