package phanastrae.mirthdew_encore.client.gui.screens.inventory;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import phanastrae.mirthdew_encore.block.entity.DoorMarkerBlockEntity;
import phanastrae.mirthdew_encore.client.services.XPlatClientInterface;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;
import phanastrae.mirthdew_encore.network.packet.SetDoorMarkerBlockPayload;

public class DoorMarkerEditScreen extends Screen {
    private static final Component DOOR_TYPE_LABEL = Component.translatable("mirthdew_encore.door_marker.door_type_label");

    private final DoorMarkerBlockEntity doorMarkerEntity;

    private CycleButton<RoomDoor.DoorType> doorTypeButton;
    private Button doneButton;

    private RoomDoor.DoorType doorType;

    public DoorMarkerEditScreen(DoorMarkerBlockEntity doorMarkerBlockEntity) {
        super(GameNarrator.NO_TITLE);
        this.doorMarkerEntity = doorMarkerBlockEntity;
    }

    private void onDone() {
        this.sendToServer();
        this.minecraft.setScreen(null);
    }

    private void sendToServer() {
        XPlatClientInterface.INSTANCE.sendPayload(new SetDoorMarkerBlockPayload(
                this.doorMarkerEntity.getBlockPos(),
                this.doorType
        ));
        // TODO add more data to packet if needed
        /*
        this.minecraft
                .getConnection()
                .send(
                        new ServerboundSetJigsawBlockPacket(
                                this.jigsawEntity.getBlockPos(),
                                ResourceLocation.parse(this.nameEdit.getValue()),
                                ResourceLocation.parse(this.targetEdit.getValue()),
                                ResourceLocation.parse(this.poolEdit.getValue()),
                                this.finalStateEdit.getValue(),
                                this.joint,
                                this.parseAsInt(this.selectionPriorityEdit.getValue()),
                                this.parseAsInt(this.placementPriorityEdit.getValue())
                        )
                );
         */
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
        // TODO add other buttons
        this.doorType = this.doorMarkerEntity.getDoorType();
        this.doorTypeButton = this.addRenderableWidget(
                CycleButton.builder(RoomDoor.DoorType::getTranslatedName)
                        .withValues(RoomDoor.DoorType.values())
                        .withInitialValue(this.doorType)
                        .displayOnlyValue()
                        .create(this.width / 2 + 54, 160, 100, 20, DOOR_TYPE_LABEL, (button, type) -> this.doorType = type)
        );

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
        this.setInitialFocus(this.doorTypeButton);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        // TODO store info
        RoomDoor.DoorType dt = this.doorType;
        this.init(minecraft, width, height);
        // TODO restore info
        this.doorType = dt;
        this.doorTypeButton.setValue(dt);
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
        // TODO add string labels and boxes
        guiGraphics.drawString(this.font, DOOR_TYPE_LABEL, this.width / 2 + 53, 150, 10526880);
    }
}
