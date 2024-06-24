package phanastrae.mirthdew_encore.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects;

import java.util.List;

public class MirthdewVialItem extends Item {
    public static final FoodComponent FOOD_COMPONENT = new FoodComponent.Builder().nutrition(2).saturationModifier(0.5F).build();

    public MirthdewVialItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (!world.isClient) {
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_WITCH_DRINK, user.getSoundCategory(), 1.0F, 1.0F);
            Integer level = stack.getOrDefault(MirthdewEncoreDataComponentTypes.MIRTHDEW_VIAL_AMPLIFIER, 0);
            user.addStatusEffect(new StatusEffectInstance(MirthdewEncoreStatusEffects.MIRTHFUL_ENTRY, 600 * (2 * level + 1), level, false, false, true));
        }

        stack.decrementUnlessCreative(1, user);
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        Integer level = stack.getOrDefault(MirthdewEncoreDataComponentTypes.MIRTHDEW_VIAL_AMPLIFIER, 0);
        List<StatusEffectInstance> list = List.of(new StatusEffectInstance(MirthdewEncoreStatusEffects.MIRTHFUL_ENTRY, 600 * (2 * level + 1), level, false, false, true));
        PotionContentsComponent.buildTooltip(list, tooltip::add, 1.0F, context.getUpdateTickRate());
    }
}
