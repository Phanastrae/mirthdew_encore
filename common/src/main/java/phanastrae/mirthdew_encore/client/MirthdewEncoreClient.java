package phanastrae.mirthdew_encore.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import phanastrae.mirthdew_encore.card_spell.PlayerEntityMirthData;
import phanastrae.mirthdew_encore.client.render.block.MirthdewEncoreBlockRenderLayers;
import phanastrae.mirthdew_encore.client.render.block.entity.MirthdewEncoreBlockEntityRendererFactories;
import phanastrae.mirthdew_encore.client.render.world.DreamtwirlBorderRenderer;
import phanastrae.mirthdew_encore.client.render.world.MirthdewEncoreDimensionEffects;
import phanastrae.mirthdew_encore.item.SpellCardAbstractItem;

public class MirthdewEncoreClient {

    public static void init() {
        MirthdewEncoreBlockRenderLayers.init();
        MirthdewEncoreBlockEntityRendererFactories.init();

        MirthdewEncoreDimensionEffects.getInstance().init();
    }

    public static void onClientStop(Minecraft minecraftClient) {
        MirthdewEncoreDimensionEffects.getInstance().close();
        DreamtwirlBorderRenderer.close();
    }

    public static void renderMirthOverlay(Minecraft client, GuiGraphics guiGraphics) {
        Font font = client.font;

        MultiPlayerGameMode gameMode = client.gameMode;
        if(gameMode == null) return;
        if(gameMode.getPlayerMode() == GameType.SPECTATOR) return;

        Entity cameraEntity = client.getCameraEntity();
        if(!(cameraEntity instanceof Player player)) return;

        ItemStack stack = player.getMainHandItem();
        if(stack.isEmpty()) return;

        if(stack.getItem() instanceof SpellCardAbstractItem) {
            long mirth = PlayerEntityMirthData.fromPlayer(player).getMirth();

            ChatFormatting color = mirth == 0 ? ChatFormatting.RED : ChatFormatting.LIGHT_PURPLE;
            MutableComponent mutableText = Component.translatable("gui.mirthdew_encore.mirth", Component.nullToEmpty(String.valueOf(mirth)).copy().withStyle(color)).withStyle(ChatFormatting.AQUA);

            int width = font.width(mutableText);
            int x = (guiGraphics.guiWidth() - width) / 2;
            int y = guiGraphics.guiHeight() - 59;
            if (!gameMode.canHurtPlayer()) {
                y += 14;
            }

            y -= 14;

            guiGraphics.drawStringWithBackdrop(font, mutableText, x, y, width, FastColor.ARGB32.color(255, -1));
        }
    }
}
