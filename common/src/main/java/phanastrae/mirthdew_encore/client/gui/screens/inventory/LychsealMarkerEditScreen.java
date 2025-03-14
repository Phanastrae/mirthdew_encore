package phanastrae.mirthdew_encore.client.gui.screens.inventory;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import phanastrae.mirthdew_encore.block.entity.LychsealMarkerBlockEntity;
import phanastrae.mirthdew_encore.client.services.XPlatClientInterface;
import phanastrae.mirthdew_encore.network.packet.SetLychsealMarkerBlockPayload;

public class LychsealMarkerEditScreen extends Screen {
    private static final Component LYCHSEAL_NAME_LABEL = Component.translatable("mirthdew_encore.door_marker.lychseal_name_label");

    private final LychsealMarkerBlockEntity lychsealMarkerBlockEntity;

    private EditBox lychsealNameEdit;
    private Button doneButton;

    public LychsealMarkerEditScreen(LychsealMarkerBlockEntity lychsealMarkerBlockEntity) {
        super(GameNarrator.NO_TITLE);
        this.lychsealMarkerBlockEntity = lychsealMarkerBlockEntity;
    }

    private void onDone() {
        this.sendToServer();
        this.minecraft.setScreen(null);
    }

    private void sendToServer() {
        XPlatClientInterface.INSTANCE.sendPayload(new SetLychsealMarkerBlockPayload(
                this.lychsealMarkerBlockEntity.getBlockPos(),
                this.lychsealNameEdit.getValue()
        ));
    }

    private void onCancel() {
        this.minecraft.setScreen(null);
    }

    @Override
    public void onClose() {
        this.onCancel();
    }

    @Override
    protected void init() {
        this.lychsealNameEdit = new EditBox(this.font, this.width / 2 - 153, 55, 300, 20, LYCHSEAL_NAME_LABEL);
        this.lychsealNameEdit.setMaxLength(128);
        this.lychsealNameEdit.setValue(this.lychsealMarkerBlockEntity.getLychsealName());
        this.lychsealNameEdit.setResponder(p_98981_ -> this.updateValidity());
        this.addWidget(this.lychsealNameEdit);

        this.doneButton = this.addRenderableWidget(
                Button.builder(CommonComponents.GUI_DONE, p_98973_ -> this.onDone()).bounds(this.width / 2 - 4 - 150, 210, 150, 20).build()
        );
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, p_98964_ -> this.onCancel()).bounds(this.width / 2 + 4, 210, 150, 20).build());

        this.updateValidity();
    }

    private void updateValidity() {
        boolean isValid = true;
        this.doneButton.active = isValid;
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.lychsealNameEdit);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String lychsealTargetName = this.lychsealNameEdit.getValue();

        this.init(minecraft, width, height);

        this.lychsealNameEdit.setValue(lychsealTargetName);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (!this.doneButton.active || keyCode != 257 && keyCode != 335) {
            return false;
        } else {
            this.onDone();
            return true;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawString(this.font, LYCHSEAL_NAME_LABEL, this.width / 2 - 153, 45, 10526880);
        this.lychsealNameEdit.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
