package phanastrae.mirthdew_encore.render.entity.model;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.render.block.entity.SlumbersocketBlockEntityRenderer;
import phanastrae.mirthdew_encore.render.block.entity.VericDreamsnareBlockEntityRenderer;

public class MirthdewEncoreEntityModelLayers {

    public static final EntityModelLayer DREAMSPECK = createMainLayer("dreamspeck");
    public static final EntityModelLayer DREAMSPECK_OUTER = createLayer("dreamspeck", "outer");
    public static final EntityModelLayer DREAMSNARE_TONGUE = createMainLayer("veric_dreamsnare_tongue");
    public static final EntityModelLayer SLUMBERSOCKET_EYE = createMainLayer("slumbersocket_eye");

    public static void init() {
        registerModelLayer(DREAMSPECK, DreamspeckEntityModel::getInnerTexturedModelData);
        registerModelLayer(DREAMSPECK_OUTER, DreamspeckEntityModel::getOuterTexturedModelData);
        registerModelLayer(DREAMSNARE_TONGUE, VericDreamsnareBlockEntityRenderer::getTexturedModelData);
        registerModelLayer(SLUMBERSOCKET_EYE, SlumbersocketBlockEntityRenderer::getTexturedModelData);
    }

    private static EntityModelLayer createMainLayer(String id) {
        return new EntityModelLayer(MirthdewEncore.id(id), "main");
    }

    private static EntityModelLayer createLayer(String id, String layer) {
        return new EntityModelLayer(MirthdewEncore.id(id), layer);
    }

    private static <E extends Entity> void registerModelLayer(EntityModelLayer modelLayer, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        EntityModelLayerRegistry.registerModelLayer(modelLayer, provider);
    }
}
