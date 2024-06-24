package phanastrae.mirthdew_encore.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects;

import java.util.List;

public class SpectralCandyItem extends Item {
    public static final FoodComponent FOOD_COMPONENT = new FoodComponent.Builder().nutrition(1).saturationModifier(4F).alwaysEdible().build();

    public SpectralCandyItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.mirthdew_encore.spectral_candy.when_full").formatted(Formatting.GRAY));
        List<StatusEffectInstance> list = List.of(new StatusEffectInstance(MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY, 3000, 2));
        PotionContentsComponent.buildTooltip(list, tooltip::add, 1.0F, context.getUpdateTickRate());
    }
}
