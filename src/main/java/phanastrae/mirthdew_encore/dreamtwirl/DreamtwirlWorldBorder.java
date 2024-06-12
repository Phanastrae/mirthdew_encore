package phanastrae.mirthdew_encore.dreamtwirl;

import net.minecraft.world.border.WorldBorder;
import phanastrae.mirthdew_encore.util.RegionPos;

public class DreamtwirlWorldBorder extends WorldBorder {

    private final DreamtwirlWorldAttachment dreamtwirlWorldAttachment;

    public DreamtwirlWorldBorder(DreamtwirlWorldAttachment dreamtwirlWorldAttachment) {
        this.dreamtwirlWorldAttachment = dreamtwirlWorldAttachment;
    }

    @Override
    public boolean contains(double x, double z, double margin) {
        if(super.contains(x, z, margin)) {
            RegionPos regionPos = RegionPos.fromWorldCoordsDoubles(x, z);
            DreamtwirlBorder dreamtwirlBorder = this.dreamtwirlWorldAttachment.getDreamtwirlBorder(regionPos);

            return dreamtwirlBorder.contains(x, z, margin);
        } else {
            return false;
        }
    }

    // Below we set many of WorldBorder's methods to empty, as we want to keep the real WorldBorder at its default settings

    @Override
    public void setCenter(double x, double z) {
        // empty
    }

    @Override
    public void setSize(double size) {
        // empty
    }

    @Override
    public void interpolateSize(double fromSize, double toSize, long time) {
        // empty
    }

    @Override
    public void setMaxRadius(int maxRadius) {
        // empty
    }

    @Override
    public void setSafeZone(double safeZone) {
        // empty
    }

    @Override
    public void setDamagePerBlock(double damagePerBlock) {
        // empty
    }

    @Override
    public void setWarningTime(int warningTime) {
        // empty
    }

    @Override
    public void setWarningBlocks(int warningBlocks) {
        // empty
    }
}
