package phanastrae.mirthdew_encore.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityAttachment;

public class WarpRendering {

    public static int getWarpColor(MirthdewEncoreEntityAttachment entityAttachment, boolean translucent, float partialTicks) {
        float startProgress = entityAttachment.getWarpStartProgress(partialTicks);
        float endProgress = entityAttachment.getWarpEndProgress(partialTicks);

        float a = (translucent ? 38/255F : 1F) * (1F - startProgress * 0.6F - endProgress * 0.4F);
        float r = 1 - startProgress * 0.6F + endProgress * 0.3F;
        float g = 1 - startProgress * 0.7F + endProgress * 0.3F;
        float b = 1 - endProgress * 0.4F;

        return FastColor.ARGB32.colorFromFloat(a, r, g, b);
    }

    public static int getLightColor(MirthdewEncoreEntityAttachment entityAttachment, int packedLight, float partialTicks) {
        int blockLight = LightTexture.block(packedLight);
        int skyLight = LightTexture.sky(packedLight);
        blockLight = Math.min(Mth.floor(blockLight + entityAttachment.getWarpStartProgress(partialTicks) * 15), 15);
        return LightTexture.pack(blockLight, skyLight);
    }

    public static void renderWarpScreenEffect(GuiGraphics guiGraphics, LocalPlayer player, float partialTicks) {
        MirthdewEncoreEntityAttachment meea = MirthdewEncoreEntityAttachment.fromEntity(player);

        float startProgress = meea.getWarpStartProgress(partialTicks);
        float endProgress = meea.getWarpEndProgress(partialTicks);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        guiGraphics.setColor(0.2F, 0.4F, 0.4F, 1F);
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        RenderSystem.setShaderTexture(0, MirthdewEncore.id("textures/environment/dreamtwirl_nova_swirl.png"));
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(guiGraphics.guiWidth() / 2F, guiGraphics.guiHeight() / 2F, 0F);
        float scale = Math.max(guiGraphics.guiWidth(), guiGraphics.guiHeight()) / 2F;
        guiGraphics.pose().scale(scale, scale, 1F);

        int SWIRLS = 15;
        for(int i = 0; i < SWIRLS; i++) {
            float p = i / (float)SWIRLS;

            guiGraphics.pose().pushPose();

            float size = 0.1F * (1 + i + 0.1F * i * i) * (2.5F - startProgress - endProgress);
            guiGraphics.pose().scale(size, size, 1);
            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(360 * ((meea.getWarpTicks() + partialTicks) / (float)MirthdewEncoreEntityAttachment.WARP_TIME) * (1 + p * 3) + i * 413));

            Matrix4f matrix4f = guiGraphics.pose().last().pose();

            float a = startProgress * 0.7F + endProgress * 0.3F;

            float r = (1 - p) * 0.5F * a;
            float g = p * a;
            float b = a;
            int color = FastColor.ARGB32.colorFromFloat(1, r, g, b);

            bufferbuilder.addVertex(matrix4f, -1F, -1F, -90F).setUv(0F, 0F).setColor(color);
            bufferbuilder.addVertex(matrix4f, -1F, 1F, -90F).setUv(0F, 1F).setColor(color);
            bufferbuilder.addVertex(matrix4f, 1F, 1F, -90F).setUv(1F, 1F).setColor(color);
            bufferbuilder.addVertex(matrix4f, 1F, -1F, -90F).setUv(1F, 0F).setColor(color);

            guiGraphics.pose().popPose();
        }
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());

        guiGraphics.pose().popPose();

        RenderSystem.defaultBlendFunc();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }
}
