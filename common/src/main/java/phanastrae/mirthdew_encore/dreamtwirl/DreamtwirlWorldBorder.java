package phanastrae.mirthdew_encore.dreamtwirl;

import net.minecraft.world.level.border.WorldBorder;
import phanastrae.mirthdew_encore.dreamtwirl.stage.play.DreamtwirlBorder;
import phanastrae.mirthdew_encore.util.RegionPos;

public class DreamtwirlWorldBorder extends WorldBorder {

    private final DreamtwirlLevelAttachment dreamtwirlWorldAttachment;

    public DreamtwirlWorldBorder(DreamtwirlLevelAttachment dreamtwirlWorldAttachment) {
        this.dreamtwirlWorldAttachment = dreamtwirlWorldAttachment;
    }

    @Override
    public boolean isWithinBounds(double x, double z, double margin) {
        if(super.isWithinBounds(x, z, margin)) {
            RegionPos regionPos = RegionPos.fromWorldCoordsDoubles(x, z);
            DreamtwirlBorder dreamtwirlBorder = this.dreamtwirlWorldAttachment.getDreamtwirlBorder(regionPos);

            if(dreamtwirlBorder != null) {
                return dreamtwirlBorder.contains(x, z, margin);
            } else {
                return true;
            }
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
    public void lerpSizeBetween(double fromSize, double toSize, long time) {
        // empty
    }

    @Override
    public void setAbsoluteMaxSize(int maxRadius) {
        // empty
    }

    @Override
    public void setDamageSafeZone(double safeZone) {
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
