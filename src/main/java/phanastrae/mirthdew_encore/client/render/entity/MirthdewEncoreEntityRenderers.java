package phanastrae.mirthdew_encore.client.render.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;

public class MirthdewEncoreEntityRenderers {

    public static void init() {
        register(MirthdewEncoreEntityTypes.DREAM_SPECK, DreamspeckEntityRenderer::new);
    }

    private static <E extends Entity> void register(EntityType<E> entityType, EntityRendererFactory<E> entityRendererFactory) {
        EntityRendererRegistry.register(entityType, entityRendererFactory);
    }
}
