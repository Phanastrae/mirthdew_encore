package phanastrae.mirthdew_encore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.world.level.levelgen.structure.pools.ListPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

@Mixin(ListPoolElement.class)
public interface ListPoolElementAccessor {
    @Accessor
    List<StructurePoolElement> getElements();
}
