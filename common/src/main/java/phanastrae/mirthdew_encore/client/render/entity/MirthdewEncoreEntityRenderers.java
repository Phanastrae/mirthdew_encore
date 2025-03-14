package phanastrae.mirthdew_encore.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;

public class MirthdewEncoreEntityRenderers {

    public interface EntityRendererAcceptor {
        <T extends Entity> void accept(EntityType<? extends T> type, EntityRendererProvider<T> entityRendererProvider);
    }

    public static void init(EntityRendererAcceptor r) {
        r.accept(MirthdewEncoreEntityTypes.DREAMSPECK, DreamspeckEntityRenderer::new);
    }
}
