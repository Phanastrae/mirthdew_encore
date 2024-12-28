package phanastrae.mirthdew_encore.fabric.data;

import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import phanastrae.mirthdew_encore.MirthdewEncore;

import java.util.Optional;

public class MirthdewEncoreModelTemplates {
    public static final ModelTemplate LATTICE_CAP = create("template_lattice_cap", "_cap", TextureSlot.PARTICLE, TextureSlot.PANE, TextureSlot.EDGE);
    public static final ModelTemplate LATTICE_CAP_ALT = create("template_lattice_cap_alt", "_cap_alt", TextureSlot.PARTICLE, TextureSlot.PANE, TextureSlot.EDGE);
    public static final ModelTemplate LATTICE_POST = create("template_lattice_post", "_post", TextureSlot.PARTICLE, TextureSlot.PANE);
    public static final ModelTemplate LATTICE_POST_ENDS = create("template_lattice_post_ends", "_post_ends", TextureSlot.PARTICLE, TextureSlot.EDGE);
    public static final ModelTemplate LATTICE_SIDE = create("template_lattice_side", "_side", TextureSlot.PARTICLE, TextureSlot.PANE, TextureSlot.EDGE);
    public static final ModelTemplate LATTICE_SIDE_ALT = create("template_lattice_side_alt", "_side_alt", TextureSlot.PARTICLE, TextureSlot.PANE, TextureSlot.EDGE);


    private static ModelTemplate create(String blockModelLocation, String suffix, TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.of(MirthdewEncore.id("block/" + blockModelLocation)), Optional.of(suffix), requiredSlots);
    }
}
