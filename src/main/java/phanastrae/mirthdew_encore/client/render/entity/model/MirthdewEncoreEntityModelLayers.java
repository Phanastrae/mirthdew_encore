package phanastrae.mirthdew_encore.client.render.entity.model;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.world.entity.Entity;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.client.render.block.entity.SlumbersocketBlockEntityRenderer;
import phanastrae.mirthdew_encore.client.render.block.entity.VericDreamsnareBlockEntityRenderer;

public class MirthdewEncoreEntityModelLayers {

    public static final ModelLayerLocation DREAMSPECK = createMainLayer("dreamspeck");
    public static final ModelLayerLocation DREAMSPECK_OUTER = createLayer("dreamspeck", "outer");
    public static final ModelLayerLocation DREAMSNARE_TONGUE = createMainLayer("veric_dreamsnare_tongue");
    public static final ModelLayerLocation SLUMBERSOCKET_EYE = createMainLayer("slumbersocket_eye");

    public static void init() {
        registerModelLayer(DREAMSPECK, DreamspeckEntityModel::getInnerTexturedModelData);
        registerModelLayer(DREAMSPECK_OUTER, DreamspeckEntityModel::getOuterTexturedModelData);
        registerModelLayer(DREAMSNARE_TONGUE, VericDreamsnareBlockEntityRenderer::getTexturedModelData);
        registerModelLayer(SLUMBERSOCKET_EYE, SlumbersocketBlockEntityRenderer::getTexturedModelData);
    }

    private static ModelLayerLocation createMainLayer(String id) {
        return new ModelLayerLocation(MirthdewEncore.id(id), "main");
    }

    private static ModelLayerLocation createLayer(String id, String layer) {
        return new ModelLayerLocation(MirthdewEncore.id(id), layer);
    }

    private static <E extends Entity> void registerModelLayer(ModelLayerLocation modelLayer, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        EntityModelLayerRegistry.registerModelLayer(modelLayer, provider);
    }
}
