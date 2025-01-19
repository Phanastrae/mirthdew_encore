package phanastrae.mirthdew_encore.duck;

import phanastrae.mirthdew_encore.block.entity.DoorMarkerBlockEntity;
import phanastrae.mirthdew_encore.entity.MirthdewEncorePlayerEntityAttachment;

public interface PlayerEntityDuckInterface {
    MirthdewEncorePlayerEntityAttachment mirthdew_encore$getAttachment();
    void mirthdew_encore$openDoorMarkerBlock(DoorMarkerBlockEntity doorMarkerBlockEntity);
}
