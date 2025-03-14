package phanastrae.mirthdew_encore.duck;

import phanastrae.mirthdew_encore.block.entity.DoorMarkerBlockEntity;
import phanastrae.mirthdew_encore.block.entity.LychsealMarkerBlockEntity;
import phanastrae.mirthdew_encore.entity.MirthdewEncorePlayerEntityAttachment;

public interface PlayerDuckInterface {
    MirthdewEncorePlayerEntityAttachment mirthdew_encore$getAttachment();

    default void mirthdew_encore$openDoorMarkerBlock(DoorMarkerBlockEntity doorMarkerBlockEntity) {
        // empty
    }

    default void mirthdew_encore$openLychsealMarkerBlock(LychsealMarkerBlockEntity lychsealMarkerBlockEntity) {
        // empty
    }
}
