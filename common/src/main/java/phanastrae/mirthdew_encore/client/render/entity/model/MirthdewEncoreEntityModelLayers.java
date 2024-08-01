package phanastrae.mirthdew_encore.client.render.entity.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.client.render.block.entity.SlumbersocketBlockEntityRenderer;
import phanastrae.mirthdew_encore.client.render.block.entity.VericDreamsnareBlockEntityRenderer;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MirthdewEncoreEntityModelLayers {

    public static final ModelLayerLocation DREAMSPECK = createMainLayer("dreamspeck");
    public static final ModelLayerLocation DREAMSPECK_OUTER = createLayer("dreamspeck", "outer");
    public static final ModelLayerLocation DREAMSNARE_TONGUE = createMainLayer("veric_dreamsnare_tongue");
    public static final ModelLayerLocation SLUMBERSOCKET_EYE = createMainLayer("slumbersocket_eye");

    public static void init(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> r) {
        r.accept(DREAMSPECK, DreamspeckEntityModel::getInnerTexturedModelData);
        r.accept(DREAMSPECK_OUTER, DreamspeckEntityModel::getOuterTexturedModelData);
        r.accept(DREAMSNARE_TONGUE, VericDreamsnareBlockEntityRenderer::getTexturedModelData);
        r.accept(SLUMBERSOCKET_EYE, SlumbersocketBlockEntityRenderer::getTexturedModelData);
    }

    private static ModelLayerLocation createMainLayer(String id) {
        return new ModelLayerLocation(MirthdewEncore.id(id), "main");
    }

    private static ModelLayerLocation createLayer(String id, String layer) {
        return new ModelLayerLocation(MirthdewEncore.id(id), layer);
    }
}
