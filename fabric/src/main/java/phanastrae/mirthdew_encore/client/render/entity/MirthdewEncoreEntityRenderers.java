package phanastrae.mirthdew_encore.client.render.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;

public class MirthdewEncoreEntityRenderers {

    public static void init() {
        register(MirthdewEncoreEntityTypes.DREAM_SPECK, DreamspeckEntityRenderer::new);
    }

    private static <E extends Entity> void register(EntityType<E> entityType, EntityRendererProvider<E> entityRendererFactory) {
        EntityRendererRegistry.register(entityType, entityRendererFactory);
    }
}
