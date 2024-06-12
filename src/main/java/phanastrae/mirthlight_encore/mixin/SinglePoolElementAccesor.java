package phanastrae.mirthlight_encore.mixin;

import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.SinglePoolElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SinglePoolElement.class)
public interface SinglePoolElementAccesor {
    @Invoker
    StructureTemplate invokeGetStructure(StructureTemplateManager structureTemplateManager);
}
