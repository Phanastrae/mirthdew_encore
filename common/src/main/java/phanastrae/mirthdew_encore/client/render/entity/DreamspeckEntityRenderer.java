package phanastrae.mirthdew_encore.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.client.render.entity.feature.DreamspeckOverlayFeatureRenderer;
import phanastrae.mirthdew_encore.client.render.entity.model.DreamspeckEntityModel;
import phanastrae.mirthdew_encore.client.render.entity.model.MirthdewEncoreEntityModelLayers;
import phanastrae.mirthdew_encore.entity.DreamspeckEntity;

public class DreamspeckEntityRenderer extends MobRenderer<DreamspeckEntity, DreamspeckEntityModel<DreamspeckEntity>> {
    private static final ResourceLocation TEXTURE = MirthdewEncore.id("textures/entity/dreamspeck/dreamspeck.png");

    protected DreamspeckEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DreamspeckEntityModel<>(context.bakeLayer(MirthdewEncoreEntityModelLayers.DREAMSPECK)), 0.2F);
        this.addLayer(new DreamspeckOverlayFeatureRenderer<>(this, context.getModelSet()));
    }

    @Override
    protected int getBlockLightLevel(DreamspeckEntity entity, BlockPos pos) {
        return Math.max(12, super.getBlockLightLevel(entity, pos));
    }

    @Override
    public ResourceLocation getTextureLocation(DreamspeckEntity entity) {
        return TEXTURE;
    }
}
