package phanastrae.mirthdew_encore.item;

import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects;

import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

public class MirthdewVialItem extends Item {
    public static final FoodProperties FOOD_COMPONENT = new FoodProperties.Builder().nutrition(2).saturationModifier(0.5F).build();

    public MirthdewVialItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (user instanceof ServerPlayer serverPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!world.isClientSide) {
            world.playSound(null, user.blockPosition(), SoundEvents.WITCH_DRINK, user.getSoundSource(), 1.0F, 1.0F);
            Integer level = stack.getOrDefault(MirthdewEncoreDataComponentTypes.MIRTHDEW_VIAL_AMPLIFIER, 0);
            user.addEffect(new MobEffectInstance(MirthdewEncoreStatusEffects.MIRTHFUL_ENTRY, 600 * (2 * level + 1), level));
        }

        stack.consume(1, user);
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(world, user, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        Integer level = stack.getOrDefault(MirthdewEncoreDataComponentTypes.MIRTHDEW_VIAL_AMPLIFIER, 0);
        List<MobEffectInstance> list = List.of(new MobEffectInstance(MirthdewEncoreStatusEffects.MIRTHFUL_ENTRY, 600 * (2 * level + 1), level));
        PotionContents.addPotionTooltip(list, tooltip::add, 1.0F, context.tickRate());
    }
}
