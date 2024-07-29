package phanastrae.mirthdew_encore.item;

import phanastrae.mirthdew_encore.component.type.LocationComponent;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT;

public class SlumberingEyeItem extends Item {
    public SlumberingEyeItem(Properties settings) {
        super(settings);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        if (stack.has(LOCATION_COMPONENT)) {
            return true;
        } else {
            return super.isFoil(stack);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if(stack.has(LOCATION_COMPONENT)) {
            return super.use(world, user, hand);
        } else {
            if(!world.isClientSide()) {
                Vec3 pos = user.isShiftKeyDown() ? user.position() : user.blockPosition().getBottomCenter();
                stack.set(LOCATION_COMPONENT, LocationComponent.fromPosAndWorld(pos, user.level()));
            }

            return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        LocationComponent locationComponent = stack.get(LOCATION_COMPONENT);
        if(locationComponent != null) {
            tooltip.add(Component.translatable("item.mirthdew_encore.slumbering_eye.bound").copy().withStyle(ChatFormatting.LIGHT_PURPLE));

            Component xyz = Component.translatable("item.mirthdew_encore.slumbering_eye.bound.to.xyz", locationComponent.x(), locationComponent.y(), locationComponent.z()).withStyle(ChatFormatting.WHITE);
            Component dimension = Component.translationArg(locationComponent.dimensionId()).copy().withStyle(ChatFormatting.WHITE);

            tooltip.add(Component.translatable("item.mirthdew_encore.slumbering_eye.bound.to", xyz).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("item.mirthdew_encore.slumbering_eye.bound.in", dimension).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("item.mirthdew_encore.slumbering_eye.unbound").copy().withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, context, tooltip, type);
    }
}
