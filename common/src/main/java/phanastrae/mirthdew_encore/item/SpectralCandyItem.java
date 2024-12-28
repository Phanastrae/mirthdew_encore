package phanastrae.mirthdew_encore.item;

import phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;

public class SpectralCandyItem extends Item {
    public static final FoodProperties FOOD_COMPONENT = new FoodProperties.Builder().nutrition(1).saturationModifier(4F).alwaysEdible().build();

    public SpectralCandyItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        // TODO make this a component
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("item.mirthdew_encore.spectral_candy.when_full").withStyle(ChatFormatting.GRAY));
        List<MobEffectInstance> list = List.of(new MobEffectInstance(MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY, 3000, 2));
        PotionContents.addPotionTooltip(list, tooltip::add, 1.0F, context.tickRate());
    }
}
