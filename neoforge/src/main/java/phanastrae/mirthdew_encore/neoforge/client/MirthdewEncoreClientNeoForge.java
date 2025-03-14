package phanastrae.mirthdew_encore.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.client.MirthdewEncoreClient;
import phanastrae.mirthdew_encore.client.particle.MirthdewEncoreParticles;
import phanastrae.mirthdew_encore.client.render.entity.MirthdewEncoreEntityRenderers;
import phanastrae.mirthdew_encore.client.render.entity.model.MirthdewEncoreEntityModelLayers;
import phanastrae.mirthdew_encore.client.render.shader.MirthdewEncoreShaders;
import phanastrae.mirthdew_encore.client.render.world.DreamtwirlBorderRenderer;
import phanastrae.mirthdew_encore.client.render.world.MirthdewEncoreDimensionEffects;
import phanastrae.mirthdew_encore.neoforge.client.fluid.MirthdewEncoreFluidTypeExtensions;
import phanastrae.mirthdew_encore.world.dimension.MirthdewEncoreDimensions;

import java.io.IOException;
import java.io.UncheckedIOException;

import static net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage.AFTER_SKY;

@Mod(value = MirthdewEncore.MOD_ID, dist = Dist.CLIENT)
public class MirthdewEncoreClientNeoForge {

    public MirthdewEncoreClientNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::onClientInit);

        // entity renderers
        modEventBus.addListener(this::registerEntityRenderers);

        // entity model layers
        modEventBus.addListener(this::registerEntityModelLayers);

        // particles
        modEventBus.addListener(this::registerParticleProviders);

        // register shaders
        modEventBus.addListener(this::registerShaders);

        // register dimension effects
        modEventBus.addListener(this::registerDimensionEffects);

        // register client extensions
        modEventBus.addListener(this::registerClientExtensions);



        // client shutdown
        NeoForge.EVENT_BUS.addListener(this::onGameShutdown);

        // render dreamtwirl sky
        NeoForge.EVENT_BUS.addListener(this::renderLevel);

        // render dreamtwirl border
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::renderLevelLowestPriority);

        NeoForge.EVENT_BUS.addListener(this::renderGuiLayers);
    }

    public void onClientInit(FMLClientSetupEvent event) {
        // everything here needs to be multithread safe
        event.enqueueWork(MirthdewEncoreClient::init);
    }

    public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        MirthdewEncoreEntityRenderers.init(event::registerEntityRenderer);
    }

    public void registerEntityModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        MirthdewEncoreEntityModelLayers.init(event::registerLayerDefinition);
    }

    public void registerParticleProviders(RegisterParticleProvidersEvent event) {
        MirthdewEncoreParticles.init(new MirthdewEncoreParticles.ClientParticleRegistrar() {
            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
                event.registerSpecial(type, provider);
            }
            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider.Sprite<T> provider) {
                event.registerSprite(type, provider);
            }
            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, MirthdewEncoreParticles.ParticleRegistration<T> registration) {
                event.registerSpriteSet(type, registration::create);
            }
        });
    }

    public void registerShaders(RegisterShadersEvent event) {
        try {
            MirthdewEncoreShaders.registerShaders((id, vertexFormat, callback) -> event.registerShader(new ShaderInstance(event.getResourceProvider(), id, vertexFormat), callback));
        } catch (IOException e) {
            // TODO check if this is fine
            throw new UncheckedIOException(e);
        }
    }

    public void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(MirthdewEncoreDimensions.DREAMTWIRL_ID, MirthdewEncoreDimensionEffects.getDreamtwirlDimensionEffects());
    }

    public void registerClientExtensions(RegisterClientExtensionsEvent event) {
        MirthdewEncoreFluidTypeExtensions.init(event::registerFluidType);
    }

    public void onGameShutdown(GameShuttingDownEvent event) {
        MirthdewEncoreClient.onClientStop(Minecraft.getInstance());
    }

    public void renderLevel(RenderLevelStageEvent event) {
        ClientLevel level = Minecraft.getInstance().level;
        if(level == null) return;

        if(event.getStage().equals(AFTER_SKY)) {
            if(MirthdewEncoreDimensions.DREAMTWIRL_WORLD.equals(level.dimension())) {
                MirthdewEncoreDimensionEffects.renderSky(event.getModelViewMatrix(), event.getPartialTick(), Minecraft.getInstance().gameRenderer, event.getCamera(), level, event.getProjectionMatrix());
            }
        }
    }

    public void renderLevelLowestPriority(RenderLevelStageEvent event) {
        ClientLevel level = Minecraft.getInstance().level;
        if(level == null) return;

        if(event.getStage().equals(AFTER_SKY)) {
            // TODO maybe get level from LevelRenderer instead?
            DreamtwirlBorderRenderer.render(event.getModelViewMatrix(), level, event.getCamera());
        }
    }

    public void renderGuiLayers(RenderGuiLayerEvent.Post event) {
        boolean hideGui = Minecraft.getInstance().options.hideGui;
        if(!hideGui) {
            if (event.getName().equals(VanillaGuiLayers.SELECTED_ITEM_NAME)) {
                MirthdewEncoreClient.renderMirthOverlay(Minecraft.getInstance(), event.getGuiGraphics());
            }
        }
    }
}
