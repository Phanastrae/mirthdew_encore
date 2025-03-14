package phanastrae.mirthdew_encore.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import phanastrae.mirthdew_encore.component.type.LinkedAcheruneComponent;
import phanastrae.mirthdew_encore.component.type.LinkedDreamtwirlComponent;
import phanastrae.mirthdew_encore.component.type.LocationComponent;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.Acherune;

import java.util.List;

import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.*;

public class SlumberingEyeItem extends Item {
    public SlumberingEyeItem(Properties settings) {
        super(settings);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        if (eyeHasDestination(stack)) {
            return true;
        } else {
            return super.isFoil(stack);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if(eyeHasDestination(stack)) {
            return super.use(world, user, hand);
        } else {
            if(!world.isClientSide()) {
                Vec3 pos = user.isShiftKeyDown() ? user.position() : user.blockPosition().getBottomCenter();
                stack.set(LOCATION_COMPONENT, LocationComponent.fromPosAndLevel(pos, user.level()));
            }

            return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();

        if(!eyeHasDestination(stack)) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();

            DreamtwirlStage stage = DreamtwirlStageManager.getStage(level, pos);
            if(stage != null) {
                Acherune acherune = stage.getStageAcherunes().getAcherune(pos);
                if(acherune != null) {
                    stack.set(LINKED_ACHERUNE, LinkedAcheruneComponent.fromAcheruneAndStage(stage, acherune));
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }

        return super.useOn(context);
    }

    public static boolean eyeHasDestination(ItemStack itemStack) {
        return itemStack.has(LOCATION_COMPONENT) || itemStack.has(LINKED_DREAMTWIRL) || itemStack.has(LINKED_ACHERUNE);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            if(other.isEmpty() || !other.is(MirthdewEncoreItems.OCULAR_SOPORSTEW)) {
                return false;
            } else if (stack.is(MirthdewEncoreItems.SLUMBERING_EYE) && stack.getDamageValue() == 0) {
                return false;
            } else {
                other.shrink(1);
                this.playEatSound(player);

                ItemStack newStack = repairEye(stack);
                slot.set(newStack);
                return true;
            }
        } else {
            return false;
        }
    }

    private void playEatSound(Entity entity) {
        entity.playSound(SoundEvents.ENDERMAN_SCREAM, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    public static ItemStack damageEye(ItemStack stack) {
        if(stack.is(MirthdewEncoreItems.SLUMBERING_EYE)) {
            boolean breaks = stack.getDamageValue() + 1 >= stack.getMaxDamage();

            ItemStack newStack;
            if(breaks) {
                newStack = MirthdewEncoreItems.SLEEPY_EYE.getDefaultInstance();
                newStack.applyComponentsAndValidate(stack.getComponentsPatch());

                if(newStack.has(DataComponents.DAMAGE)) {
                    newStack.remove(DataComponents.DAMAGE);
                }
                if(newStack.has(DataComponents.MAX_DAMAGE)) {
                    newStack.remove(DataComponents.MAX_DAMAGE);
                }
            } else {
                newStack = stack.copy();

                newStack.setDamageValue(newStack.getDamageValue() + 1);
            }
            return newStack;
        } else {
            return stack.copy();
        }
    }

    public static ItemStack repairEye(ItemStack stack) {
        if(stack.is(MirthdewEncoreItems.SLEEPY_EYE)) {
            ItemStack newStack = MirthdewEncoreItems.SLUMBERING_EYE.getDefaultInstance();
            newStack.applyComponentsAndValidate(stack.getComponentsPatch());

            newStack.setDamageValue(newStack.getMaxDamage() - 1);
            return newStack;
        } else if(stack.is(MirthdewEncoreItems.SLUMBERING_EYE)) {
            ItemStack newStack = stack.copy();

            if(newStack.getDamageValue() != 0) {
                newStack.setDamageValue(stack.getDamageValue() - 1);
            }
            return newStack;
        } else {
            return stack.copy();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        LocationComponent locationComponent = stack.get(LOCATION_COMPONENT);
        LinkedDreamtwirlComponent linkedDreamtwirl = stack.get(LINKED_DREAMTWIRL);
        LinkedAcheruneComponent linkedAcherune = stack.get(LINKED_ACHERUNE);
        if(locationComponent != null) {
            tooltip.add(Component.translatable("item.mirthdew_encore.slumbering_eye.bound").copy().withStyle(ChatFormatting.LIGHT_PURPLE));

            Component xyz = Component.translatable("item.mirthdew_encore.slumbering_eye.bound.to.xyz", locationComponent.x(), locationComponent.y(), locationComponent.z()).withStyle(ChatFormatting.WHITE);
            tooltip.add(Component.translatable("item.mirthdew_encore.slumbering_eye.bound.to", xyz).withStyle(ChatFormatting.GRAY));

            Component dimension = Component.translationArg(locationComponent.dimensionId()).copy().withStyle(ChatFormatting.WHITE);
            tooltip.add(Component.translatable("item.mirthdew_encore.slumbering_eye.bound.in", dimension).withStyle(ChatFormatting.GRAY));
        } else if(linkedDreamtwirl != null) {
            tooltip.add(Component.translatable("item.mirthdew_encore.slumbering_eye.bound").copy().withStyle(ChatFormatting.LIGHT_PURPLE));

            Component stage = Component.translatable("item.mirthdew_encore.slumbering_eye.bound.to.dreamtwirl_stage").withStyle(ChatFormatting.WHITE);
            tooltip.add(Component.translatable("item.mirthdew_encore.slumbering_eye.bound.to", stage).withStyle(ChatFormatting.GRAY));

            Component dimension = Component.translationArg(linkedDreamtwirl.dimensionId()).copy().withStyle(ChatFormatting.WHITE);
            tooltip.add(Component.translatable("item.mirthdew_encore.slumbering_eye.bound.in", dimension).withStyle(ChatFormatting.GRAY));
        } else if(linkedAcherune != null) {
            tooltip.add(Component.translatable("item.mirthdew_encore.slumbering_eye.bound").copy().withStyle(ChatFormatting.LIGHT_PURPLE));

            Component acherune = Component.translatable("item.mirthdew_encore.slumbering_eye.bound.to.acherune").withStyle(ChatFormatting.WHITE);
            tooltip.add(Component.translatable("item.mirthdew_encore.slumbering_eye.bound.to", acherune).withStyle(ChatFormatting.GRAY));

            Component dimension = Component.translationArg(linkedAcherune.dimensionId()).copy().withStyle(ChatFormatting.WHITE);
            tooltip.add(Component.translatable("item.mirthdew_encore.slumbering_eye.bound.in", dimension).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("item.mirthdew_encore.slumbering_eye.unbound").copy().withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, context, tooltip, type);
    }
}
